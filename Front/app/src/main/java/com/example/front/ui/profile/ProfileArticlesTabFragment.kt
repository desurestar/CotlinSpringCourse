package com.example.front.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.repository.ArticleRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.data.repository.ResearchTeamRepository
import com.example.front.databinding.FragmentProfileTabBinding
import com.example.front.ui.articles.ArticleAdapter
import com.example.front.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProfileArticlesTabFragment : Fragment() {
    
    private var _binding: FragmentProfileTabBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ProfileViewModel by activityViewModels {
        val preferencesManager = PreferencesManager(requireContext())
        val apiService = RetrofitClient.getApiService { preferencesManager.getToken() }
        ProfileViewModelFactory(
            EmployeeRepository(apiService),
            ArticleRepository(apiService),
            ResearchTeamRepository(apiService)
        )
    }
    
    private var employeeId: Long = -1L
    private var isMainAuthor: Boolean = true
    
    private lateinit var articleAdapter: ArticleAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            employeeId = it.getLong(ARG_EMPLOYEE_ID, -1L)
            isMainAuthor = it.getBoolean(ARG_IS_MAIN_AUTHOR, true)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileTabBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupCreateButton()
        loadArticles()
        observeArticles()
        observeArticleDeletion()
    }
    
    private fun setupCreateButton() {
        // Only show create button for main author tab
        if (isMainAuthor) {
            binding.btnCreateArticle.visibility = View.VISIBLE
            binding.btnCreateArticle.setOnClickListener {
                val dialog = CreateArticleDialog.newInstance()
                dialog.setOnArticleCreatedListener {
                    // Refresh articles list
                    loadArticles()
                    Snackbar.make(
                        binding.root,
                        "Статья успешно создана",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                dialog.show(childFragmentManager, CreateArticleDialog.TAG)
            }
        } else {
            binding.btnCreateArticle.visibility = View.GONE
        }
    }
    
    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(
            onItemClick = { article ->
                val action = ProfileFragmentDirections
                    .actionProfileFragmentToArticleDetail(article.id)
                findNavController().navigate(action)
            },
            onDeleteClick = if (isMainAuthor) {
                { article -> showDeleteConfirmationDialog(article) }
            } else {
                null
            }
        )
        
        // Clear the container and add RecyclerView
        binding.contentContainer.removeAllViews()
        
        val recyclerView = androidx.recyclerview.widget.RecyclerView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        
        binding.contentContainer.addView(recyclerView)
    }
    
    private fun loadArticles() {
        if (employeeId != -1L) {
            if (isMainAuthor) {
                viewModel.refreshEmployeeArticlesAsAuthor(employeeId)
            } else {
                viewModel.refreshEmployeeArticlesAsCoauthor(employeeId)
            }
        }
    }
    
    private fun observeArticles() {
        val articlesFlow = if (isMainAuthor) {
            viewModel.myArticles
        } else {
            viewModel.coauthoredArticles
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            articlesFlow.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.contentContainer.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        
                        val articles = resource.data ?: emptyList()
                        if (articles.isEmpty()) {
                            binding.contentContainer.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                            binding.tvEmptyState.text = "Нет статей"
                        } else {
                            binding.contentContainer.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                            articleAdapter.submitList(articles)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.contentContainer.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.tvEmptyState.text = resource.message ?: "Ошибка загрузки статей"
                    }
                }
            }
        }
    }
    
    private fun showDeleteConfirmationDialog(article: com.example.front.data.model.Article) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Удалить статью?")
            .setMessage("Вы уверены, что хотите удалить статью \"${article.title}\"?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteArticle(article.id)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    
    private fun observeArticleDeletion() {
        viewModel.articleDeletionResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Could show a progress indicator
                }
                is Resource.Success -> {
                    Snackbar.make(
                        binding.root,
                        "Статья успешно удалена",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    // Reload articles list after successful deletion
                    loadArticles()
                }
                is Resource.Error -> {
                    Snackbar.make(
                        binding.root,
                        resource.message ?: "Ошибка удаления статьи",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val ARG_EMPLOYEE_ID = "employee_id"
        private const val ARG_IS_MAIN_AUTHOR = "is_main_author"
        
        fun newInstance(employeeId: Long, isMainAuthor: Boolean) = 
            ProfileArticlesTabFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_EMPLOYEE_ID, employeeId)
                    putBoolean(ARG_IS_MAIN_AUTHOR, isMainAuthor)
                }
            }
    }
}

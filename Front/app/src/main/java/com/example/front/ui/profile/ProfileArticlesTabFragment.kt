package com.example.front.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
        loadArticles()
        observeArticles()
    }
    
    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter { article ->
            val action = ProfileFragmentDirections
                .actionProfileFragmentToArticleDetail(article.id)
            findNavController().navigate(action)
        }
        
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
        if (isMainAuthor) {
            viewModel.getEmployeeArticlesAsAuthor(employeeId)
        } else {
            viewModel.getEmployeeArticlesAsCoauthor(employeeId)
        }
    }
    
    private fun observeArticles() {
        val articlesLiveData = if (isMainAuthor) {
            viewModel.myArticles
        } else {
            viewModel.coauthoredArticles
        }
        
        articlesLiveData.observe(viewLifecycleOwner) { resource ->
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

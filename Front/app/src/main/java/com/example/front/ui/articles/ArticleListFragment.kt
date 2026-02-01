package com.example.front.ui.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.repository.ArticleRepository
import com.example.front.databinding.FragmentArticleListBinding
import com.example.front.util.Resource
import com.google.android.material.snackbar.Snackbar

class ArticleListFragment : Fragment() {
    
    private var _binding: FragmentArticleListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ArticleViewModel by viewModels {
        val preferencesManager = PreferencesManager(requireContext())
        val apiService = RetrofitClient.getApiService { preferencesManager.getToken() }
        ArticleViewModelFactory(ArticleRepository(apiService))
    }
    
    private lateinit var articleAdapter: ArticleAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearchView()
        observeArticles()
        
        // Load articles on start
        viewModel.getAllArticles()
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(
            onItemClick = { article ->
                val action = ArticleListFragmentDirections
                    .actionArticleListToArticleDetail(article.id)
                findNavController().navigate(action)
            }
            // onDeleteClick не передаем, будет использоваться значение по умолчанию null
        )

        binding.rvArticles.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchArticles(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.getAllArticles()
                }
                return true
            }
        })
    }
    
    private fun observeArticles() {
        viewModel.articles.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvArticles.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    
                    val articles = resource.data ?: emptyList()
                    if (articles.isEmpty()) {
                        binding.rvArticles.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.VISIBLE
                    } else {
                        binding.rvArticles.visibility = View.VISIBLE
                        binding.tvEmptyState.visibility = View.GONE
                        articleAdapter.submitList(articles)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvArticles.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.VISIBLE
                    
                    Snackbar.make(
                        binding.root,
                        resource.message ?: "Ошибка загрузки статей",
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
}

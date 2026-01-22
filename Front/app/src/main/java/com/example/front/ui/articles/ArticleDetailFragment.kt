package com.example.front.ui.articles

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.repository.ArticleRepository
import com.example.front.databinding.FragmentArticleDetailBinding
import com.example.front.util.Resource
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleDetailFragment : Fragment() {
    
    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: ArticleDetailFragmentArgs by navArgs()
    
    private val viewModel: ArticleViewModel by viewModels {
        val preferencesManager = PreferencesManager(requireContext())
        val apiService = RetrofitClient.getApiService { preferencesManager.getToken() }
        ArticleViewModelFactory(ArticleRepository(apiService))
    }
    
    private lateinit var coauthorAdapter: CoauthorAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupCoauthorsRecyclerView()
        observeArticle()
        
        // Load article details
        viewModel.getArticleById(args.articleId)
    }
    
    private fun setupCoauthorsRecyclerView() {
        coauthorAdapter = CoauthorAdapter()
        binding.rvCoauthors.apply {
            adapter = coauthorAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun observeArticle() {
        viewModel.selectedArticle.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    
                    resource.data?.let { article ->
                        binding.apply {
                            // Title and description
                            tvArticleTitle.text = article.title
                            tvDescription.text = article.description
                            
                            // Main author
                            tvMainAuthor.text = article.mainAuthor.name
                            tvMainAuthorDepartment.text = article.mainAuthor.department?.name 
                                ?: "Кафедра не указана"
                            
                            // Coauthors
                            if (article.coauthors.isNullOrEmpty()) {
                                rvCoauthors.visibility = View.GONE
                                tvNoCoauthors.visibility = View.VISIBLE
                            } else {
                                rvCoauthors.visibility = View.VISIBLE
                                tvNoCoauthors.visibility = View.GONE
                                coauthorAdapter.submitList(article.coauthors)
                            }
                            
                            // Dates
                            tvPublicationDate.text = formatDate(article.publicationDate) ?: "Не указана"
                            tvCreatedAt.text = formatDate(article.createdAt) ?: "Не указана"
                            tvUpdatedAt.text = formatDate(article.updatedAt) ?: "Не указана"
                            
                            // External link
                            if (article.externalLink != null) {
                                btnOpenLink.visibility = View.VISIBLE
                                btnOpenLink.setOnClickListener {
                                    openExternalLink(article.externalLink)
                                }
                            } else {
                                btnOpenLink.visibility = View.GONE
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    
                    Snackbar.make(
                        binding.root,
                        resource.message ?: "Ошибка загрузки статьи",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    private fun formatDate(dateString: String?): String? {
        dateString ?: return null
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) }
        } catch (e: Exception) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) }
            } catch (e2: Exception) {
                dateString
            }
        }
    }
    
    private fun openExternalLink(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Snackbar.make(
                binding.root,
                "Не удалось открыть ссылку",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

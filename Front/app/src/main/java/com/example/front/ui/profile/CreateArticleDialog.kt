package com.example.front.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.model.ArticleCreateRequest
import com.example.front.data.repository.ArticleRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.data.repository.ResearchTeamRepository
import com.example.front.databinding.DialogCreateArticleBinding
import com.example.front.util.Resource
import com.google.android.material.snackbar.Snackbar

class CreateArticleDialog : DialogFragment() {

    private var _binding: DialogCreateArticleBinding? = null
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

    private var onArticleCreated: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreateArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
    }

    private fun setupDialog() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnCreate.setOnClickListener {
            createArticle()
        }
    }

    private fun createArticle() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val externalLink = binding.etExternalLink.text.toString().trim()
        val publicationDate = binding.etPublicationDate.text.toString().trim()

        // Validation
        if (title.isEmpty()) {
            binding.etTitle.error = "Введите название статьи"
            return
        }

        if (description.isEmpty()) {
            binding.etDescription.error = "Введите описание"
            return
        }

        // Get current employee ID as main author
        val preferencesManager = PreferencesManager(requireContext())
        val mainAuthorId = preferencesManager.getEmployeeId()

        if (mainAuthorId == -1L) {
            Snackbar.make(binding.root, "Ошибка: ID сотрудника не найден", Snackbar.LENGTH_LONG).show()
            return
        }

        // Show loading
        binding.progressBar.visibility = View.VISIBLE
        binding.btnCreate.isEnabled = false

        // Parse coauthor IDs if provided
        val coauthorIds = binding.etCoauthorIds.text.toString()
            .split(",")
            .mapNotNull { it.trim().toLongOrNull() }
            .takeIf { it.isNotEmpty() }

        // Create article
        val request = ArticleCreateRequest(
            title = title,
            description = description,
            externalLink = externalLink.takeIf { it.isNotEmpty() },
            publicationDate = publicationDate.takeIf { it.isNotEmpty() },
            mainAuthorId = mainAuthorId,
            coauthorIds = coauthorIds
        )

        viewModel.createArticle(request)
    }

    private fun observeCreation() {
        viewModel.articleCreationResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnCreate.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    onArticleCreated?.invoke()
                    dismiss()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnCreate.isEnabled = true
                    Snackbar.make(
                        binding.root,
                        resource.message ?: "Ошибка создания статьи",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        observeCreation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setOnArticleCreatedListener(listener: () -> Unit) {
        onArticleCreated = listener
    }

    companion object {
        const val TAG = "CreateArticleDialog"

        fun newInstance(): CreateArticleDialog {
            return CreateArticleDialog()
        }
    }
}

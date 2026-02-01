package com.example.front.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.model.ArticleCreateRequest
import com.example.front.data.model.Employee
import com.example.front.data.repository.ArticleRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.data.repository.ResearchTeamRepository
import com.example.front.databinding.DialogCreateArticleBinding
import com.example.front.util.Resource
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

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

    private val employeeRepository by lazy {
        val preferencesManager = PreferencesManager(requireContext())
        val apiService = RetrofitClient.getApiService { preferencesManager.getToken() }
        EmployeeRepository(apiService)
    }

    private var onArticleCreated: (() -> Unit)? = null
    private var employees: List<Employee> = emptyList()
    private val selectedCoauthorIds = mutableSetOf<Long>()

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
        loadEmployees()
    }

    private fun loadEmployees() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = employeeRepository.getEmployees()
                when (result) {
                    is Resource.Success -> {
                        employees = result.data ?: emptyList()
                        setupCoauthorChips()
                    }
                    is Resource.Error -> {
                        // Continue without employee selection
                        android.util.Log.e("CreateArticleDialog", "Error loading employees: ${result.message}")
                    }
                    is Resource.Loading -> {}
                }
            } catch (e: Exception) {
                android.util.Log.e("CreateArticleDialog", "Exception loading employees", e)
            }
        }
    }

    private fun setupCoauthorChips() {
        val currentEmployeeId = PreferencesManager(requireContext()).getEmployeeId()
        
        // Filter out current user from coauthor selection
        val availableEmployees = employees.filter { it.id != currentEmployeeId }
        
        availableEmployees.forEach { employee ->
            val chip = Chip(requireContext()).apply {
                text = employee.name
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedCoauthorIds.add(employee.id)
                    } else {
                        selectedCoauthorIds.remove(employee.id)
                    }
                }
            }
            binding.chipGroupCoauthors.addView(chip)
        }
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

        // Parse coauthor IDs from text field if chips weren't used
        val manualCoauthorIds = binding.etCoauthorIds.text.toString()
            .split(",")
            .mapNotNull { it.trim().toLongOrNull() }
        
        // Combine selected chips and manual IDs
        val allCoauthorIds = (selectedCoauthorIds + manualCoauthorIds).toList().takeIf { it.isNotEmpty() }

        // Create article without publication date (server will set it automatically)
        val request = ArticleCreateRequest(
            title = title,
            description = description,
            externalLink = externalLink.takeIf { it.isNotEmpty() },
            mainAuthorId = mainAuthorId,
            coauthorIds = allCoauthorIds
        )

        android.util.Log.d("CreateArticleDialog", "Creating article with request: $request")
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

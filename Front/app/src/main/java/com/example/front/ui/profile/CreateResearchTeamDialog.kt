package com.example.front.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.model.ResearchTeamCreateRequest
import com.example.front.data.model.Employee
import com.example.front.data.repository.ArticleRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.data.repository.ResearchTeamRepository
import com.example.front.databinding.DialogCreateResearchTeamBinding
import com.example.front.util.Resource
import com.google.android.material.snackbar.Snackbar

class CreateResearchTeamDialog : DialogFragment() {

    private var _binding: DialogCreateResearchTeamBinding? = null
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

    private var onTeamCreated: (() -> Unit)? = null

    private var employees: List<Employee> = emptyList()
    private val selectedMemberIds = mutableSetOf<Long>()
    private val employeeRepository by lazy {
        val preferencesManager = PreferencesManager(requireContext())
        val apiService = RetrofitClient.getApiService { preferencesManager.getToken() }
        com.example.front.data.repository.EmployeeRepository(apiService)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreateResearchTeamBinding.inflate(inflater, container, false)
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
                        setupMemberChips()
                    }
                    is Resource.Error -> {
                        android.util.Log.e("CreateResearchTeamDialog", "Error loading employees: ${result.message}")
                    }
                    is Resource.Loading -> {}
                }
            } catch (e: Exception) {
                android.util.Log.e("CreateResearchTeamDialog", "Exception loading employees", e)
            }
        }
    }

    private fun setupMemberChips() {
        val currentEmployeeId = PreferencesManager(requireContext()).getEmployeeId()
        val available = employees.filter { it.id != currentEmployeeId }
        available.forEach { emp ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = emp.name
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedMemberIds.add(emp.id) else selectedMemberIds.remove(emp.id)
                }
            }
            binding.chipGroupMembers.addView(chip)
        }
    }

    private fun setupDialog() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnCreate.setOnClickListener {
            createTeam()
        }
    }

    private fun createTeam() {
        val name = binding.etTeamName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            binding.etTeamName.error = "Введите название коллектива"
            return
        }

        if (description.isEmpty()) {
            binding.etDescription.error = "Введите описание"
            return
        }

        // Get current employee ID as leader
        val preferencesManager = PreferencesManager(requireContext())
        val leaderId = preferencesManager.getEmployeeId()

        if (leaderId == -1L) {
            Snackbar.make(binding.root, "Ошибка: ID сотрудника не найден", Snackbar.LENGTH_LONG).show()
            return
        }

        // Show loading
        binding.progressBar.visibility = View.VISIBLE
        binding.btnCreate.isEnabled = false

        // Parse manual member IDs (comma-separated)
        val manualIds = binding.etMemberIds?.text.toString()
            .split(",")
            .mapNotNull { it.trim().toLongOrNull() }

        val allMemberIds = (selectedMemberIds + manualIds).toList().takeIf { it.isNotEmpty() }

        // Create team with optional members
        val request = ResearchTeamCreateRequest(
            name = name,
            description = description,
            leaderId = leaderId,
            memberIds = allMemberIds
        )

        viewModel.createResearchTeam(request)
    }

    private fun observeCreation() {
        viewModel.teamCreationResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnCreate.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    onTeamCreated?.invoke()
                    dismiss()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnCreate.isEnabled = true
                    Snackbar.make(
                        binding.root,
                        resource.message ?: "Ошибка создания коллектива",
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

    fun setOnTeamCreatedListener(listener: () -> Unit) {
        onTeamCreated = listener
    }

    companion object {
        const val TAG = "CreateResearchTeamDialog"

        fun newInstance(): CreateResearchTeamDialog {
            return CreateResearchTeamDialog()
        }
    }
}

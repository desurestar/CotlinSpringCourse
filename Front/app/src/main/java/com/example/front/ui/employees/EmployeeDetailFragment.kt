package com.example.front.ui.employees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.front.R
import com.example.front.data.local.PreferencesManager
import com.example.front.databinding.FragmentEmployeeDetailBinding
import com.example.front.util.Resource
import com.example.front.util.gone
import com.example.front.util.showToast
import com.example.front.util.visible

class EmployeeDetailFragment : Fragment() {
    
    private var _binding: FragmentEmployeeDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: EmployeeDetailFragmentArgs by navArgs()
    
    private val viewModel: EmployeeViewModel by viewModels {
        EmployeeViewModelFactory(PreferencesManager(requireContext()))
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        viewModel.loadEmployeeById(args.employeeId)
    }
    
    private fun setupObservers() {
        viewModel.employee.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visible()
                }
                is Resource.Success -> {
                    binding.progressBar.gone()
                    resource.data?.let { employee ->
                        binding.tvIdBadge.text = getString(R.string.employee_id, employee.id)
                        binding.tvInitials.text = getInitials(employee.name)
                        binding.tvEmployeeName.text = employee.name
                        binding.tvPost.text = employee.post?.postName ?: "Не указано"
                        binding.tvDepartment.text = employee.department?.departmentName ?: "Не указано"
                        binding.tvEmail.text = employee.user?.email ?: "Не указано"
                        binding.tvRole.text = employee.user?.role ?: "Не указано"
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.gone()
                    showToast(resource.message ?: "Ошибка загрузки сотрудника")
                }
            }
        }
    }
    
    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when (parts.size) {
            0 -> "?"
            1 -> parts[0].take(1).uppercase()
            else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

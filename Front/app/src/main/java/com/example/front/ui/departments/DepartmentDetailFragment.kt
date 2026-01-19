package com.example.front.ui.departments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.front.R
import com.example.front.data.local.PreferencesManager
import com.example.front.databinding.FragmentDepartmentDetailBinding
import com.example.front.ui.employees.EmployeeAdapter
import com.example.front.util.Resource
import com.example.front.util.gone
import com.example.front.util.showToast
import com.example.front.util.visible

class DepartmentDetailFragment : Fragment() {
    
    private var _binding: FragmentDepartmentDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: DepartmentDetailFragmentArgs by navArgs()
    
    private val viewModel: DepartmentViewModel by viewModels {
        DepartmentViewModelFactory(PreferencesManager(requireContext()))
    }
    
    private lateinit var employeeAdapter: EmployeeAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDepartmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        
        viewModel.loadDepartmentById(args.departmentId)
    }
    
    private fun setupRecyclerView() {
        employeeAdapter = EmployeeAdapter { employee ->
            val action = DepartmentDetailFragmentDirections
                .actionDepartmentDetailToEmployeeDetail(employee.id)
            findNavController().navigate(action)
        }
        
        binding.rvEmployees.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmployees.adapter = employeeAdapter
    }
    
    private fun setupObservers() {
        viewModel.departmentDetail.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visible()
                }
                is Resource.Success -> {
                    binding.progressBar.gone()
                    resource.data?.let { department ->
                        binding.tvDepartmentName.text = department.departmentName
                        binding.tvEmployeeCount.text = getString(
                            R.string.employee_count,
                            department.employees?.size ?: 0
                        )
                        
                        val employees = department.employees ?: emptyList()
                        if (employees.isEmpty()) {
                            binding.tvEmptyState.visible()
                            binding.rvEmployees.gone()
                        } else {
                            binding.tvEmptyState.gone()
                            binding.rvEmployees.visible()
                            employeeAdapter.submitList(employees)
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.gone()
                    showToast(resource.message ?: "Ошибка загрузки отдела")
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.front.ui.departments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.front.data.local.PreferencesManager
import com.example.front.databinding.FragmentDepartmentListBinding
import com.example.front.util.Resource
import com.example.front.util.gone
import com.example.front.util.showToast
import com.example.front.util.visible

class DepartmentListFragment : Fragment() {
    
    private var _binding: FragmentDepartmentListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DepartmentViewModel by viewModels {
        DepartmentViewModelFactory(PreferencesManager(requireContext()))
    }
    
    private lateinit var adapter: DepartmentAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDepartmentListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearch()
        setupObservers()
        
        viewModel.loadDepartments()
    }
    
    private fun setupRecyclerView() {
        adapter = DepartmentAdapter { department ->
            val action = DepartmentListFragmentDirections
                .actionDepartmentListToDepartmentDetail(department.id)
            findNavController().navigate(action)
        }
        
        binding.rvDepartments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDepartments.adapter = adapter
    }
    
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim()
                viewModel.searchDepartments(if (query.isNullOrEmpty()) null else query)
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun setupObservers() {
        viewModel.departments.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visible()
                    binding.rvDepartments.gone()
                    binding.tvEmptyState.gone()
                }
                is Resource.Success -> {
                    binding.progressBar.gone()
                    val departments = resource.data ?: emptyList()
                    
                    if (departments.isEmpty()) {
                        binding.tvEmptyState.visible()
                        binding.rvDepartments.gone()
                    } else {
                        binding.tvEmptyState.gone()
                        binding.rvDepartments.visible()
                        adapter.submitList(departments)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.gone()
                    binding.rvDepartments.gone()
                    binding.tvEmptyState.visible()
                    showToast(resource.message ?: "Ошибка загрузки отделов")
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

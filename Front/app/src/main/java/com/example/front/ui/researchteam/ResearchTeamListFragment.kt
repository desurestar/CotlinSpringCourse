package com.example.front.ui.researchteam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.front.databinding.FragmentResearchTeamListBinding
import com.example.front.util.Resource
import com.example.front.util.gone
import com.example.front.util.showToast
import com.example.front.util.visible

class ResearchTeamListFragment : Fragment() {
    
    private var _binding: FragmentResearchTeamListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: ResearchTeamViewModel
    private lateinit var adapter: ResearchTeamAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResearchTeamListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this, ResearchTeamViewModelFactory(requireContext()))[ResearchTeamViewModel::class.java]
        
        setupRecyclerView()
        setupListeners()
        setupObservers()
        
        viewModel.loadTeams()
    }
    
    private fun setupRecyclerView() {
        adapter = ResearchTeamAdapter { team ->
            val action = ResearchTeamListFragmentDirections.actionTeamListToTeamDetail(team.id)
            findNavController().navigate(action)
        }
        binding.rvTeams.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTeams.adapter = adapter
    }
    
    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            // Show create team dialog
            showToast("Создание коллектива - в разработке")
        }
    }
    
    private fun setupObservers() {
        viewModel.teams.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visible()
                    binding.emptyState.gone()
                }
                is Resource.Success -> {
                    binding.progressBar.gone()
                    val teams = resource.data ?: emptyList()
                    if (teams.isEmpty()) {
                        binding.emptyState.visible()
                        binding.rvTeams.gone()
                    } else {
                        binding.emptyState.gone()
                        binding.rvTeams.visible()
                        adapter.submitList(teams)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.gone()
                    showToast(resource.message ?: "Ошибка")
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

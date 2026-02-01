package com.example.front.ui.researchteam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.front.databinding.FragmentResearchTeamDetailBinding
import com.example.front.util.Resource
import com.example.front.util.gone
import com.example.front.util.showToast
import com.example.front.util.visible
import com.google.android.material.tabs.TabLayout

class ResearchTeamDetailFragment : Fragment() {
    
    private var _binding: FragmentResearchTeamDetailBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: ResearchTeamViewModel
    private lateinit var memberAdapter: TeamMemberAdapter
    private lateinit var workAdapter: TeamWorkAdapter
    
    private val args: ResearchTeamDetailFragmentArgs by navArgs()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResearchTeamDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this, ResearchTeamViewModelFactory(requireContext()))[ResearchTeamViewModel::class.java]
        
        setupRecyclerViews()
        setupTabs()
        setupObservers()
        
        viewModel.loadTeamDetail(args.teamId)
    }
    
    private fun setupRecyclerViews() {
        memberAdapter = TeamMemberAdapter()
        binding.rvMembers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMembers.adapter = memberAdapter
        
        workAdapter = TeamWorkAdapter()
        binding.rvWorks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWorks.adapter = workAdapter
    }
    
    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.membersSection.visible()
                        binding.worksSection.gone()
                    }
                    1 -> {
                        binding.membersSection.gone()
                        binding.worksSection.visible()
                    }
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    private fun setupObservers() {
        viewModel.teamDetail.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visible()
                }
                is Resource.Success -> {
                    binding.progressBar.gone()
                    resource.data?.let { team ->
                        binding.tvTeamName.text = team.name
                        binding.tvLeader.text = "👤 Лидер: ${team.leader?.name ?: "Не указан"}"
                        binding.tvDescription.text = team.description ?: "Нет описания"
                        
                        // Members
                        val members = team.members ?: emptyList()
                        if (members.isEmpty()) {
                            binding.emptyMembers.visible()
                            binding.rvMembers.gone()
                        } else {
                            binding.emptyMembers.gone()
                            binding.rvMembers.visible()
                            memberAdapter.submitList(members)
                        }
                        
                        // Works
                        val works = team.researchWorks ?: emptyList()
                        if (works.isEmpty()) {
                            binding.emptyWorks.visible()
                            binding.rvWorks.gone()
                        } else {
                            binding.emptyWorks.gone()
                            binding.rvWorks.visible()
                            workAdapter.submitList(works)
                        }
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

package com.example.front.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.repository.ArticleRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.data.repository.ResearchTeamRepository
import com.example.front.databinding.FragmentProfileTabBinding
import com.example.front.ui.researchteam.ResearchTeamAdapter
import com.example.front.util.Resource
import com.example.front.util.showToast
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProfileTeamsTabFragment : Fragment() {

    private var _binding: FragmentProfileTabBinding? = null
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

    private var employeeId: Long = -1L

    private lateinit var adapter: ResearchTeamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            employeeId = it.getLong(ARG_EMPLOYEE_ID, -1L)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCreateButton()
        setupRecyclerView()
        loadTeams()
        observeTeams()
        observeTeamDeletion()
    }

    private fun setupCreateButton() {
        if (employeeId != -1L) {
            binding.btnCreateTeam.visibility = View.VISIBLE
            binding.btnCreateTeam.setOnClickListener {
                val dialog = CreateResearchTeamDialog.newInstance()
                dialog.setOnTeamCreatedListener {
                    loadTeams()
                    Snackbar.make(binding.root, "Научный коллектив успешно создан", Snackbar.LENGTH_SHORT).show()
                }
                if (childFragmentManager.findFragmentByTag(CreateResearchTeamDialog.TAG) == null) {
                    dialog.show(childFragmentManager, CreateResearchTeamDialog.TAG)
                }
            }
        } else {
            binding.btnCreateTeam.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = ResearchTeamAdapter({ team ->
            val bundle = androidx.core.os.bundleOf("teamId" to team.id)
            findNavController().navigate(com.example.front.R.id.researchTeamDetailFragment, bundle)
        }, onDeleteClick = { team ->
            val currentEmployeeId = PreferencesManager(requireContext()).getEmployeeId()
            if (team.leader?.id == currentEmployeeId) {
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Удалить коллектив?")
                    .setMessage("Вы уверены, что хотите удалить коллектив \"${team.name}\"?")
                    .setPositiveButton("Удалить") { _, _ ->
                        viewModel.deleteTeam(team.id)
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            } else {
                showToast("Только руководитель может удалить коллектив")
            }
        }, onItemLongClick = { team ->
            // fallback: long-press also attempts deletion for leader
            val currentEmployeeId = PreferencesManager(requireContext()).getEmployeeId()
            if (team.leader?.id == currentEmployeeId) {
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Удалить коллектив?")
                    .setMessage("Вы уверены, что хотите удалить коллектив \"${team.name}\"?")
                    .setPositiveButton("Удалить") { _, _ ->
                        viewModel.deleteTeam(team.id)
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            } else {
                showToast("Только руководитель может удалить коллектив")
            }
        })

        binding.rvTeams.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTeams.adapter = adapter
    }

    private fun loadTeams() {
        if (employeeId != -1L) {
            viewModel.refreshEmployeeResearchTeams(employeeId)
        }
    }

    private fun observeTeams() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myTeams.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.contentContainer.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.GONE
                        binding.rvTeams.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val teams = resource.data ?: emptyList()
                        if (teams.isEmpty()) {
                            binding.contentContainer.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                            binding.tvEmptyState.text = "Нет коллективов"
                            binding.rvTeams.visibility = View.GONE
                        } else {
                            binding.contentContainer.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.GONE
                            binding.rvTeams.visibility = View.VISIBLE
                            adapter.submitList(teams)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.contentContainer.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.tvEmptyState.text = resource.message ?: "Ошибка загрузки коллективов"
                        binding.rvTeams.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun observeTeamDeletion() {
        viewModel.teamDeletionResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    showToast("Коллектив удален")
                    loadTeams()
                }
                is Resource.Error -> {
                    showToast(resource.message ?: "Ошибка удаления коллектива")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_EMPLOYEE_ID = "employee_id"

        fun newInstance(employeeId: Long): ProfileTeamsTabFragment {
            return ProfileTeamsTabFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_EMPLOYEE_ID, employeeId)
                }
            }
        }
    }
}
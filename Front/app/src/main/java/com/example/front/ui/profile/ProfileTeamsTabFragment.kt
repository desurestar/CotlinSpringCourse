package com.example.front.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.front.R
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.repository.ArticleRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.data.repository.ResearchTeamRepository
import com.example.front.databinding.FragmentProfileTabBinding
import com.example.front.util.Resource
import com.google.android.material.card.MaterialCardView

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

        loadTeams()
        observeTeams()
    }

    private fun loadTeams() {
        viewModel.getEmployeeResearchTeams(employeeId)
    }

    private fun observeTeams() {
        viewModel.myTeams.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentContainer.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE

                    val teams = resource.data ?: emptyList()
                    if (teams.isEmpty()) {
                        binding.contentContainer.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.tvEmptyState.text = "Нет коллективов"
                    } else {
                        binding.contentContainer.visibility = View.VISIBLE
                        binding.tvEmptyState.visibility = View.GONE

                        binding.contentContainer.removeAllViews()

                        teams.forEach { team ->
                            addTeamCard(
                                teamName = team.name,
                                leaderName = team.leader.name,
                                description = team.description ?: "Описание отсутствует"
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentContainer.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.tvEmptyState.text = resource.message ?: "Ошибка загрузки коллективов"
                }
            }
        }
    }

    private fun addTeamCard(teamName: String, leaderName: String, description: String) {
        val cardView = MaterialCardView(requireContext()).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = resources.getDimensionPixelSize(R.dimen.card_margin)
            }
            radius = resources.getDimension(R.dimen.card_corner_radius)
            cardElevation = resources.getDimension(R.dimen.card_elevation)

            val padding = resources.getDimensionPixelSize(R.dimen.card_padding)
            setContentPadding(padding, padding, padding, padding)
        }

        val textView = TextView(requireContext()).apply {
            text = buildString {
                append("👥 ")
                append(teamName)
                append("\n")
                append("Руководитель: ")
                append(leaderName)
                append("\n")
                append(description)
            }
            textSize = 16f
            setTextColor(resources.getColor(R.color.text_primary, null))
        }

        cardView.addView(textView)
        binding.contentContainer.addView(cardView)
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
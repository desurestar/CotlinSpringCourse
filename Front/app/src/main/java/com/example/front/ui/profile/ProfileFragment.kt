package com.example.front.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.front.R
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.model.ArticleCreateRequest
import com.example.front.data.model.ResearchTeamCreateRequest
import com.example.front.data.repository.ArticleRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.data.repository.ResearchTeamRepository
import com.example.front.databinding.FragmentProfileBinding
import com.example.front.ui.articles.ArticleViewModel
import com.example.front.ui.articles.ArticleViewModelFactory
import com.example.front.ui.researchteam.ResearchTeamViewModel
import com.example.front.ui.researchteam.ResearchTeamViewModelFactory
import com.example.front.util.Resource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText

class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager
    private var employeeId: Long = -1L
    
    private val profileViewModel: ProfileViewModel by viewModels {
        val apiService = RetrofitClient.getApiService { preferencesManager.getToken() }
        ProfileViewModelFactory(
            EmployeeRepository(apiService),
            ArticleRepository(apiService),
            ResearchTeamRepository(apiService)
        )
    }
    
    private val articleViewModel: ArticleViewModel by viewModels {
        val apiService = RetrofitClient.getApiService { preferencesManager.getToken() }
        ArticleViewModelFactory(ArticleRepository(apiService))
    }
    
    private val teamViewModel: ResearchTeamViewModel by viewModels {
        val apiService = RetrofitClient.getApiService { preferencesManager.getToken() }
        ResearchTeamViewModelFactory(ResearchTeamRepository(apiService))
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager(requireContext())
        
        // Check if user is logged in and is an employee
        if (!preferencesManager.isAuthenticated()) {
            showGuestMessage()
            return
        }
        
        employeeId = preferencesManager.getEmployeeId()
        
        if (employeeId == -1L) {
            showNoEmployeeMessage()
            return
        }
        
        setupProfile()
        setupFabs()
        observeArticleCreation()
        observeTeamCreation()
        
        // Load all profile data
        profileViewModel.refreshAll(employeeId)
    }
    
    private fun setupProfile() {
        binding.guestCard.visibility = View.GONE
        binding.profileContent.visibility = View.VISIBLE
        
        val pagerAdapter = ProfilePagerAdapter(this, employeeId)
        binding.viewPager.adapter = pagerAdapter
        
        val tabTitles = arrayOf(
            "Информация",
            "Мои статьи",
            "Участие в статьях",
            "Мои коллективы",
            "Участие в коллективах"
        )
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
        
        // Handle tab selection for FAB visibility
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateFabVisibility(tab?.position ?: 0)
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        
        updateFabVisibility(0)
    }
    
    private fun updateFabVisibility(position: Int) {
        when (position) {
            1 -> { // My Articles tab
                binding.fabCreateArticle.show()
                binding.fabCreateTeam.hide()
            }
            3 -> { // My Teams tab
                binding.fabCreateArticle.hide()
                binding.fabCreateTeam.show()
            }
            else -> {
                binding.fabCreateArticle.hide()
                binding.fabCreateTeam.hide()
            }
        }
    }
    
    private fun setupFabs() {
        binding.fabCreateArticle.setOnClickListener {
            showCreateArticleDialog()
        }
        
        binding.fabCreateTeam.setOnClickListener {
            showCreateTeamDialog()
        }
    }
    
    private fun showCreateArticleDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_article, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()
        
        val etTitle = dialogView.findViewById<TextInputEditText>(R.id.etTitle)
        val etDescription = dialogView.findViewById<TextInputEditText>(R.id.etDescription)
        val etExternalLink = dialogView.findViewById<TextInputEditText>(R.id.etExternalLink)
        val etCoauthorIds = dialogView.findViewById<TextInputEditText>(R.id.etCoauthorIds)
        val progressBar = dialogView.findViewById<View>(R.id.progressBar)
        val btnCreate = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCreate)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
        
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        btnCreate.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val externalLink = etExternalLink.text.toString().trim().takeIf { it.isNotEmpty() }
            val coauthorIdsStr = etCoauthorIds.text.toString().trim()
            
            if (title.isEmpty() || description.isEmpty()) {
                Snackbar.make(binding.root, "Заполните все обязательные поля", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val coauthorIds = if (coauthorIdsStr.isNotEmpty()) {
                try {
                    coauthorIdsStr.split(",").map { it.trim().toLong() }
                } catch (e: Exception) {
                    Snackbar.make(binding.root, "Неверный формат ID соавторов", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                null
            }
            
            val request = ArticleCreateRequest(
                title = title,
                description = description,
                externalLink = externalLink,
                mainAuthorId = employeeId,
                coauthorIds = coauthorIds
            )
            
            progressBar.visibility = View.VISIBLE
            btnCreate.isEnabled = false
            
            articleViewModel.createArticle(request)
            dialog.dismiss()
        }
        
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.show()
    }
    
    private fun showCreateTeamDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_research_team, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()
        
        val etTeamName = dialogView.findViewById<TextInputEditText>(R.id.etTeamName)
        val etDescription = dialogView.findViewById<TextInputEditText>(R.id.etDescription)
        val progressBar = dialogView.findViewById<View>(R.id.progressBar)
        val btnCreate = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCreate)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
        
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        btnCreate.setOnClickListener {
            val name = etTeamName.text.toString().trim()
            val description = etDescription.text.toString().trim()
            
            if (name.isEmpty()) {
                Snackbar.make(binding.root, "Введите название коллектива", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val request = ResearchTeamCreateRequest(
                name = name,
                description = description.takeIf { it.isNotEmpty() },
                leaderId = employeeId
            )
            
            progressBar.visibility = View.VISIBLE
            btnCreate.isEnabled = false
            
            teamViewModel.createTeam(request)
            dialog.dismiss()
        }
        
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.show()
    }
    
    private fun observeArticleCreation() {
        articleViewModel.createArticleResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Статья создана", Snackbar.LENGTH_SHORT).show()
                    profileViewModel.loadMyArticles(employeeId)
                }
                is Resource.Error -> {
                    Snackbar.make(
                        binding.root,
                        resource.message ?: "Ошибка создания статьи",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                else -> {}
            }
        }
    }
    
    private fun observeTeamCreation() {
        teamViewModel.createTeamResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Коллектив создан", Snackbar.LENGTH_SHORT).show()
                    profileViewModel.loadMyTeams(employeeId)
                }
                is Resource.Error -> {
                    Snackbar.make(
                        binding.root,
                        resource.message ?: "Ошибка создания коллектива",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                else -> {}
            }
        }
    }
    
    private fun showGuestMessage() {
        binding.guestCard.visibility = View.VISIBLE
        binding.profileContent.visibility = View.GONE
        binding.fabCreateArticle.hide()
        binding.fabCreateTeam.hide()
    }
    
    private fun showNoEmployeeMessage() {
        binding.guestCard.visibility = View.VISIBLE
        binding.profileContent.visibility = View.GONE
        binding.fabCreateArticle.hide()
        binding.fabCreateTeam.hide()
        
        // Update message for non-employee users
        val textView = binding.guestCard.findViewById<android.widget.TextView>(
            com.google.android.R.id.text1
        )
        textView?.text = "Профиль доступен только для сотрудников"
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

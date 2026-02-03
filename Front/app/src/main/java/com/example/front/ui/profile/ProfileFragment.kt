package com.example.front.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.repository.ArticleRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.data.repository.ResearchTeamRepository
import com.example.front.databinding.FragmentProfileBinding
import com.example.front.util.Resource
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager
    
    private val viewModel: ProfileViewModel by viewModels {
        val preferencesManager = PreferencesManager(requireContext())
        val apiService = RetrofitClient.getApiService { preferencesManager.getToken() }
        ProfileViewModelFactory(
            EmployeeRepository(apiService),
            ArticleRepository(apiService),
            ResearchTeamRepository(apiService)
        )
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        preferencesManager = PreferencesManager(requireContext())
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Check if user is in guest mode
        if (preferencesManager.isGuestMode()) {
            showGuestMode()
        } else {
            showProfileContent()
            loadProfileData()
        }
    }
    
    
    private fun showGuestMode() {
        binding.guestCard.visibility = View.VISIBLE
        binding.profileContent.visibility = View.GONE
    }
    
    private fun showProfileContent() {
        binding.guestCard.visibility = View.GONE
        binding.profileContent.visibility = View.VISIBLE
    }
    
    private fun loadProfileData() {
        val employeeId = preferencesManager.getEmployeeId()
        
        android.util.Log.d("ProfileFragment", "Loading profile for employeeId: $employeeId")
        
        if (employeeId == -1L) {
            // User doesn't have an employee record
            android.util.Log.w("ProfileFragment", "No employee record found. UserId: ${preferencesManager.getUserId()}")
            
            // Show guest-like view with explanation
            binding.guestCard.visibility = View.VISIBLE
            binding.profileContent.visibility = View.GONE
            
            // Show informative message
            Snackbar.make(
                binding.root,
                "У вашей учетной записи нет профиля сотрудника. Обратитесь к администратору для связывания аккаунта.",
                Snackbar.LENGTH_LONG
            ).show()
            return
        }
        
        // Load all data centrally before setting up ViewPager
        viewModel.getCurrentEmployee(employeeId)
        viewModel.getEmployeeArticlesAsAuthor(employeeId)
        viewModel.getEmployeeArticlesAsCoauthor(employeeId)
        viewModel.getEmployeeResearchTeams(employeeId)
        
        // Observe employee data to know when to set up ViewPager
        observeEmployee(employeeId)
    }
    
    private fun observeEmployee(employeeId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentEmployee.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        
                        resource.data?.let { employee ->
                            // Set up ViewPager once employee data is loaded
                            if (binding.viewPager.adapter == null) {
                                setupViewPager(employee.id)
                            }
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        
                        Snackbar.make(
                            binding.root,
                            resource.message ?: "Ошибка загрузки профиля",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
    
    private fun setupViewPager(employeeId: Long) {
        val adapter = ProfilePagerAdapter(this, employeeId)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Информация"
                1 -> "Мои статьи"
                2 -> "Участие"
                3 -> "Коллективы"
                else -> ""
            }
        }.attach()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private class ProfilePagerAdapter(
        fragment: Fragment,
        private val employeeId: Long
    ) : FragmentStateAdapter(fragment) {
        
        override fun getItemCount(): Int = 4
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ProfileInfoTabFragment.newInstance(employeeId)
                1 -> ProfileArticlesTabFragment.newInstance(employeeId, isMainAuthor = true)
                2 -> ProfileArticlesTabFragment.newInstance(employeeId, isMainAuthor = false)
                3 -> ProfileTeamsTabFragment.newInstance(employeeId)
                else -> ProfileInfoTabFragment.newInstance(employeeId)
            }
        }
    }
}

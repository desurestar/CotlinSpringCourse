package com.example.front.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.front.R
import com.example.front.data.api.RetrofitClient
import com.example.front.data.local.PreferencesManager
import com.example.front.data.model.Article
import com.example.front.data.model.ResearchTeam
import com.example.front.data.repository.ArticleRepository
import com.example.front.data.repository.EmployeeRepository
import com.example.front.data.repository.ResearchTeamRepository
import com.example.front.databinding.FragmentProfileTabBinding
import com.example.front.util.Resource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ProfileTabFragment : Fragment() {
    
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
    
    private var tabType: String = TAB_INFO
    private var employeeId: Long = -1L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tabType = it.getString(ARG_TAB_TYPE, TAB_INFO)
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
        
        when (tabType) {
            TAB_INFO -> setupInfoTab()
            TAB_MY_ARTICLES -> setupMyArticlesTab()
            TAB_PARTICIPATION_ARTICLES -> setupParticipationArticlesTab()
            TAB_MY_TEAMS -> setupMyTeamsTab()
            TAB_PARTICIPATION_TEAMS -> setupParticipationTeamsTab()
        }
    }
    
    private fun setupInfoTab() {
        binding.btnCreateArticle.visibility = View.GONE
        viewModel.employee.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE
                    
                    val employee = resource.data
                    if (employee != null) {
                        displayEmployeeInfo(employee)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.tvEmptyState.text = resource.message ?: "Ошибка загрузки данных"
                }
            }
        }
    }
    
    private fun displayEmployeeInfo(employee: com.example.front.data.model.Employee) {
        binding.contentContainer.removeAllViews()
        
        val context = requireContext()
        val layoutInflater = LayoutInflater.from(context)
        
        // Add info cards
        addInfoCard(layoutInflater, "Имя", employee.name)
        addInfoCard(layoutInflater, "Должность", employee.post?.name ?: "Не указана")
        addInfoCard(layoutInflater, "Кафедра", employee.department?.name ?: "Не указана")
        employee.user?.let { user ->
            addInfoCard(layoutInflater, "Email", user.email)
            addInfoCard(layoutInflater, "Роль", user.role)
        }
    }
    
    private fun addInfoCard(layoutInflater: LayoutInflater, label: String, value: String) {
        val cardView = layoutInflater.inflate(
            R.layout.item_info_card,
            binding.contentContainer,
            false
        ) as com.google.android.material.card.MaterialCardView
        
        cardView.findViewById<android.widget.TextView>(R.id.tvLabel).text = label
        cardView.findViewById<android.widget.TextView>(R.id.tvValue).text = value
        
        binding.contentContainer.addView(cardView)
    }
    
    private fun setupMyArticlesTab() {
        binding.btnCreateArticle.visibility = View.VISIBLE
        binding.btnCreateArticle.text = "+ Создать статью"
        
        val adapter = ProfileArticleAdapter(
            onItemClick = { article ->
                navigateToArticleDetail(article.id)
            },
            onDeleteClick = { article ->
                showDeleteArticleDialog(article)
            }
        )
        
        setupRecyclerView(adapter)
        
        viewModel.myArticles.observe(viewLifecycleOwner) { resource ->
            handleListResource(resource, adapter, "Нет созданных статей")
        }
        
        viewModel.deleteArticleResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Статья удалена", Snackbar.LENGTH_SHORT).show()
                    viewModel.loadMyArticles(employeeId)
                }
                is Resource.Error -> {
                    Snackbar.make(
                        binding.root,
                        resource.message ?: "Ошибка удаления",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                else -> {}
            }
        }
    }
    
    private fun setupParticipationArticlesTab() {
        binding.btnCreateArticle.visibility = View.GONE
        
        val adapter = ProfileArticleAdapter(
            onItemClick = { article ->
                navigateToArticleDetail(article.id)
            }
        )
        
        setupRecyclerView(adapter)
        
        viewModel.participationArticles.observe(viewLifecycleOwner) { resource ->
            handleListResource(resource, adapter, "Нет статей с участием")
        }
    }
    
    private fun setupMyTeamsTab() {
        binding.btnCreateArticle.visibility = View.VISIBLE
        binding.btnCreateArticle.text = "+ Создать коллектив"
        
        val adapter = ProfileTeamAdapter(
            onItemClick = { team ->
                navigateToTeamDetail(team.id)
            },
            onDeleteClick = { team ->
                showDeleteTeamDialog(team)
            }
        )
        
        setupRecyclerView(adapter)
        
        viewModel.myTeams.observe(viewLifecycleOwner) { resource ->
            handleListResource(resource, adapter, "Нет созданных коллективов")
        }
        
        viewModel.deleteTeamResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Коллектив удален", Snackbar.LENGTH_SHORT).show()
                    viewModel.loadMyTeams(employeeId)
                }
                is Resource.Error -> {
                    Snackbar.make(
                        binding.root,
                        resource.message ?: "Ошибка удаления",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                else -> {}
            }
        }
    }
    
    private fun setupParticipationTeamsTab() {
        binding.btnCreateArticle.visibility = View.GONE
        
        val adapter = ProfileTeamAdapter(
            onItemClick = { team ->
                navigateToTeamDetail(team.id)
            }
        )
        
        setupRecyclerView(adapter)
        
        viewModel.participationTeams.observe(viewLifecycleOwner) { resource ->
            handleListResource(resource, adapter, "Нет коллективов с участием")
        }
    }
    
    private fun setupRecyclerView(listAdapter: RecyclerView.Adapter<*>) {
        val recyclerView = RecyclerView(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = listAdapter
        
        binding.contentContainer.removeAllViews()
        binding.contentContainer.addView(
            recyclerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
    
    private fun <T> handleListResource(
        resource: Resource<List<T>>,
        adapter: ListAdapter<T, *>,
        emptyMessage: String
    ) {
        when (resource) {
            is Resource.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
                binding.tvEmptyState.visibility = View.GONE
            }
            is Resource.Success -> {
                binding.progressBar.visibility = View.GONE
                val items = resource.data ?: emptyList()
                
                if (items.isEmpty()) {
                    binding.scrollView.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.tvEmptyState.text = emptyMessage
                } else {
                    binding.scrollView.visibility = View.VISIBLE
                    binding.tvEmptyState.visibility = View.GONE
                    adapter.submitList(items)
                }
            }
            is Resource.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.tvEmptyState.text = resource.message ?: "Ошибка загрузки"
            }
        }
    }
    
    private fun showDeleteArticleDialog(article: Article) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить статью?")
            .setMessage("Вы уверены, что хотите удалить статью \"${article.title}\"?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteArticle(article.id)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    
    private fun showDeleteTeamDialog(team: ResearchTeam) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить коллектив?")
            .setMessage("Вы уверены, что хотите удалить коллектив \"${team.name}\"?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteTeam(team.id)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    
    private fun navigateToArticleDetail(articleId: Long) {
        val action = ProfileFragmentDirections.actionProfileFragmentToArticleDetail(articleId)
        findNavController().navigate(action)
    }
    
    private fun navigateToTeamDetail(teamId: Long) {
        val bundle = Bundle().apply {
            putLong("teamId", teamId)
        }
        findNavController().navigate(R.id.researchTeamDetailFragment, bundle)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val ARG_TAB_TYPE = "tab_type"
        private const val ARG_EMPLOYEE_ID = "employee_id"
        
        const val TAB_INFO = "info"
        const val TAB_MY_ARTICLES = "my_articles"
        const val TAB_PARTICIPATION_ARTICLES = "participation_articles"
        const val TAB_MY_TEAMS = "my_teams"
        const val TAB_PARTICIPATION_TEAMS = "participation_teams"
        
        fun newInstance(tabType: String, employeeId: Long): ProfileTabFragment {
            return ProfileTabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TAB_TYPE, tabType)
                    putLong(ARG_EMPLOYEE_ID, employeeId)
                }
            }
        }
    }
}

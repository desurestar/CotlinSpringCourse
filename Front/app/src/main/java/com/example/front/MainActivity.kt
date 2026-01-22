package com.example.front

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.front.data.local.PreferencesManager
import com.example.front.databinding.ActivityMainBinding
import com.example.front.ui.auth.AuthViewModel
import com.example.front.ui.auth.AuthViewModelFactory
import com.example.front.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels { 
        AuthViewModelFactory(PreferencesManager(this)) 
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupNavigation()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        
        // Show guest status in toolbar
        if (PreferencesManager(this).isGuestMode()) {
            supportActionBar?.subtitle = getString(R.string.guest_mode)
        }
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        
        // Change icon/text for guest
        if (PreferencesManager(this).isGuestMode()) {
            menu.findItem(R.id.action_logout)?.title = getString(R.string.action_login)
        }
        
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                val navController = (supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
                navController.navigate(R.id.profileFragment)
                true
            }
            R.id.menu_departments -> {
                val navController = (supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
                navController.navigate(R.id.departmentListFragment)
                true
            }
            R.id.menu_articles -> {
                val navController = (supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
                navController.navigate(R.id.articleListFragment)
                true
            }
            R.id.menu_research_teams -> {
                val navController = (supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
                navController.navigate(R.id.researchTeamListFragment)
                true
            }
            R.id.action_logout -> {
                if (PreferencesManager(this).isGuestMode()) {
                    // For guest - go to login
                    authViewModel.logout()
                    navigateToLogin()
                } else {
                    // For authenticated - show logout confirmation
                    showLogoutDialog()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showLogoutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout_title))
            .setMessage(getString(R.string.logout_message))
            .setPositiveButton(getString(R.string.action_yes)) { _, _ ->
                authViewModel.logout()
                navigateToLogin()
            }
            .setNegativeButton(getString(R.string.action_no), null)
            .show()
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

package com.example.front.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.front.R
import com.example.front.data.local.PreferencesManager
import com.example.front.databinding.ActivityRegisterBinding
import com.example.front.util.Resource
import com.example.front.util.gone
import com.example.front.util.visible

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels { 
        AuthViewModelFactory(PreferencesManager(this)) 
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRoleSpinner()
        setupObservers()
        setupListeners()
    }
    
    private fun setupRoleSpinner() {
        val roles = arrayOf(
            getString(R.string.role_student),
            getString(R.string.role_employee),
            getString(R.string.role_admin)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        binding.spinnerRole.setAdapter(adapter)
        binding.spinnerRole.setText(roles[0], false)
    }
    
    private fun setupObservers() {
        viewModel.registerResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visible()
                    binding.btnRegister.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.gone()
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(this, "Регистрация успешна! Войдите в систему", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Resource.Error -> {
                    binding.progressBar.gone()
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(this, resource.message ?: "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val role = binding.spinnerRole.text.toString()
            
            if (validateInput(email, password)) {
                viewModel.register(email, password, role)
            }
        }
        
        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_empty_email)
            return false
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            return false
        }
        
        binding.tilEmail.error = null
        
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_password)
            return false
        }
        
        if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.error_short_password)
            return false
        }
        
        binding.tilPassword.error = null
        return true
    }
}

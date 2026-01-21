package com.example.front.util

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.example.front.data.local.PreferencesManager
import com.example.front.ui.auth.LoginActivity

object GuestAccessHelper {
    
    fun isGuestMode(context: Context): Boolean {
        val preferencesManager = PreferencesManager(context)
        return preferencesManager.isGuestMode()
    }
    
    fun isAuthenticated(context: Context): Boolean {
        val preferencesManager = PreferencesManager(context)
        return preferencesManager.isAuthenticated()
    }
    
    fun showLoginRequiredDialog(context: Context, message: String = "Эта функция доступна только зарегистрированным пользователям") {
        AlertDialog.Builder(context)
            .setTitle("Требуется авторизация")
            .setMessage(message)
            .setPositiveButton("Войти") { _, _ ->
                val preferencesManager = PreferencesManager(context)
                preferencesManager.clearAuth()
                
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    
    fun checkAccessAndExecute(
        context: Context,
        action: () -> Unit,
        deniedMessage: String = "Эта функция недоступна в режиме гостя"
    ) {
        if (isGuestMode(context)) {
            showLoginRequiredDialog(context, deniedMessage)
        } else {
            action()
        }
    }
}

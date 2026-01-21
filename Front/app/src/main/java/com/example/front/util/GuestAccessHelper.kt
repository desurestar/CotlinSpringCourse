package com.example.front.util

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.example.front.R
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
    
    fun showLoginRequiredDialog(
        context: Context,
        message: String = context.getString(R.string.login_required_message)
    ) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.login_required_title))
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.action_login)) { _, _ ->
                val preferencesManager = PreferencesManager(context)
                preferencesManager.clearAuth()
                
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
            .setNegativeButton(context.getString(R.string.action_cancel), null)
            .show()
    }
    
    fun checkAccessAndExecute(
        context: Context,
        action: () -> Unit,
        deniedMessage: String = context.getString(R.string.guest_access_denied)
    ) {
        if (isGuestMode(context)) {
            showLoginRequiredDialog(context, deniedMessage)
        } else {
            action()
        }
    }
}

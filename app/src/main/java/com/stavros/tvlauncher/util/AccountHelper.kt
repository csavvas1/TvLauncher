package com.stavros.tvlauncher.util

import android.accounts.AccountManager
import android.content.Context

object AccountHelper {
    fun getFirstName(context: Context): String? {
        return try {
            val accounts = AccountManager.get(context)
                .getAccountsByType("com.google")
            accounts.firstOrNull()?.name
                ?.substringBefore("@")
                ?.substringBefore(".")
                ?.replaceFirstChar { it.uppercase() }
        } catch (e: SecurityException) {
            null
        }
    }
}

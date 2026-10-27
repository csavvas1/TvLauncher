package com.stavros.tvlauncher

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.stavros.tvlauncher.home.HomeFragment

class LauncherActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }
}

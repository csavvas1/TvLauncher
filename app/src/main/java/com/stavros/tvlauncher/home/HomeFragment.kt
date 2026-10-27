package com.stavros.tvlauncher.home

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.stavros.tvlauncher.R
import com.stavros.tvlauncher.allapps.AllAppsFragment
import com.stavros.tvlauncher.settings.SettingsFragment
import com.stavros.tvlauncher.util.AppPrefs
import com.stavros.tvlauncher.util.TimeFormatter
import com.stavros.tvlauncher.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var prefs: AppPrefs
    private val weatherRepo = WeatherRepository()
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var tvTimeDate: TextView
    private lateinit var tvWeather: TextView
    private lateinit var dockIsland: LinearLayout
    private lateinit var bezel: View
    private lateinit var dockContainer: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = AppPrefs(requireContext())

        tvTimeDate = view.findViewById(R.id.tv_time_date)
        tvWeather = view.findViewById(R.id.tv_weather)
        dockIsland = view.findViewById(R.id.dock_island)
        bezel = view.findViewById(R.id.bezel)
        dockContainer = view.findViewById(R.id.dock_container)

        setupQuickButtons(view)
        startClock()
        loadDock()
        fetchWeather()
    }

    private fun setupQuickButtons(view: View) {
        view.findViewById<ImageView>(R.id.btn_settings).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<ImageView>(R.id.btn_wifi).setOnClickListener {
            startActivity(Intent(android.provider.Settings.ACTION_WIFI_SETTINGS))
        }
    }

    private fun startClock() {
        val tick = object : Runnable {
            override fun run() {
                updateTimeDateView()
                handler.postDelayed(this, 30_000)
            }
        }
        handler.post(tick)
    }

    private fun updateTimeDateView() {
        val use24h = prefs.getTimeFormat24h()
        val time = TimeFormatter.formatTime(use24h = use24h)
        val date = TimeFormatter.formatDate()

        // Build styled span: large time | smaller date
        val pipe = "  |  "
        val full = "$time$pipe$date"
        val ssb = SpannableStringBuilder(full)

        // Pipe — very dim
        val pipeStart = time.length
        val pipeEnd = pipeStart + pipe.length
        ssb.setSpan(
            ForegroundColorSpan(0x26FFFFFF.toInt()),
            pipeStart, pipeEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Date — smaller + lighter
        ssb.setSpan(
            RelativeSizeSpan(0.52f),
            pipeEnd, full.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ssb.setSpan(
            ForegroundColorSpan(0xA6FFFFFF.toInt()),
            pipeEnd, full.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvTimeDate.text = ssb
    }

    private fun loadDock() {
        val pm = requireContext().packageManager
        val pinnedPackages = prefs.getPinnedApps()

        val items = pinnedPackages.mapNotNull { pkg ->
            try {
                val info = pm.getApplicationInfo(pkg, 0)
                AppItem(pkg, pm.getApplicationLabel(info).toString(), pm.getApplicationIcon(pkg))
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }

        dockIsland.removeAllViews()

        items.forEachIndexed { _, item ->
            val iconView = layoutInflater.inflate(R.layout.item_dock_app, dockIsland, false)
            iconView.isFocusable = true
            iconView.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    dockIsland.post {
                        BezzelAnimator.moveTo(bezel, v, dockContainer)
                    }
                }
            }
            iconView.setOnClickListener { launchApp(item.packageName) }
            iconView.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    openAllApps()
                    true
                } else false
            }
            val iv = iconView.findViewById<ImageView>(R.id.app_icon)
            iv.setImageDrawable(item.icon)
            dockIsland.addView(iconView)
        }

        // Focus first icon after layout completes
        dockIsland.post {
            dockIsland.getChildAt(0)?.requestFocus()
        }
    }

    private fun openAllApps() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AllAppsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun launchApp(packageName: String) {
        val pm = requireContext().packageManager
        val intent = pm.getLeanbackLaunchIntentForPackage(packageName)
            ?: pm.getLaunchIntentForPackage(packageName)
        intent?.let { startActivity(it) }
    }

    private fun fetchWeather() {
        viewLifecycleOwner.lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) {
                weatherRepo.fetch(prefs.getWeatherLat(), prefs.getWeatherLon())
            }
            data?.let {
                tvWeather.text = "${it.emoji} ${it.tempCelsius}°C · ${it.conditionText}"
            }
            handler.postDelayed({ fetchWeather() }, 30 * 60 * 1000L)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}

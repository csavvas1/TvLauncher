package com.stavros.tvlauncher.util

import android.content.Context
import android.content.SharedPreferences

class AppPrefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("launcher_prefs", Context.MODE_PRIVATE)

    fun getPinnedApps(): List<String> {
        val saved = prefs.getString(KEY_PINNED, null) ?: return DEFAULT_PINNED
        return saved.split(",").filter { it.isNotBlank() }
    }

    fun setPinnedApps(packages: List<String>) {
        prefs.edit().putString(KEY_PINNED, packages.joinToString(",")).apply()
    }

    fun getTimeFormat24h(): Boolean = prefs.getBoolean(KEY_24H, true)
    fun setTimeFormat24h(value: Boolean) = prefs.edit().putBoolean(KEY_24H, value).apply()

    fun getBackgroundMode(): String = prefs.getString(KEY_BG, BG_DARK) ?: BG_DARK
    fun setBackgroundMode(mode: String) = prefs.edit().putString(KEY_BG, mode).apply()

    fun getWallpaperUri(): String? = prefs.getString(KEY_WALLPAPER_URI, null)
    fun setWallpaperUri(uri: String) = prefs.edit().putString(KEY_WALLPAPER_URI, uri).apply()

    fun getWeatherLat(): Double = prefs.getFloat(KEY_LAT, 35.16f).toDouble()
    fun getWeatherLon(): Double = prefs.getFloat(KEY_LON, 33.36f).toDouble()
    fun setWeatherLocation(lat: Double, lon: Double) {
        prefs.edit()
            .putFloat(KEY_LAT, lat.toFloat())
            .putFloat(KEY_LON, lon.toFloat())
            .apply()
    }

    companion object {
        private const val KEY_PINNED = "pinned_apps"
        private const val KEY_24H = "time_24h"
        private const val KEY_BG = "bg_mode"
        private const val KEY_WALLPAPER_URI = "wallpaper_uri"
        private const val KEY_LAT = "weather_lat"
        private const val KEY_LON = "weather_lon"

        const val BG_DARK = "dark"
        const val BG_WALLPAPER = "wallpaper"
        const val BG_SLIDESHOW = "slideshow"

        val DEFAULT_PINNED = listOf(
            "com.google.android.youtube.tv",
            "com.netflix.ninja",
            "com.spotify.tv.android",
            "com.cloudmosa.puffinTV",
            "com.google.android.apps.youtube.music"
        )
    }
}

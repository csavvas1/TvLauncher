package com.stavros.tvlauncher.weather

import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class WeatherRepository {

    private val client = OkHttpClient()

    suspend fun fetch(lat: Double, lon: Double): WeatherData? = withContext(Dispatchers.IO) {
        val url = "https://api.open-meteo.com/v1/forecast" +
            "?latitude=$lat&longitude=$lon" +
            "&current_weather=true&temperature_unit=celsius"
        try {
            val request = Request.Builder().url(url).build()
            val body = client.newCall(request).execute().body?.string() ?: return@withContext null
            val json = JsonParser.parseString(body).asJsonObject
            val current = json.getAsJsonObject("current_weather")
            val temp = current.get("temperature").asDouble.toInt()
            val wmo = current.get("weathercode").asInt
            val condition = wmoCodeToCondition(wmo)
            WeatherData(temp, condition.conditionText, condition.emoji)
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        fun wmoCodeToCondition(code: Int): WeatherData {
            val (text, emoji) = when (code) {
                0 -> "Clear" to "☀️"
                1, 2 -> "Mostly Clear" to "🌤️"
                3 -> "Overcast" to "☁️"
                45, 48 -> "Foggy" to "🌫️"
                51, 53, 55 -> "Drizzle" to "🌦️"
                61, 63, 65 -> "Rain" to "🌧️"
                71, 73, 75 -> "Snow" to "❄️"
                80, 81, 82 -> "Showers" to "🌦️"
                95, 96, 99 -> "Thunderstorm" to "⛈️"
                else -> "Partly Cloudy" to "⛅"
            }
            return WeatherData(0, text, emoji)
        }
    }
}

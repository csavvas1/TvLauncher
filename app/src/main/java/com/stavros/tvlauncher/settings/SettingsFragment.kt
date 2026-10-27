package com.stavros.tvlauncher.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.stavros.tvlauncher.R
import com.stavros.tvlauncher.util.AppPrefs

class SettingsFragment : Fragment() {

    private lateinit var prefs: AppPrefs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = AppPrefs(requireContext())

        val list = view.findViewById<LinearLayout>(R.id.settings_list)

        addToggle(list, "24-hour clock", prefs.getTimeFormat24h()) {
            prefs.setTimeFormat24h(it)
        }

        addPicker(
            list,
            "Background",
            listOf("Dark", "Wallpaper", "Slideshow"),
            when (prefs.getBackgroundMode()) {
                AppPrefs.BG_WALLPAPER -> 1
                AppPrefs.BG_SLIDESHOW -> 2
                else -> 0
            }
        ) { idx ->
            prefs.setBackgroundMode(
                listOf(AppPrefs.BG_DARK, AppPrefs.BG_WALLPAPER, AppPrefs.BG_SLIDESHOW)[idx]
            )
        }
    }

    private fun addToggle(
        parent: ViewGroup,
        label: String,
        current: Boolean,
        onChange: (Boolean) -> Unit
    ) {
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 24, 0, 24)
        }
        row.addView(TextView(requireContext()).apply {
            text = label
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })
        row.addView(Switch(requireContext()).apply {
            isChecked = current
            setOnCheckedChangeListener { _, checked -> onChange(checked) }
        })
        parent.addView(row)
    }

    private fun addPicker(
        parent: ViewGroup,
        label: String,
        options: List<String>,
        selected: Int,
        onChange: (Int) -> Unit
    ) {
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 24, 0, 24)
        }
        row.addView(TextView(requireContext()).apply {
            text = label
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })
        row.addView(Spinner(requireContext()).apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
                .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            setSelection(selected)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) =
                    onChange(pos)
                override fun onNothingSelected(p: AdapterView<*>?) {}
            }
        })
        parent.addView(row)
    }
}

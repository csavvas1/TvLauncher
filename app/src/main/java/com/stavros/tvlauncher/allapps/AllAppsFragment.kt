package com.stavros.tvlauncher.allapps

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stavros.tvlauncher.R
import com.stavros.tvlauncher.home.AppItem

class AllAppsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_all_apps, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val grid = view.findViewById<RecyclerView>(R.id.all_apps_grid)
        grid.layoutManager = GridLayoutManager(requireContext(), 8)

        val pm = requireContext().packageManager
        val tvIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        val apps = pm.queryIntentActivities(tvIntent, PackageManager.GET_META_DATA).map { ri ->
            AppItem(
                ri.activityInfo.packageName,
                ri.loadLabel(pm).toString(),
                ri.loadIcon(pm)
            )
        }.sortedBy { it.label.lowercase() }

        grid.adapter = object : RecyclerView.Adapter<IconViewHolder>() {
            override fun getItemCount() = apps.size
            override fun onCreateViewHolder(parent: ViewGroup, vt: Int) = IconViewHolder(parent)
            override fun onBindViewHolder(holder: IconViewHolder, pos: Int) {
                holder.bind(apps[pos]) { pkg ->
                    val i = pm.getLeanbackLaunchIntentForPackage(pkg)
                        ?: pm.getLaunchIntentForPackage(pkg)
                    i?.let { startActivity(it) }
                }
            }
        }

        view.isFocusableInTouchMode = true
        view.requestFocus()
    }
}

class IconViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    ImageView(parent.context).apply {
        val size = (parent.context.resources.displayMetrics.density * 80).toInt()
        layoutParams = RecyclerView.LayoutParams(size, size).apply {
            setMargins(6, 6, 6, 6)
        }
        scaleType = ImageView.ScaleType.CENTER_INSIDE
        setPadding(8, 8, 8, 8)
        isFocusable = true
        setBackgroundResource(R.drawable.bg_app_icon)
    }
) {
    fun bind(item: AppItem, onClick: (String) -> Unit) {
        (itemView as ImageView).apply {
            setImageDrawable(item.icon)
            contentDescription = item.label
            setOnClickListener { onClick(item.packageName) }
        }
    }
}

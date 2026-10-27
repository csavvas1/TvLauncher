package com.stavros.tvlauncher.home

import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator

object BezzelAnimator {
    private const val DURATION = 180L
    private val interpolator = OvershootInterpolator(1.6f)

    fun moveTo(bezel: View, targetView: View, container: View) {
        val containerLoc = IntArray(2)
        val targetLoc = IntArray(2)
        container.getLocationOnScreen(containerLoc)
        targetView.getLocationOnScreen(targetLoc)

        val newX = (targetLoc[0] - containerLoc[0]).toFloat()
        val newY = (targetLoc[1] - containerLoc[1]).toFloat()
        val newW = targetView.width
        val newH = targetView.height

        if (newW == 0 || newH == 0) return

        if (bezel.visibility != View.VISIBLE) {
            // First placement — snap without animation
            val lp = bezel.layoutParams as ViewGroup.LayoutParams
            lp.width = newW
            lp.height = newH
            bezel.layoutParams = lp
            bezel.x = newX
            bezel.y = newY
            bezel.visibility = View.VISIBLE
            return
        }

        val lp = bezel.layoutParams as ViewGroup.LayoutParams
        lp.width = newW
        lp.height = newH
        bezel.layoutParams = lp

        bezel.animate().cancel()
        bezel.animate()
            .x(newX)
            .y(newY)
            .setDuration(DURATION)
            .setInterpolator(interpolator)
            .start()
    }
}

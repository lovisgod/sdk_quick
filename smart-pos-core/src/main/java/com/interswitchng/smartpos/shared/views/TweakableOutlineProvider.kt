package com.interswitchng.smartpos.shared.views

import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi

/**
 * This class represents the shadow outline for
 * the view it gets applied to.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class TweakableOutlineProvider(


    private val cornerRadius: Float = 0f,
    private val scaleX: Float  = 1f,
    private val scaleY: Float  = 1f,
    private val yShift: Int = 0,
    private val rect: Rect = Rect()) : ViewOutlineProvider() {


    override fun getOutline(view: View, outline: Outline) {
        view.background.copyBounds(rect)
        scale(rect, scaleX, scaleY)
        rect.offset(0, yShift)
        outline.setRoundRect(rect, cornerRadius)
    }

    private fun scale(rect: Rect, @FloatRange(from = -1.0, to = 1.0) scaleX: Float, @FloatRange(from = -1.0, to = 1.0) scaleY: Float) {

        val newWidth = rect.width() * scaleX
        val newHeight = rect.height() * scaleY
        val deltaX = (rect.width() - newWidth) / 2
        val deltaY = (rect.height() - newHeight) / 2

        rect.set((rect.left + deltaX).toInt(), (rect.top + deltaY).toInt(), (rect.right - deltaX).toInt(), (rect.bottom - deltaY).toInt())
    }
}
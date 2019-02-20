package com.interswitchng.smartpos.modules.card.view

import android.content.Context
import android.view.View
import android.os.Bundle
import android.os.Parcelable
import android.text.TextPaint
import android.content.res.TypedArray
import android.util.AttributeSet
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import android.graphics.*


class ProgressBar : View {


    private lateinit var textPaint: Paint
    private val rectF = RectF()
    private val percentageRect = Rect()

    private var textSize: Float = 0.toFloat()
    private var textColor: Int = 0
    private var progress = 0
    private var max: Int = 0
    private var suffixText: String = "%"

    private val defaultTextColor = Color.BLACK
    private val defaultMax = 100
    private var defaultTextSize: Float = 0f
    private var minSize: Int = 0
    private val paint = Paint()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        defaultTextSize = DisplayUtils.convertSpToPixels(12f, context)
        minSize = DisplayUtils.convertDpToPixel(100f, context).toInt()

        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.IswProgress, defStyleAttr, 0)
        initByAttributes(attributes)
        attributes.recycle()
    }

    private fun initByAttributes(attributes: TypedArray) {
        textColor = attributes.getColor(R.styleable.IswProgress_isw_progress_text_color, defaultTextColor)
        textSize = attributes.getDimension(R.styleable.IswProgress_isw_progress_text_size, defaultTextSize)

        max = attributes.getInt(R.styleable.IswProgress_isw_progress_max, defaultMax)
        progress = attributes.getInt(R.styleable.IswProgress_isw_progress_value, 0)

        attributes.getString(R.styleable.IswProgress_isw_progress_suffix_text)?.let {
            suffixText = it
        }
        invalidate()
    }

    private fun initPainters() {
        textPaint = TextPaint()
        textPaint.color = textColor
        textPaint.textSize = textSize
        textPaint.isAntiAlias = true
        paint.isAntiAlias = true
    }

    private fun getTriangle(start: Int, length: Int): Path {
        val path = Path()
        val computedLength = start + length
        val p1 = Point(start, start)
        val p2 = Point(start, computedLength)
        val p3 = Point(computedLength, computedLength)

        // draw the triangle using path
        path.moveTo(p1.x.toFloat(), p1.y.toFloat())
        path.lineTo(p2.x.toFloat(), p2.y.toFloat())
        path.lineTo(p3.x.toFloat(), p3.y.toFloat())

        return path
    }

    override fun invalidate() {
        initPainters()
        super.invalidate()
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        if (this.progress > max) {
            this.progress %= max
        }
        invalidate()
    }

    fun setMax(max: Int) {
        if (max > 0) {
            this.max = max
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        val blockLength = this.textSize.toInt()
        // calculate how many blocks fit view width
        val blocksCount = width / blockLength
        // 9 for percentage block, 1 for text block
        val percentageBlocksCount = (blocksCount * 0.9f).toInt()
        val textBlock = blocksCount - percentageBlocksCount
        // compute percentage rect
        percentageRect.set(0, 0, percentageBlocksCount - blockLength, height)




        // draw percentage rect
        paint.color = textColor
        canvas.drawRect(percentageRect, paint)

        val text = "$progress$suffixText"
        if (text.isNotEmpty()) {
            val textHeight = textPaint.descent() + textPaint.ascent()
            canvas.drawText(text, 0f, (width - textHeight) / 2.0f, textPaint)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putInt(INSTANCE_TEXT_COLOR, textColor)
        bundle.putFloat(INSTANCE_TEXT_SIZE, textSize)
        bundle.putInt(INSTANCE_MAX, progress)
        bundle.putInt(INSTANCE_PROGRESS, progress)
        bundle.putString(INSTANCE_SUFFIX, suffixText)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            textColor = state.getInt(INSTANCE_TEXT_COLOR)
            textSize = state.getFloat(INSTANCE_TEXT_SIZE)
            initPainters()
            setMax(state.getInt(INSTANCE_MAX))
            setProgress(state.getInt(INSTANCE_PROGRESS))
            suffixText = state.getString(INSTANCE_SUFFIX)!!

            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATE))
            return
        }
        super.onRestoreInstanceState(state)
    }

    companion object {
        private const val INSTANCE_STATE = "saved_instance"
        private const val INSTANCE_TEXT_COLOR = "text_color"
        private const val INSTANCE_TEXT_SIZE = "text_size"
        private const val INSTANCE_MAX = "max"
        private const val INSTANCE_PROGRESS = "progress"
        private const val INSTANCE_SUFFIX = "suffix"
    }

}
package com.azazellj.videocollages.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView
import com.azazellj.videocollages.data.FilterType


/**
 * Created by azazellj on 2/6/18.
 */
class DrawingView : ImageView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @SuppressLint("NewApi")
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private var screenRatio: Float = 16f.div(9f)
    private var percentValue: Float = 0f

    private var mPaint: Paint? = null
    var originalBitmap: Bitmap? = null
    private var mScaledBitmap: Bitmap? = null

    private var drawingAreaRect: Rect? = null

    private var mFilter: FilterType = FilterType.NORMAL

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.style = Paint.Style.FILL_AND_STROKE
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()
        screenRatio = screenWidth / screenHeight

        initFilters()
    }

    private fun initFilters() {
        mPaint!!.colorFilter = ColorMatrixColorFilter(ColorMatrix(mFilter.value))
    }

    override fun setImageBitmap(bitmap: Bitmap?) {
        originalBitmap = bitmap
        initScaledImage()
    }

    fun setCurrentFilter(filter: FilterType) {
        mFilter = filter
        initFilters()
        invalidate()
    }

    fun getCurrentFilter(): FilterType {
        return mFilter
    }

    fun setPercentage(percentage: Float) {
        this.percentValue = percentage

        drawingAreaRect = Rect(0, 0, (percentage * mScaledBitmap!!.width).toInt(), mScaledBitmap!!.height)
    }

    private fun initScaledImage() {
        if (originalBitmap == null) return

        val viewWidth = measuredWidth.toFloat()
        val viewHeight = measuredHeight.toFloat()
        val bitmapRatio = viewWidth / originalBitmap!!.width

        val newImageWidth = measuredWidth
        val newImageHeight = (originalBitmap!!.height * bitmapRatio).toInt()

        if (newImageWidth == 0 || newImageHeight == 0) return

        mScaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newImageWidth, newImageHeight, false)
        drawingAreaRect = Rect(0, 0, mScaledBitmap!!.width, mScaledBitmap!!.height)

        requestLayout()
    }

    override fun onDraw(canvas: Canvas?) {
        if (mScaledBitmap == null) return
        canvas!!.save()
        canvas.drawBitmap(mScaledBitmap, 0f, 0f, null)
        canvas.drawBitmap(mScaledBitmap, drawingAreaRect, drawingAreaRect, mPaint)
        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val currentWidth = MeasureSpec.getSize(widthMeasureSpec)
        var measuredHeight = (screenRatio * currentWidth).toInt()

        if (mScaledBitmap != null) if (mScaledBitmap!!.height != measuredHeight) measuredHeight = mScaledBitmap!!.height

        val measuredHeightSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.UNSPECIFIED)
        setMeasuredDimension(widthMeasureSpec, measuredHeightSpec)
    }

    fun getPaint(): Paint? {
        return mPaint
    }
}
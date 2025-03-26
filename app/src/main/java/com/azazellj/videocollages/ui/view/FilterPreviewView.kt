package com.azazellj.videocollages.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.azazellj.videocollages.R
import com.azazellj.videocollages.other.OnDragTouchListener
import kotlinx.android.synthetic.main.view_filter_preview.view.*


/**
 * Created by azazellj on 2/6/18.
 */
class FilterPreviewView : RelativeLayout, OnDragTouchListener.OnDragActionListener {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @SuppressLint("NewApi")
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    var drawingView: DrawingView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_filter_preview, this, true)
        dividerView.setOnTouchListener(OnDragTouchListener(dividerView, onDragActionListener = this))
        drawingView = originalImageView
    }

    override fun onDragStart(view: View?) {
    }

    override fun onDrag(view: View?, percent: Float) {
        originalImageView.setPercentage(percent)
        originalImageView.invalidate()
    }

    override fun onDragEnd(view: View?) {
    }
}
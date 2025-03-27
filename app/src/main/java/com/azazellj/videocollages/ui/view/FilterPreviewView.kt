package com.azazellj.videocollages.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.azazellj.videocollages.R
import com.azazellj.videocollages.databinding.ViewFilterPreviewBinding
import com.azazellj.videocollages.other.OnDragTouchListener


/**
 * Created by azazellj on 2/6/18.
 */
@SuppressLint("ClickableViewAccessibility")
class FilterPreviewView : RelativeLayout, OnDragTouchListener.OnDragActionListener {

    constructor(context: Context?) : super(context) {
        initBinding()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initBinding()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initBinding()
    }

    @SuppressLint("NewApi")
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initBinding()
    }

    var drawingView: DrawingView
    private lateinit var binding: ViewFilterPreviewBinding

    private fun initBinding() {
        binding = ViewFilterPreviewBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        ) // Inflate the binding
        binding.dividerView.setOnTouchListener(
            OnDragTouchListener(
                binding.dividerView,
                onDragActionListener = this
            )
        ) // Access views via binding
        drawingView = binding.originalImageView // Access views via binding
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_filter_preview, this, true)
        binding.dividerView.setOnTouchListener(
            OnDragTouchListener(
                binding.dividerView,
                onDragActionListener = this
            )
        )
        drawingView = binding.originalImageView
    }


    override fun onDragStart(view: View?) {
    }

    override fun onDrag(view: View?, percent: Float) {
        binding.originalImageView.setPercentage(percent)
        binding.originalImageView.invalidate()
    }

    override fun onDragEnd(view: View?) {
    }
}
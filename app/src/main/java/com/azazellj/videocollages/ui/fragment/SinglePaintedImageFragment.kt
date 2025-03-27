package com.azazellj.videocollages.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.azazellj.videocollages.R
import com.azazellj.videocollages.data.FilterType
import com.azazellj.videocollages.interfaces.OnFilterConfirm
import com.azazellj.videocollages.other.OnDragTouchListener
import kotlinx.android.synthetic.main.fragment_single_painted_image.*


class SinglePaintedImageFragment : DialogFragment(), OnDragTouchListener.OnDragActionListener {
    lateinit var bitmap: Bitmap
    lateinit var confirmListener: OnFilterConfirm
    private val filters: Array<String> = FilterType.values().map { it.filterName }.toTypedArray()
    var currentFilter = FilterType.NORMAL


    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_single_painted_image, container)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawingView.post {
            drawingView.setImageBitmap(bitmap)
        }

        dividerView.setOnTouchListener(OnDragTouchListener(dividerView, onDragActionListener = this, parent = rootLayout))

        filter.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, filters)
                .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); }
        filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentFilter = FilterType.fromName(filters[position])
                drawingView.setCurrentFilter(currentFilter)
            }
        }

        confirm.setOnClickListener({ confirmFilter() })
    }

    override fun onDragStart(view: View?) {
    }

    override fun onDrag(view: View?, percent: Float) {
        drawingView.setPercentage(percent)
        drawingView.invalidate()
    }

    override fun onDragEnd(view: View?) {
    }

    private fun confirmFilter() {
        confirmListener.onConfirm(currentFilter)
        dismissAllowingStateLoss()
    }
}
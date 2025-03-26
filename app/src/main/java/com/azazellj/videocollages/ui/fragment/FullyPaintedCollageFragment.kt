package com.azazellj.videocollages.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.azazellj.videocollages.R
import com.azazellj.videocollages.data.FilterType
import com.azazellj.videocollages.interfaces.OnFilterConfirm
import com.azazellj.videocollages.ui.view.DrawingView
import kotlinx.android.synthetic.main.fragment_fully_painted_collage.*


class FullyPaintedCollageFragment : DialogFragment() {
    lateinit var bitmaps: Array<Bitmap?>
    private lateinit var drawingViews: Array<DrawingView>
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
        return inflater!!.inflate(R.layout.fragment_fully_painted_collage, container)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawingViews = arrayOf(i1, i2, i3, i4, i5)
        drawingViews.forEachIndexed { index, view -> view.post({ view.setImageBitmap(bitmaps[index]) }) }

        filter.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, filters)
                .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); }
        filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentFilter = FilterType.fromName(filters[position])
                drawingViews.forEach { it.setCurrentFilter(currentFilter) }
            }
        }

        confirm.setOnClickListener({ confirmFilter() })
    }

    private fun confirmFilter() {
        confirmListener.onConfirm(currentFilter)
        dismissAllowingStateLoss()
    }
}
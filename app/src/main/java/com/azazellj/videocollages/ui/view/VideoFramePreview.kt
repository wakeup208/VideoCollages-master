package com.azazellj.videocollages.ui.view

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.azazellj.videocollages.R
import com.azazellj.videocollages.interfaces.OnCurrentFrameTimeChanged
import com.azazellj.videocollages.interfaces.OnInitListener
import com.azazellj.videocollages.tools.AppHelper
import java.io.File


/**
 * Created by azazellj on 2/12/18.
 */
class VideoFramePreview : RelativeLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    var listener: OnCurrentFrameTimeChanged? = null
    var initListener: OnInitListener? = null
    var file: File? = null
    var index: Int = 0
        set(value) {
            field = value
            header.text = "Frame #${value + 1}"
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_video_frame_preview, this, true)

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()
        val screenRatio = screenWidth / screenHeight
        val newImageWidth = screenWidth
        val newImageHeight = newImageWidth * screenRatio
        preview.layoutParams.height = newImageHeight.toInt()


        frameSeeker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) setScaledThumbnail()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                listener?.frameTimeChanged(seekBar!!.progress, index)
                setScaledThumbnail()
            }
        })
    }

    private fun setScaledThumbnail() {
        Thread(Runnable {
            val image = optimizeImage(AppHelper.getFrame(frameSeeker.progress, file!!))
            Handler(Looper.getMainLooper()).post({
                preview.setImageBitmap(image)
                initListener?.initFinished()
            })
        }).start()
    }

    fun setMaxTime(time: Int) {
        frameSeeker.max = time
    }

    fun redrawPreview(time: Int) {
        if (frameSeeker.progress != time) frameSeeker.progress = time
    }

    fun optimizeImage(bitmap: Bitmap): Bitmap {
        val width = measuredWidth
        val newBitmapWidth = width
        val newBitmapHeight = (width / bitmap.width.toFloat()) * bitmap.height
        val result = Bitmap.createScaledBitmap(bitmap, newBitmapWidth, newBitmapHeight.toInt(), false)
        bitmap.recycle()
        return result
    }
}
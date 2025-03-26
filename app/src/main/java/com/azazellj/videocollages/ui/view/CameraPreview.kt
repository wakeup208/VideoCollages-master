package com.azazellj.videocollages.ui.view

import android.annotation.TargetApi
import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.Camera
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View

/**
 * Created by azazellj on 2/12/18.
 */

class CameraPreview : SurfaceView, SurfaceHolder.Callback {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private val mHolder: SurfaceHolder = holder
    private var mCamera: Camera? = null

    init {
        mHolder.addCallback(this)
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        setOnClickListener({ mCamera!!.autoFocus { success, camera -> camera.cancelAutoFocus() } })
        systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    fun setCamera(camera: Camera) {
        mCamera = camera
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (mCamera == null) return
        mCamera!!.setPreviewDisplay(holder)
        mCamera!!.startPreview()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        if (mHolder.surface == null || mCamera == null) {
            return
        }
        try {
            mCamera!!.stopPreview()
        } catch (e: Exception) {
        }

        try {
            mCamera!!.setPreviewDisplay(mHolder)
            mCamera!!.startPreview()

        } catch (e: Exception) {
            Log.d(TAG, "Error starting camera preview: " + e.message)
        }
    }
}
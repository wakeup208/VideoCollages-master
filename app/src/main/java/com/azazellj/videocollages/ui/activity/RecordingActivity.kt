package com.azazellj.videocollages.ui.activity

import android.content.Intent
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.azazellj.videocollages.R
import com.azazellj.videocollages.tools.AppHelper
import kotlinx.android.synthetic.main.activity_recording.*
import java.io.File
import java.io.IOException


class RecordingActivity : AppCompatActivity() {
    private var mCamera: Camera? = null
    private var mMediaRecorder: MediaRecorder? = null
    private var mOutputFile: File? = null
    private var profile: CamcorderProfile? = null

    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)
        play.setOnClickListener({ onCaptureClick() })
        showPreview()
    }

    private fun getCamera(): Camera? {
        val camera: Camera = AppHelper.defaultBackFacingCameraInstance ?: return null

        val parameters = camera.parameters
        val mSupportedPreviewSizes = parameters.supportedPreviewSizes
        val mSupportedVideoSizes = parameters.supportedVideoSizes
        val optimalSize = AppHelper.getOptimalVideoSize(mSupportedVideoSizes, mSupportedPreviewSizes,
                resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)

        profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
        profile!!.videoFrameWidth = optimalSize!!.width
        profile!!.videoFrameHeight = optimalSize.height

        parameters.setPreviewSize(profile!!.videoFrameWidth, profile!!.videoFrameHeight)
        parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        camera.parameters = parameters

        return camera
    }

    private fun showPreview() {
        mCamera = getCamera()
        if (mCamera == null) return

        mPreview.setCamera(mCamera!!)
    }

    private fun onCaptureClick() {
        if (isRecording) {
            try {
                mMediaRecorder!!.stop()
            } catch (e: RuntimeException) {
                mOutputFile?.delete()
            }

            releaseMediaRecorder()
            mCamera!!.lock()
            isRecording = false
            releaseCamera()
            openFraming()
        } else {
            if (prepareVideoRecorder()) {
                mMediaRecorder!!.start()
                isRecording = true
                play.setColorFilter(ContextCompat.getColor(this, R.color.red_500))
            } else {
                releaseMediaRecorder()
            }
        }
    }

    private fun openFraming() {
        val intent = Intent(this, FramingVideoActivity::class.java)
        intent.putExtra(FramingVideoActivity.ARG_VIDEO_PATH, mOutputFile!!.path)
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        releaseMediaRecorder()
        releaseCamera()
    }

    private fun releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder!!.reset()
            mMediaRecorder!!.release()
            mMediaRecorder = null
            mCamera!!.lock()
        }
    }

    private fun releaseCamera() {
        mCamera!!.release()
    }

    private fun prepareVideoRecorder(): Boolean {
        mMediaRecorder = MediaRecorder()
        mCamera!!.unlock()
        mMediaRecorder!!.setCamera(mCamera)
        mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        mMediaRecorder!!.setProfile(profile)

        mOutputFile = AppHelper.getOutputMediaFile(AppHelper.MEDIA_TYPE_VIDEO)
        if (mOutputFile == null) {
            return false
        }
        mMediaRecorder!!.setOutputFile(mOutputFile!!.path)
        try {
            mMediaRecorder!!.prepare()
        } catch (e: IllegalStateException) {
            releaseMediaRecorder()
            return false
        } catch (e: IOException) {
            releaseMediaRecorder()
            return false
        }

        return true
    }
}

package com.azazellj.videocollages.tools

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.hardware.Camera
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object AppHelper {
    const val MEDIA_TYPE_IMAGE = 1
    const val MEDIA_TYPE_VIDEO = 2

    val defaultBackFacingCameraInstance: Camera?
        get() = getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_BACK)

    val defaultFrontFacingCameraInstance: Camera?
        get() = getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)

    fun getOptimalVideoSize(supportedVideoSizes: List<Camera.Size>?,
                            previewSizes: List<Camera.Size>, w: Int, h: Int): Camera.Size? {
        // Use a very small tolerance because we want an exact match.
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = w.toDouble() / h

        // Supported video sizes list might be null, it means that we are allowed to use the preview
        // sizes
        val videoSizes: List<Camera.Size>
        if (supportedVideoSizes != null) {
            videoSizes = supportedVideoSizes
        } else {
            videoSizes = previewSizes
        }
        var optimalSize: Camera.Size? = null

        // Start with max value and refine as we iterate over available video sizes. This is the
        // minimum difference between view and camera height.
        var minDiff = java.lang.Double.MAX_VALUE

        // Target view height

        // Try to find a video size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (size in videoSizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue
            if (Math.abs(size.height - h) < minDiff && previewSizes.contains(size)) {
                optimalSize = size
                minDiff = Math.abs(size.height - h).toDouble()
            }
        }

        // Cannot find video size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in videoSizes) {
                if (Math.abs(size.height - h) < minDiff && previewSizes.contains(size)) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - h).toDouble()
                }
            }
        }
        return optimalSize
    }

    /**
     *
     * @param position Physical position of the camera i.e Camera.CameraInfo.CAMERA_FACING_FRONT
     * or Camera.CameraInfo.CAMERA_FACING_BACK.
     * @return the default camera on the device. Returns null if camera is not available.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private fun getDefaultCamera(position: Int): Camera? {
        // Find the total number of cameras available
        val mNumberOfCameras = Camera.getNumberOfCameras()

        // Find the ID of the back-facing ("default") camera
        val cameraInfo = Camera.CameraInfo()
        for (i in 0 until mNumberOfCameras) {
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == position) {
                return Camera.open(i)

            }
        }

        return null
    }

    /**
     * Creates a media file in the `Environment.DIRECTORY_PICTURES` directory. The directory
     * is persistent and available to other applications like gallery.
     *
     * @param type Media type. Can be video or image.
     * @return A file object pointing to the newly created file.
     */
    fun getOutputMediaFile(type: Int): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED, ignoreCase = true)) {
            return null
        }

        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "VideoCollages")
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("VideoCollages", "failed to create directory")
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val mediaFile: File
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = File(mediaStorageDir.path + File.separator +
                    "IMG_" + timeStamp + ".jpg")
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = File(mediaStorageDir.path + File.separator +
                    "VID_" + timeStamp + ".mp4")
        } else {
            return null
        }

        return mediaFile
    }

    fun getDuration(file: File): Int {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val duration: String? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        return duration?.toInt() ?: return 0
    }

    fun getFrame(position: Int, videoFile: File): Bitmap {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoFile.absolutePath)
        val time = position.toLong() * 1000
        val frame = retriever.getFrameAtTime(time)
        retriever.release()
        return frame!!
    }

    fun getTimeSections(file: File): IntArray {
        val durations = IntArray(5)
        val duration = getDuration(file)

        val step = duration / durations.size

        for (index in 0 until durations.size) {
            durations[index] = index * step + step / 2
        }

        return durations
    }
}

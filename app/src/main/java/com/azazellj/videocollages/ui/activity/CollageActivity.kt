package com.azazellj.videocollages.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azazellj.videocollages.R
import com.azazellj.videocollages.data.FilterType
import com.azazellj.videocollages.interfaces.OnFilterConfirm
import com.azazellj.videocollages.tools.AppHelper
import com.azazellj.videocollages.ui.fragment.FullyPaintedCollageFragment
import com.azazellj.videocollages.ui.fragment.SinglePaintedImageFragment
import com.azazellj.videocollages.ui.view.DrawingView
import java.io.File
import java.io.FileOutputStream


class CollageActivity : AppCompatActivity() {
    companion object {
        const val ARG_DURATIONS = "durations"
        const val ARG_FILE = "file"
    }

    private lateinit var durations: IntArray
    private lateinit var file: File
    private var bitmaps: Array<Bitmap?> = arrayOfNulls(5)
    private lateinit var drawingViews: Array<DrawingView>
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collage)

        durations = intent.getIntArrayExtra(ARG_DURATIONS)
        file = File(intent.getStringExtra(ARG_FILE))
        drawingViews = arrayOf(i1, i2, i3, i4, i5)
        drawingViews.forEach { it.setOnClickListener({ showSinglePaintedImage(it as DrawingView) }) }

        commonFilter.setOnClickListener({ showPaintAllFragment() })
        saveToGallery.setOnClickListener({ saveImageToFile(false) })
        share.setOnClickListener({ saveImageToFile(true) })
        loadBitmaps()
    }

    private fun loadBitmaps() {
        Thread(Runnable {
            durations.forEachIndexed { index, timestamp -> bitmaps[index] = AppHelper.getFrame(timestamp, file) }
            runOnUiThread {
                drawingViews.forEachIndexed { index, view -> view.setImageBitmap(bitmaps[index]) }
                progress.visibility = View.GONE
            }
        }).start()
    }

    private fun showPaintAllFragment() {
        val fragment = FullyPaintedCollageFragment()
        fragment.bitmaps = bitmaps
        fragment.confirmListener = object : OnFilterConfirm {
            override fun onConfirm(filter: FilterType) {
                drawingViews.forEach { it.setCurrentFilter(filter) }
            }
        }
        fragment.show(supportFragmentManager, fragment.toString())
    }

    private fun showSinglePaintedImage(view: DrawingView) {
        val fragment = SinglePaintedImageFragment()
        fragment.bitmap = view.originalBitmap!!
        fragment.currentFilter = view.getCurrentFilter()
        fragment.confirmListener = object : OnFilterConfirm {
            override fun onConfirm(filter: FilterType) {
                view.setCurrentFilter(filter)
            }
        }
        fragment.show(supportFragmentManager, fragment.toString())
    }

    private lateinit var canvasBitmap: Bitmap
    private lateinit var canvas: Canvas

    fun saveImageToFile(share: Boolean) {
        //todo request permissions
        val bitmaps: Array<Bitmap?> = arrayOf(i1.originalBitmap, i2.originalBitmap, i3.originalBitmap, i4.originalBitmap, i5.originalBitmap)

        if (bitmaps.any { it == null }) return
        val finalBitmaps: Array<Bitmap> = bitmaps as Array<Bitmap>

        val canvasBitmapWidth: Int = finalBitmaps[0].width + finalBitmaps[1].width
        val canvasBitmapHeight: Int = finalBitmaps[0].height + finalBitmaps[2].height * 2 + finalBitmaps[3].height
        canvasBitmap = Bitmap.createBitmap(canvasBitmapWidth, canvasBitmapHeight, Bitmap.Config.ARGB_8888)

        canvas = Canvas(canvasBitmap)
        canvas.drawBitmap(finalBitmaps[0], 0f, 0f, i1.getPaint())
        canvas.drawBitmap(finalBitmaps[1], finalBitmaps[0].width.toFloat(), 0f, i2.getPaint())
        val dstRect = Rect(0, finalBitmaps[0].height, finalBitmaps[2].width * 2, finalBitmaps[0].height + finalBitmaps[2].height * 2)
        canvas.drawBitmap(finalBitmaps[2], null, dstRect, i3.getPaint())
        canvas.drawBitmap(finalBitmaps[3], 0f, finalBitmaps[0].height.toFloat() + finalBitmaps[2].height.toFloat() * 2, i4.getPaint())
        canvas.drawBitmap(finalBitmaps[4], finalBitmaps[3].width.toFloat(), finalBitmaps[0].height.toFloat() + finalBitmaps[2].height.toFloat() * 2, i5.getPaint())

        val file = AppHelper.getOutputMediaFile(AppHelper.MEDIA_TYPE_IMAGE)
        if (file!!.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            canvasBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(applicationContext, "Saved Successfully", Toast.LENGTH_LONG).show()

            MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null)
            { path, uri ->
                imageUri = uri
                if (share) shareImage()

                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=" + uri)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun shareImage() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
        startActivity(Intent.createChooser(intent, "Share Image"))
    }
}

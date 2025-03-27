package com.azazellj.videocollages.ui.activity

import android.content.Intent
import android.os.Bundle

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.azazellj.videocollages.R
import com.azazellj.videocollages.interfaces.OnCurrentFrameTimeChanged
import com.azazellj.videocollages.interfaces.OnInitListener
import com.azazellj.videocollages.tools.AppHelper
import com.azazellj.videocollages.ui.view.VideoFramePreview
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_framing_video.*
import java.io.File

class FramingVideoActivity : AppCompatActivity() {
    companion object {
        const val ARG_VIDEO_PATH = "video_path"
    }

    private lateinit var file: File
    private lateinit var durations: IntArray
    private var maxDuration: Int = 0

    private val subject: BehaviorSubject<Int> = BehaviorSubject.create<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_framing_video)

        file = File(intent.getStringExtra(ARG_VIDEO_PATH))

        loadFrames()
        addViews()
    }

    private fun addViews() {
        subject.buffer(5)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    progress.visibility = View.INVISIBLE
                    subject.unsubscribeOn(AndroidSchedulers.mainThread())
                })

        for (index in 0 until durations.size) {
            val preview = VideoFramePreview(this)
            preview.file = file
            preview.index = index
            preview.listener = object : OnCurrentFrameTimeChanged {
                override fun frameTimeChanged(position: Int, index: Int) {
                    durations[index] = position
                    preview.redrawPreview(position)
                }
            }
            preview.initListener = object : OnInitListener {
                override fun initFinished() {
                    subject.onNext(preview.id)
                }
            }
            parentView.addView(preview)
            preview.post({
                preview.setMaxTime(maxDuration)
                preview.redrawPreview(durations[index])
            })
        }

        val textView = AppCompatTextView(this)
        textView.text = "Next"
        textView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).also { it.topMargin = 100 }
        textView.textSize = 100f
        textView.setOnClickListener({ openCollageActivity() })
        parentView.addView(textView)
    }

    private fun openCollageActivity() {
        val intent = Intent(this, CollageActivity::class.java)
        intent.putExtra(CollageActivity.ARG_DURATIONS, durations)
        intent.putExtra(CollageActivity.ARG_FILE, file.path)
        startActivity(intent)
        finish()
    }

    private fun loadFrames() {
        durations = AppHelper.getTimeSections(file)
        maxDuration = AppHelper.getDuration(file)
    }
}

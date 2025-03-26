package com.azazellj.videocollages.other

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

/**
 * Created by azazellj on 2/5/18.
 */
class OnDragTouchListener constructor(view: View, parent: ViewGroup = view.parent as ViewGroup, onDragActionListener: OnDragActionListener? = null) : View.OnTouchListener {

    private var mView: View? = null
    private var mParent: ViewGroup? = null
    private var isDragging: Boolean = false
    private var isInitialized = false

    private var width: Int = 0
    private var xWhenAttached: Float = 0f
    private var maxLeft: Float = 0.toFloat()
    private var maxRight: Float = 0.toFloat()
    private var dX: Float = 0.toFloat()

    private var height: Int = 0
    private var yWhenAttached: Float = 0.toFloat()
    private var maxTop: Float = 0.toFloat()
    private var maxBottom: Float = 0.toFloat()
    private var dY: Float = 0.toFloat()

    private var mOnDragActionListener: OnDragActionListener? = null

    /**
     * Callback used to indicate when the drag is finished
     */
    interface OnDragActionListener {
        /**
         * Called when drag event is started
         *
         * @param view The view dragged
         */
        fun onDragStart(view: View?)

        fun onDrag(view: View?, percent: Float)

        /**
         * Called when drag event is completed
         *
         * @param view The view dragged
         */
        fun onDragEnd(view: View?)
    }

    constructor(view: View, onDragActionListener: OnDragActionListener) : this(view, view.parent as ViewGroup, onDragActionListener) {
        this.mOnDragActionListener = onDragActionListener
    }

    init {
        initListener(view, parent)
        setOnDragActionListener(onDragActionListener)
    }

    fun setOnDragActionListener(onDragActionListener: OnDragActionListener?) {
        mOnDragActionListener = onDragActionListener
    }

    fun initListener(view: View, parent: ViewGroup) {
        mView = view
        mParent = parent
        isDragging = false
        isInitialized = false
    }

    fun updateBounds() {
        updateViewBounds()
        updateParentBounds()
        isInitialized = true
    }

    fun updateViewBounds() {
        width = mView!!.measuredWidth
        xWhenAttached = mView!!.x
        dX = 0f

        height = mView!!.measuredHeight
        yWhenAttached = mView!!.y
        dY = 0f
    }

    fun updateParentBounds() {
        maxLeft = 0f
        maxRight = maxLeft + mParent!!.measuredWidth

        maxTop = 0f
        maxBottom = maxTop + mParent!!.measuredHeight
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (isDragging) {
            val bounds = FloatArray(4)
            // LEFT
            bounds[0] = event.rawX + dX
            if (bounds[0] < maxLeft) {
                bounds[0] = maxLeft
            }
            // RIGHT
            bounds[2] = bounds[0] + width
            if (bounds[2] > maxRight) {
                bounds[2] = maxRight
                bounds[0] = bounds[2] - width
            }
            // TOP
            bounds[1] = event.rawY + dY
            if (bounds[1] < maxTop) {
                bounds[1] = maxTop
            }
            // BOTTOM
            bounds[3] = bounds[1] + height
            if (bounds[3] > maxBottom) {
                bounds[3] = maxBottom
                bounds[1] = bounds[3] - height
            }

            mParent!!.requestDisallowInterceptTouchEvent(true)

            when (event.action) {
                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> onDragFinish()
                MotionEvent.ACTION_MOVE -> {
                    mView!!.x = bounds[0]
                    mOnDragActionListener?.onDrag(mView, ((bounds[0] + bounds[2]) / 2) / mParent!!.measuredWidth.toFloat())
                }
            }
            return true
        } else {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.e("LOG", "dragging")

                    isDragging = true
                    if (!isInitialized) {
                        updateBounds()
                    }
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                    if (mOnDragActionListener != null) {
                        mOnDragActionListener!!.onDragStart(mView)
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun onDragFinish() {
        if (mOnDragActionListener != null) {
            mOnDragActionListener!!.onDragEnd(mView)
        }

        Log.e("LOG", "dragging finish")

        dX = 0f
        dY = 0f
        isDragging = false
    }
}
package com.azazellj.videocollages.interfaces

import com.azazellj.videocollages.data.FilterType

/**
 * Created by azazellj on 2/19/18.
 */
interface OnFilterConfirm {
    fun onConfirm(filter: FilterType)
}
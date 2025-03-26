package com.azazellj.videocollages.data

/**
 * Created by azazellj on 17.02.18.
 */
enum class FilterType(val value: FloatArray, val filterName: String) {
    NORMAL(floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f), "Normal"),
    GRAYSCALE(floatArrayOf(
            0.3f, 0.59f, 0.11f, 0f, 0f,
            0.3f, 0.59f, 0.11f, 0f, 0f,
            0.3f, 0.59f, 0.11f, 0f, 0f,
            0.0f, 0.00f, 0.00f, 1f, 0f), "Gray scale"),
    INVERTED(floatArrayOf(
            -1f, +0f, +0f, 0f, 255f,
            +0f, -1f, +0f, 0f, 255f,
            +0f, +0f, -1f, 0f, 255f,
            +0f, +0f, +0f, 1f, 0.0f), "Inverted"),
    SEPIA(floatArrayOf(
            0.393f, 0.769f, 0.189f, 0f, 0f,
            0.349f, 0.686f, 0.168f, 0f, 0f,
            0.272f, 0.534f, 0.131f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f), "Sepia"),
    BLACK_AND_WHITE(floatArrayOf(
            1.1f, 1.1f, 1.1f, 0f, -0.05f,
            1.1f, 1.1f, 1.1f, 0f, -0.05f,
            1.1f, 1.1f, 1.1f, 0f, -0.05f,
            0f, 0f, 0f, 1f, 0f), "B&W");

    companion object {
        fun fromName(visibleName: String): FilterType {
            return values().find { it.filterName == visibleName } ?: NORMAL
        }
    }
}
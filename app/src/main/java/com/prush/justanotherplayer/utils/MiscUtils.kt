package com.prush.justanotherplayer.utils

import java.util.*
import kotlin.math.abs

fun getTimeStringFromSeconds(secs: Long): String {

    val absSeconds = abs(secs)
    val stringBuilder = StringBuilder()
    stringBuilder.setLength(0)

    return Formatter(stringBuilder, Locale.getDefault()).format(
        "%s %d:%02d",
        if (secs < 0) "- " else "",
        absSeconds / 60,
        absSeconds % 60
    ).toString()
}
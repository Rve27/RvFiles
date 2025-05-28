/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.file

import android.content.Context
import android.text.format.DateUtils
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/* @see com.android.documentsui.base.Shared#formatTime(Context, long) */
fun Instant.formatShort(context: Context): String {
    val time = toEpochMilli()
    val then = ZonedDateTime.ofInstant(this, ZoneId.systemDefault())
    val now = ZonedDateTime.now()
    val flags = DateUtils.FORMAT_NO_NOON or DateUtils.FORMAT_NO_MIDNIGHT or
        DateUtils.FORMAT_ABBREV_ALL or when {
            then.year != now.year -> DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_SHOW_DATE
            then.dayOfYear != now.dayOfYear -> DateUtils.FORMAT_SHOW_DATE
            else -> DateUtils.FORMAT_SHOW_TIME
        }
    return DateUtils.formatDateTime(context, time, flags)
}

fun Instant.formatLong(): String =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withZone(ZoneId.systemDefault())
        .format(this)

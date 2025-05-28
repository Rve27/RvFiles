/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.filelist

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java8.nio.file.Path
import me.zhanghai.android.files.provider.common.PathObservable
import me.zhanghai.android.files.provider.common.observe
import me.zhanghai.android.files.util.closeSafe
import java.io.Closeable
import java.io.IOException

class PathObserver(path: Path, @MainThread onChange: () -> Unit) : Closeable {
    private var pathObservable: PathObservable? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var closed = false
    private val lock = Any()

    init {
        coroutineScope.launch {
            synchronized(lock) {
                if (closed) {
                    return@launch
                }
                pathObservable = try {
                    path.observe(THROTTLE_INTERVAL_MILLIS)
                } catch (e: UnsupportedOperationException) {
                    // Ignored.
                    return@launch
                } catch (e: IOException) {
                    // Ignored.
                    e.printStackTrace()
                    return@launch
                }.apply {
                    val mainHandler = Handler(Looper.getMainLooper())
                    addObserver { mainHandler.post(onChange) }
                }
            }
        }
    }

    override fun close() {
        coroutineScope.launch {
            synchronized(lock) {
                if (closed) {
                    return@launch
                }
                closed = true
                pathObservable?.closeSafe()
            }
        }
    }

    companion object {
        private const val THROTTLE_INTERVAL_MILLIS = 1000L
    }
}

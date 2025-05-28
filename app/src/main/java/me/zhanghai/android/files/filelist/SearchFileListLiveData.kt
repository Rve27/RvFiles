/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.filelist

import java8.nio.file.Path
import me.zhanghai.android.files.file.FileItem
import me.zhanghai.android.files.file.loadFileItem
import me.zhanghai.android.files.provider.common.search
import me.zhanghai.android.files.util.CloseableLiveData
import me.zhanghai.android.files.util.Failure
import me.zhanghai.android.files.util.Loading
import me.zhanghai.android.files.util.Stateful
import me.zhanghai.android.files.util.Success
import me.zhanghai.android.files.util.valueCompat
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFileListLiveData(
    private val path: Path,
    private val query: String
) : CloseableLiveData<Stateful<List<FileItem>>>() {
    private var job: Job? = null
    private val executor = Executors.newSingleThreadExecutor()

    init {
        loadValue()
    }

    fun loadValue() {
        job?.cancel()
        value = Loading(emptyList())
        job = CoroutineScope(Dispatchers.Main).launch {
            try {
                val fileList = withContext(Dispatchers.IO) {
                    val list = mutableListOf<FileItem>()
                    path.search(query, INTERVAL_MILLIS) { paths: List<Path> ->
                        for (path in paths) {
                            val fileItem = try {
                                path.loadFileItem()
                            } catch (e: IOException) {
                                e.printStackTrace()
                                // TODO: Support file without information.
                                continue
                            }
                            list.add(fileItem)
                        }
                        postValue(Loading(list.toList()))
                    }
                    list
                }
                postValue(Success(fileList))
            } catch (e: Exception) {
                // TODO: Retrieval of previous value is racy.
                postValue(Failure(valueCompat.value, e))
            }
        }
    }

    override fun close() {
        job?.cancel()
        executor.shutdown()
    }

    companion object {
        private const val INTERVAL_MILLIS = 500L
    }
}

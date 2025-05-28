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
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        loadValue()
    }

    fun loadValue() {
        job?.cancel()
        value = Loading(emptyList())
        job = coroutineScope.launch {
            val fileList = mutableListOf<FileItem>()
            try {
                path.search(query, INTERVAL_MILLIS) { paths: List<Path> ->
                    for (path in paths) {
                        val fileItem = try {
                            path.loadFileItem()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            // TODO: Support file without information.
                            continue
                        }
                        fileList.add(fileItem)
                    }
                    postValue(Loading(fileList.toList()))
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
    }

    companion object {
        private const val INTERVAL_MILLIS = 500L
    }
}

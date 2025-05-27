/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.compat

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.ParcelCompat

fun Parcel.readBooleanCompat(): Boolean = ParcelCompat.readBoolean(this)

fun Parcel.writeBooleanCompat(value: Boolean) {
    ParcelCompat.writeBoolean(this, value)
}

@Suppress("DEPRECATION")
fun <E : Parcelable?, L : MutableList<E>> Parcel.readParcelableListCompat(
    list: L,
    classLoader: ClassLoader?
): L {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        @Suppress("UNCHECKED_CAST")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            readParcelableList(list, E::class.java, classLoader) as L
        } else {
            readParcelableList(list, classLoader) as L
        }
    } else {
        val size = readInt()
        if (size == -1) {
            list.clear()
            return list
        }
        val listSize = list.size
        for (index in 0..<size) {
            @Suppress("UNCHECKED_CAST")
            val element = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                readParcelable(classLoader, E::class.java)
            } else {
                readParcelable<E>(classLoader)
            } as E
            if (index < listSize) {
                list[index] = element
            } else {
                list += element
            }
        }
        if (size < listSize) {
            list.subList(size, listSize).clear()
        }
        return list
    }
}

fun <T : Parcelable?> Parcel.writeParcelableListCompat(value: List<T>?, flags: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        writeParcelableList(value, flags)
    } else {
        if (value == null) {
            writeInt(-1)
            return
        }
        writeInt(value.size)
        for (element in value) {
            writeParcelable(element, flags)
        }
    }
}

@Suppress("UNCHECKED_CAST", "DEPRECATION")
fun <T> Parcel.readSerializableCompat(): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        readSerializable(null, T::class.java) as T?
    } else {
        readSerializable() as T?
    }
}

/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.nonfree

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.pm.Signature
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import me.zhanghai.android.files.BuildConfig
import me.zhanghai.android.files.app.application
import me.zhanghai.android.files.app.packageManager
import me.zhanghai.android.files.util.getPackageInfoOrNull

object CrashlyticsInitializer {
    private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

    fun initialize() {
        if (BuildConfig.DEBUG) {
            return
        }
        if (!verifyPackageName() || !verifySignature()) {
            // Please, don't spam.
            return
        }
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }

    private fun verifyPackageName(): Boolean {
        return application.packageName == "me.rve.files"
    }

    @SuppressLint("PackageManagerGetSignatures")
    private fun verifySignature(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val packageInfo = packageManager.getPackageInfoOrNull(
                application.packageName, PackageManager.GET_SIGNING_CERTIFICATES
            ) ?: return false
            val signingInfo = packageInfo.signingInfo ?: return false
            val signatures = signingInfo.apkContentsSigners
            return signatures.size == 1 &&
                computeCertificateFingerprint(signatures[0]) == "E1:33:2C:F9:5E:5E:27:A5:88:00:36:3A:CB:9D:C7:C3:8B:A4:AE:B9:35:6F:96:27:7C:8D:1B:AB:31:58:AB:D0"
        } else {
            @Suppress("DEPRECATION")
            val packageInfo = packageManager.getPackageInfoOrNull(
                application.packageName, PackageManager.GET_SIGNATURES
            ) ?: return false
            @Suppress("DEPRECATION")
            val signatures = packageInfo.signatures ?: return false
            return signatures.size == 1 &&
                computeCertificateFingerprint(signatures[0]) == "E1:33:2C:F9:5E:5E:27:A5:88:00:36:3A:CB:9D:C7:C3:8B:A4:AE:B9:35:6F:96:27:7C:8D:1B:AB:31:58:AB:D0"
        }
    }

    private fun computeCertificateFingerprint(certificate: Signature): String {
        val messageDigest = try {
            MessageDigest.getInstance("SHA-256")
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        }
        val digest = messageDigest.digest(certificate.toByteArray())
        val chars = CharArray(3 * digest.size - 1)
        for (index in digest.indices) {
            val byte = digest[index].toInt() and 0xFF
            chars[3 * index] = HEX_CHARS[byte ushr 4]
            chars[3 * index + 1] = HEX_CHARS[byte and 0x0F]
            if (index < digest.size - 1) {
                chars[3 * index + 2] = ':'
            }
        }
        return String(chars)
    }
}

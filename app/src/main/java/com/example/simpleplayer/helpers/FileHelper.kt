package com.example.simpleplayer.helpers

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class FileHelper(context: Context) {

    private val mContext: Context

    init {
        mContext = context
        Log.d(TAG, "Initialize File Helper")
    }

    fun getRootPath(): String {
        var path: String = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file: File =
                ContextCompat.getExternalFilesDirs(mContext.applicationContext, null)[0]
            file.absolutePath
        } else {
            mContext.applicationContext.filesDir.absolutePath
        }

        return path
    }

    fun checkMD5File(md5: String, file: File): Boolean {
        if (TextUtils.isEmpty(md5) || file == null) {
            return false
        }

        val calculatedDigest: String = calculateMd5(file)
            ?: return false

        return calculatedDigest.equals(md5, ignoreCase = true)
    }

    private fun calculateMd5(updateFile: File): String? {
        val digest: MessageDigest = try {
            MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            return null
        }
        val `is`: InputStream = try {
            FileInputStream(updateFile)
        } catch (e: FileNotFoundException) {
            return null
        }
        val buffer = ByteArray(8192)
        var read: Int
        return try {
            while (`is`.read(buffer).also { read = it } > 0) {
                digest.update(buffer, 0, read)
            }
            val md5sum = digest.digest()
            val bigInt = BigInteger(1, md5sum)
            var output = bigInt.toString(16)
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0')
            output
        } catch (e: IOException) {
            throw RuntimeException("Unable to process file for MD5", e)
        } finally {
            try {
                `is`.close()
            } catch (ignored: IOException) {
            }
        }
    }

    companion object {
        const val TAG = "FILE_HELPER"
    }

}
package com.example.simpleplayer.actions

import android.content.Context
import android.util.Log
import com.downloader.Error
import com.downloader.OnCancelListener
import com.downloader.OnDownloadListener
import com.downloader.OnPauseListener
import com.downloader.OnProgressListener
import com.downloader.OnStartOrResumeListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.example.simpleplayer.constants.CONTENT_DIRECTORY
import com.example.simpleplayer.helpers.FileHelper
import com.example.simpleplayer.models.Content
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

abstract class DownloadContent(context: Context, data: JSONArray) {

    private val mContext: Context
    private val mData: JSONArray
    var mPath: String
    lateinit var mFileName: String
    lateinit var mFileUrl: String
    lateinit var mChecksum: String
    var mTotalFile: Int = 0
    var mIndexFile: Int = 0

    init {
        mContext = context
        mData = data
        mTotalFile = mData.length()
        mIndexFile = 0
        mPath = FileHelper(mContext).getRootPath() + CONTENT_DIRECTORY

        Log.d(TAG, "Data : $mData")

        PRDownloader.initialize(
            mContext,
            PRDownloaderConfig.newBuilder().setDatabaseEnabled(true).build()
        )

        process(mData, mIndexFile)
    }

    private fun process(data: JSONArray, index: Int) {
        val item: JSONObject = data.optJSONObject(mIndexFile)
        val content: Content =
            Content(
                index,
                item.optString("url"),
                item.optString("name"),
                item.optString("checksum")
            )
        val file: File = File(mPath + content.name)
        mFileName = content.name
        mFileUrl = content.url
        mChecksum = content.checksum
        Log.d(TAG, "Download Content $index : $item")
        if (!file.exists()) {
            process(data, mIndexFile)
            mIndexFile++
        } else {
            if (mIndexFile == mTotalFile - 1) {
                mIndexFile++
            } else {
                mIndexFile++
                process(data, mIndexFile)
            }
        }
    }

    abstract fun onDownloadProgress(
        fileName: String,
        rogressFile: Long,
        progressPercent: Long,
        totalFile: Int
    )

    abstract fun onDownloadNext(
        fileName: String,
        rogressFile: Long,
        progressPercent: Long,
        totalFile: Int
    )

    abstract fun onDownloadComplete(totalFile: Int)

    companion object {
        const val TAG = "DOWNLOAD_CONTENT"
    }
}

abstract class DownloadContentModel(context: Context) {

    private val mContext: Context
    var mId: Int = 0
    lateinit var mPath: String
    lateinit var mFileName: String
    lateinit var mFileUrl: String
    lateinit var mChecksum: String

    init {
        mContext = context
    }

    private fun startDownload() {
        mId = PRDownloader.download(mFileUrl, mPath, mFileName)
            .build()
            .setOnStartOrResumeListener(OnStartOrResumeListener {})
            .setOnPauseListener(OnPauseListener { })
            .setOnCancelListener(OnCancelListener { })
            .setOnProgressListener(OnProgressListener {
                val progress: Long = it.currentBytes * 100 / it.totalBytes
                inProgressFile(mFileName, progress)
            })
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    if (FileHelper(mContext).checkMD5File(mChecksum, File(mPath + mFileName))) {
                        inFinishFile()
                    } else {
                        restartDownload(mId)
                    }
                }

                override fun onError(error: Error?) {
                    restartDownload(mId)
                }

            })
    }

    private fun restartDownload(mId: Int) {
        try {
            PRDownloader.cancel(mId)
            Thread.sleep(5000)
            startDownload()
        } catch (IEx: InterruptedException) {
            Log.d(DownloadContent.TAG, IEx.printStackTrace().toString())
        }
    }

    private fun inProgressFile(fileName: String, progress: Long) {}

    private fun inFinishFile() {}

    companion object {
        const val TAG = "DOWNLOAD_CONTENT_MODEl"
    }
}
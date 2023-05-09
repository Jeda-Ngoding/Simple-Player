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
    private var mPath: String
    private lateinit var mFileName: String
    private var mTotalFile: Int = 0
    private var mIndexFile: Int = 0
    private lateinit var mDownloadContentModel: DownloadContentModel

    init {
        mContext = context
        mData = data
        mTotalFile = mData.length()
        mIndexFile = 0
        mPath = FileHelper(mContext).getRootPath() + CONTENT_DIRECTORY

        Log.d(TAG, "Total Content : $mTotalFile - Data : $mData")

        PRDownloader.initialize(
            mContext,
            PRDownloaderConfig.newBuilder().setDatabaseEnabled(true).build()
        )

        process(mData, mIndexFile)
    }

    private fun process(data: JSONArray, index: Int) {
        val item: JSONObject = data.optJSONObject(mIndexFile)
        val content =
            Content(
                index,
                item.optString("url"),
                item.optString("name"),
                item.optString("checksum")
            )
        val file = File(mPath + content.name)

        if (!file.exists()) {
            mDownloadContentModel = object : DownloadContentModel(mContext, content) {
                override fun inProgressFile(fileName: String, progress: Long) {
                    onDownloadProgress(fileName, mIndexFile, progress, mTotalFile)
                }

                override fun inFinishFile() {
                    if (mIndexFile == mTotalFile - 1) {
                        mIndexFile++
                        onDownloadNext(mFileName, mIndexFile, 100, mTotalFile)
                        onDownloadComplete(mTotalFile)
                    } else {
                        mIndexFile++
                        onDownloadNext(mFileName, mIndexFile, 0, mTotalFile)
                        process(data, mIndexFile)
                    }
                }

            }
            mDownloadContentModel.startDownload()
        } else {
            if (mIndexFile == mTotalFile - 1) {
                mIndexFile++
                onDownloadComplete(mTotalFile)
            } else {
                mIndexFile++
                onDownloadProgress(mFileName, mIndexFile, 0, mTotalFile)
                process(data, mIndexFile)
            }
        }
    }

    abstract fun onDownloadProgress(
        fileName: String,
        progressFile: Int,
        progressPercent: Long,
        totalFile: Int
    )

    abstract fun onDownloadNext(
        fileName: String,
        progressFile: Int,
        progressPercent: Long,
        totalFile: Int
    )

    abstract fun onDownloadComplete(totalFile: Int)

    companion object {
        const val TAG = "DOWNLOAD_CONTENT"
    }
}

abstract class DownloadContentModel(
    context: Context,
    content: Content,
) {

    private val mContext: Context
    private var mId: Int = 0
    private var mPath: String
    private var mFileName: String
    private var mFileUrl: String
    private var mChecksum: String

    init {
        mContext = context
        mPath = FileHelper(mContext).getRootPath() + CONTENT_DIRECTORY
        mFileName = content.name
        mFileUrl = content.url
        mChecksum = content.checksum
    }

    fun startDownload() {
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

    fun restartDownload(mId: Int) {
        try {
            PRDownloader.cancel(mId)
            Thread.sleep(5000)
            startDownload()
        } catch (IEx: InterruptedException) {
            Log.d(DownloadContent.TAG, IEx.printStackTrace().toString())
        }
    }

    abstract fun inProgressFile(fileName: String, progress: Long)

    abstract fun inFinishFile()

    companion object {
        const val TAG = "DOWNLOAD_CONTENT_MODEl"
    }
}
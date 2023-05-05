package com.example.simpleplayer.actions

import android.content.Context
import android.util.Log
import org.json.JSONObject

class DownloadContent(context: Context, data: Any) {

    private val mContext: Context
    private val mData: Any

    init {
        mContext = context
        mData = JSONObject(data.toString())
        Log.d(TAG, "Data : $mData")
    }

    companion object {
        const val TAG = "DOWNLOAD_CONTENT"
    }


}
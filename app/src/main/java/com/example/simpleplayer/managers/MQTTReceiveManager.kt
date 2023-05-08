package com.example.simpleplayer.managers

import android.content.Context
import android.util.Log
import com.example.simpleplayer.actions.DownloadContent
import com.example.simpleplayer.actions.SetContent
import com.example.simpleplayer.constants.*
import org.json.JSONArray
import org.json.JSONObject

class MQTTReceiveManager(context: Context, topic: String, data: Any) {

    private var mContext: Context
    private var mTopic: String
    private var mAction: String
    private var mData: JSONObject

    init {
        mContext = context
        mTopic = topic
        mData = JSONObject(data.toString())
        mAction = mData.optString("action")


        Log.d(TAG, "Topic : $mTopic - Action : $mAction - Data : $mData")

        setAction()

    }

    private fun setAction() {
        when (mAction) {
            MQTT_ACTION_CONTENT_DOWNLOAD -> object : DownloadContent(
                mContext,
                mData.optJSONArray("content") as JSONArray
            ) {
                override fun onDownloadProgress(
                    fileName: String,
                    rogressFile: Long,
                    progressPercent: Long,
                    totalFile: Int
                ) {

                }

                override fun onDownloadNext(
                    fileName: String,
                    rogressFile: Long,
                    progressPercent: Long,
                    totalFile: Int
                ) {

                }

                override fun onDownloadComplete(totalFile: Int) {

                }

            }

            MQTT_ACTION_CONTENT_PLAY -> {
                FragmentManager(mContext, "")
            }

            MQTT_ACTION_CONTENT_STOP -> {
                FragmentManager(mContext, "")
            }

            "" -> object : SetContent(mContext) {
                override fun test1() {
                    TODO("Not yet implemented")
                }

                override fun test2() {
                    TODO("Not yet implemented")
                }

                override fun test3() {
                    TODO("Not yet implemented")
                }

            }
        }
    }

    companion object {
        const val TAG = "MQTT_RECEIVE_MANAGER"
    }
}
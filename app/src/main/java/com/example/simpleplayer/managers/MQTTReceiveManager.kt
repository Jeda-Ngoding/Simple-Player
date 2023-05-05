package com.example.simpleplayer.managers

import android.content.Context
import android.util.Log
import com.example.simpleplayer.actions.DownloadContent
import com.example.simpleplayer.constants.*
import org.json.JSONObject

class MQTTReceiveManager(context: Context, topic: String, data: Any) {

    private var mContext: Context
    private var mTopic: String
    private var mAction: String
    private var mData: Any

    init {
        mContext = context
        mTopic = topic
        mData = JSONObject(data.toString())
        mAction = (mData as JSONObject).optString("action")

        Log.d(TAG, "Topic : $mTopic - Action : $mAction - Data : $mData")

        setAction()

    }

    private fun setAction(){
        when (mAction) {
            MQTT_ACTION_CONTENT_DOWNLOAD -> {
                DownloadContent(mContext, mData)
            }

            MQTT_ACTION_CONTENT_PLAY -> {
                FragmentManager(mContext,"")
            }

            MQTT_ACTION_CONTENT_STOP -> {
                FragmentManager(mContext,"")
            }
        }
    }

    companion object {
        const val TAG = "MQTT_RECEIVE_MANAGER"
    }
}
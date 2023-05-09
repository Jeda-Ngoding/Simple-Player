package com.example.simpleplayer.managers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.simpleplayer.actions.DownloadContent
import com.example.simpleplayer.constants.*
import org.json.JSONArray
import org.json.JSONObject

class MQTTReceiveManager(context: Context, topic: String, data: Any) : BroadcastReceiver() {

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
                    progressFile: Int,
                    progressPercent: Long,
                    totalFile: Int
                ) {

                }


                override fun onDownloadNext(
                    fileName: String,
                    progressFile: Int,
                    progressPercent: Long,
                    totalFile: Int
                ) {

                }

                override fun onDownloadComplete(totalFile: Int) {

                }

            }

            MQTT_ACTION_CONTENT_PLAYLIST -> {
                Log.d(TAG, "Topic : $mTopic - Action : $mAction == $MQTT_ACTION_CONTENT_PLAYLIST")
            }

            MQTT_ACTION_CONTENT_PLAY -> {
                Log.d(TAG, "Topic : $mTopic - Action : $mAction == $MQTT_ACTION_CONTENT_PLAY")
                FragmentManagers(mContext, FRAGMENT_PLAYER)
            }

            MQTT_ACTION_CONTENT_STOP -> {
                Log.d(TAG, "Topic : $mTopic - Action : $mAction == $MQTT_ACTION_CONTENT_STOP")
            }
        }
    }

    companion object {
        const val TAG = "MQTT_RECEIVE_MANAGER"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "On Receive ${context.toString()} - ${intent.toString()}")
    }
}
package com.example.simpleplayer.managers

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.simpleplayer.MainActivity
import com.example.simpleplayer.actions.DownloadContent
import com.example.simpleplayer.constants.*
import com.example.simpleplayer.fragments.PlayerFragment
import org.json.JSONArray
import org.json.JSONObject

class MQTTReceiveManager : BroadcastReceiver() {

    private lateinit var mActivity: MainActivity
    private lateinit var mContext: Context
    private lateinit var mTopic: String
    private lateinit var mAction: String
    private lateinit var mData: JSONObject

    fun setAction(activity: MainActivity, context: Context, topic: String, data: Any) {
        Log.d(TAG, "Topic : $mTopic - Action : $mAction - Data : $mData")
        mActivity = activity
        mContext = context
        mTopic = topic
        mData = JSONObject(data.toString())
        mAction = mData.optString("action")
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
                mActivity.switchFragment(PlayerFragment().newInstance())
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

    }
}
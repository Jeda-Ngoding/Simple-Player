package com.example.simpleplayer.managers

import android.content.Context
import android.util.Log

class FragmentManager(context: Context, action: String?) {

    private var mContext: Context
    private var mAction: String? = null

    init {
        mContext = context
        mAction = action
        Log.d(TAG, "Action : $mAction")
    }

    companion object {
        const val TAG = "FRAGMENT_MANAGER"
    }


}
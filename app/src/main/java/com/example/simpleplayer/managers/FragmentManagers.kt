package com.example.simpleplayer.managers

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.simpleplayer.R
import com.example.simpleplayer.constants.FRAGMENT_PLAYER
import com.example.simpleplayer.fragments.HomeFragment
import com.example.simpleplayer.fragments.PlayerFragment


class FragmentManagers(context: Context, label: String?) : Fragment() {
    private var mContext: Context

    init {
        mContext = context
        Log.d(TAG, "Label Fragment : $label")

        val mTransactionManager = requireFragmentManager().beginTransaction()
        if (label == FRAGMENT_PLAYER) {
            Log.d(TAG, "FRAMENT PLAYER")
            mTransactionManager.replace(R.id.container, PlayerFragment().newInstance())
        } else {
            Log.d(TAG, "FRAMENT HOME")
            mTransactionManager.replace(R.id.container, HomeFragment().newInstance())
        }

        mTransactionManager.commit()

    }


    companion object {
        const val TAG = "FRAGMENT_MANAGER"
    }


}
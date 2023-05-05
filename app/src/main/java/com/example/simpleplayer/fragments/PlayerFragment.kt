package com.example.simpleplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simpleplayer.R
import com.google.ads.interactivemedia.v3.api.AdErrorEvent
import com.google.ads.interactivemedia.v3.api.AdEvent
import com.google.android.exoplayer2.*


class PlayerFragment : Fragment(), Player.Listener,
    AdErrorEvent.AdErrorListener, AdEvent.AdEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    companion object {

    }

    override fun onAdError(p0: AdErrorEvent) {
        TODO("Not yet implemented")
    }

    override fun onAdEvent(p0: AdEvent) {
        TODO("Not yet implemented")
    }
}
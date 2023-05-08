package com.example.simpleplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simpleplayer.R
import com.google.android.exoplayer2.ui.StyledPlayerView


class PlayerFragment : Fragment(),StyledPlayerView.ControllerVisibilityListener {

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

    override fun onVisibilityChanged(visibility: Int) {
        TODO("Not yet implemented")
    }
}
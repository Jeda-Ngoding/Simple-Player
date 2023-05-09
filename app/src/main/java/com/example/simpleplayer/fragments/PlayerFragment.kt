package com.example.simpleplayer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.simpleplayer.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util


class PlayerFragment : Fragment(), Player.Listener {
    private var player: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true
    private var playerExoView: StyledPlayerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_player, container, false)
        playerExoView = view.findViewById(R.id.player_exo)
        return view
    }

    private fun initPlayer() {
        player = activity?.let { ExoPlayer.Builder(it.applicationContext).build() }
        player?.playWhenReady = true
        playerExoView?.player = player
        val mediaItem =
            MediaItem.fromUri("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8")
        val mediaSource =
            HlsMediaSource.Factory(DefaultHttpDataSource.Factory()).createMediaSource(mediaItem)
        player?.setMediaSource(mediaSource)
        player?.seekTo(playbackPosition)
        player?.playWhenReady = playWhenReady
        player?.prepare()

    }

    private fun releasePlayer() {
        player?.let {
            playbackPosition = it.currentPosition
            playWhenReady = it.playWhenReady
            it.release()
            player = null
        }
    }


    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    fun newInstance(): PlayerFragment {
        return PlayerFragment()
    }

    companion object {
        const val TAG = "PLAYER_FRAGMENT"
    }

}
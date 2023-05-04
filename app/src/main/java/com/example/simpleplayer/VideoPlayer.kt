package com.example.simpleplayer

import android.content.Context
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

class VideoPlayer(context: Context) {

    private var mContext:Context? = null
    private var mPlayer: SimpleExoPlayer? = null
    private var playerView: PlayerView

    private val videoURL = "https://api.wowlite.interadsdev.com/locate/assets/eyJpdiI6IkZLSW9FMkdKQi9rNWNncnc3S2wydkE9PSIsInZhbHVlIjoiRTA0Qi9Wdk5HTHFQcVdXNFIyTnZWeXQrTTBOQlFZTUNvellQaXlsWUJwL2M0Q3hEWUxPcVlITmJyaFNHWkRtTUtZdVZIUFU4Z0ZPdG11ejZoOVdXWHpsSHQzQVhmK2NQaHd5SjRnbXF6VzA9IiwibWFjIjoiNjljZGExNDE4M2IyMTA3ZDE2MTkxMDZjMmNjOGJiYjZjYWE1M2I2ZjNkMGE3ZTM2NDIyYzIxNmE4ZDQ1ZTIxOSIsInRhZyI6IiJ9"
//    private val videoURL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

    companion object{
        const val TAG = "VIDEO_PLAYER"
    }

    init {
        mContext = context

    }

    //creating mediaSource
    private fun buildMediaSource(): MediaSource {
        // Create a data source factory.
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

        // Create a progressive media source pointing to a stream uri.
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoURL))
    }

    fun initPlayer() {

        try {
            // Create a player instance.
            mPlayer = mContext?.let { SimpleExoPlayer.Builder(it).build() }

            // Bind the player to the view.
            playerView.player = mPlayer

            //setting exoplayer when it is ready.
            mPlayer!!.playWhenReady = true

            mPlayer!!.playbackLooper

            // Set the media source to be played.
            mPlayer!!.setMediaSource(buildMediaSource())

            // Prepare the player.
            mPlayer!!.prepare()

            mPlayer!!.play()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    fun releasePlayer() {
        if (playerView == null) {
            return
        }
        //release player when done
        playerView.release()
        playerView = null
    }
}
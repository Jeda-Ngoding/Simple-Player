package com.example.simpleplayer.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.simpleplayer.R
import com.example.simpleplayer.models.Content
import com.example.simpleplayer.models.ItemPlaylist
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class PlayerFragment : Fragment(), Player.Listener {
    private var player: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true
    private var playerExoView: StyledPlayerView? = null
    private lateinit var playlistContent: ArrayList<ItemPlaylist>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistContent = createPlaylist()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_player, container, false)
        playerExoView = view.findViewById(R.id.player_exo)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPlayer()
    }

    private fun startPlayer() {
        var playItemPosition = 0
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {

                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                if (playItemPosition >= playlistContent.size) {
                    playItemPosition = 0
                }

                var url = ""

                playlistContent.map {
                    if (dateFormat.format(calendar.time) == it.timestamp) {
                        Log.d(
                            TAG,
                            "Item To Play : $it"
                        )
                        url = it.content.url
                    }
                }


                if (url !== "") {

                    if (player != null) {
                        releasePlayer()
                    }

                    initPlayer(url)
                    playItemPosition++
                }


                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    @Suppress("DEPRECATION")
    private fun initPlayer(videoURL: String) {
        player = activity?.let { ExoPlayer.Builder(it.applicationContext).build() }
        player?.playWhenReady = true
        playerExoView?.player = player
        playerExoView?.useController = false
        player?.seekTo(playbackPosition)
        player?.playWhenReady = playWhenReady
        player?.prepare(buildMediaSource(videoURL), true, false)
    }

    private fun releasePlayer() {
        player?.let {
            playbackPosition = it.currentPosition
            playWhenReady = it.playWhenReady
            it.release()
            player = null
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
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

    private fun buildMediaSource(videoURL: String): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoURL))
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPlaylist(): ArrayList<ItemPlaylist> {

        val dataContent: Array<String> = arrayOf(
            "https://static.videezy.com/system/resources/previews/000/053/846/original/Comp_9_4.mp4",
            "https://static.videezy.com/system/resources/previews/000/055/878/original/prts_new_01__5_.mp4",
            "https://static.videezy.com/system/resources/previews/000/044/903/original/telepoorte_fnl.mp4",
            "https://static.videezy.com/system/resources/previews/000/044/249/original/01__2822_29.mp4",
            "https://static.videezy.com/system/resources/previews/000/053/842/original/Comp_7_2.mp4",
            "https://static.videezy.com/system/resources/previews/000/046/254/original/Comp_1_1_2.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/712/original/visualdesign3.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/578/original/glowartwork32.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/682/original/tunnelmotions34872artworkdesign0001-0600.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/335/original/glowvisual27.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/472/original/artvisual40.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/570/original/glowdesign2.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/643/original/tunnelmotions34854reflectionspace0001-0600.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/519/original/tunnelmotions34903movinglights0001-0600.mp4",
            "https://static.videezy.com/system/resources/previews/000/055/883/original/tunnelmotions34810metaltunnel0001-0600.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/362/original/artvisual42.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/628/original/glowartwork17.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/503/original/artworkloop29.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/641/original/artdesign7.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/691/original/tunnelmotions34873artworkdesign0001-0600.mp4",
            "https://static.videezy.com/system/resources/previews/000/056/732/original/glowartwork21.mp4"
        )
        val playlistContent: ArrayList<ItemPlaylist> = ArrayList()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val listSize = 2500
        val intervalSeconds = 10 // Interval in seconds
        (0 until listSize).map {
            playlistContent.add(
                ItemPlaylist(
                    dateFormat.format(calendar.time),
                    Content(
                        it,
                        dataContent.random(),
                        "",
                        ""
                    )
                )
            )

            calendar.add(Calendar.SECOND, intervalSeconds)
        }

        return playlistContent
    }

    companion object {
        const val TAG = "PLAYER_FRAGMENT"
    }

}
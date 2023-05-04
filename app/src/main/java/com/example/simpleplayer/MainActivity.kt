package com.example.simpleplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.google.android.material.snackbar.Snackbar
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.Timer
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    private var mPlayer: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView

    private val videoURL =
        "https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4"

    private val mqttClient by lazy {
        MQTTClientHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get PlayerView by its id
        playerView = findViewById(R.id.playerView)

        setMqttCallback()

        Timer("CheckMqttConnection", false).schedule(3000) {
            if (!mqttClient.isConnected()) {
                val snackbarMsg = "Failed connect to host:\n'$MQTT_HOST'."
                Snackbar.make(findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            } else {
                try {
                    mqttClient.subscribe(MQTT_TOPIC_SUBSCRIBE.trim())
                    Log.d("MAIN", "Subscribed to topic '$MQTT_TOPIC_SUBSCRIBE'")
                } catch (ex: MqttException) {
                    Log.d(
                        "MAIN",
                        "Error subscribing to topic: $MQTT_TOPIC_SUBSCRIBE + : ${ex.printStackTrace()}"
                    )
                }
            }
        }

        initPlayer()

    }

    override fun onDestroy() {
        mqttClient.destroy()
        super.onDestroy()
    }

    private fun initPlayer() {

        try {
            // Create a player instance.
            mPlayer = SimpleExoPlayer.Builder(this).build()

            // Bind the player to the view.
            playerView.player = mPlayer

            //setting exoplayer when it is ready.
            mPlayer!!.playWhenReady = true

            // Set the media source to be played.
            mPlayer!!.setMediaSource(buildMediaSource())

            // Prepare the player.
            mPlayer!!.prepare()

            mPlayer!!.play()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || mPlayer == null) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun setMqttCallback() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {
                val snackbarMsg = "Connection to host lost:\n'$MQTT_HOST'"
                Log.w("Debug", snackbarMsg)
                Snackbar.make(findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

            @SuppressLint("SetTextI18n", "CutPasteId")
            @Throws(Exception::class)
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.w("Debug", "Message received from host '$MQTT_HOST': $message")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.w("Debug", "Message published to host '$MQTT_HOST'")
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                val snackbarMsg = "Connected to host:\n'$MQTT_HOST'."
                Snackbar.make(findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

        })
    }

    private fun releasePlayer() {
        if (mPlayer == null) {
            return
        }
        //release player when done
        mPlayer!!.release()
        mPlayer = null
    }

    //creating mediaSource
    private fun buildMediaSource(): MediaSource {
        // Create a data source factory.
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

        // Create a progressive media source pointing to a stream uri.

        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoURL))
    }
}
package com.example.simpleplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.util.Util
import com.google.android.material.snackbar.Snackbar
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.Timer
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    private val mqttClient by lazy {
        MQTTClientHelper(this)
    }

    private val videoPlayer by lazy {
        VideoPlayer(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    }

    override fun onDestroy() {
        mqttClient.destroy()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            videoPlayer.initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            videoPlayer.initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            videoPlayer.releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            videoPlayer.releasePlayer()
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
}
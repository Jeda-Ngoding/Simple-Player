package com.example.simpleplayer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.simpleplayer.constants.*
import com.example.simpleplayer.helpers.MQTTClientHelper
import com.example.simpleplayer.managers.MQTTReceiveManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setMqttCallback()

    }

    override fun onDestroy() {
        mqttClient.destroy()
        super.onDestroy()
    }

    private fun setMqttCallback() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {
                Log.w(TAG, "Connection to host lost:\n'$MQTT_HOST'")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.d(TAG, "MQTT Message Arrived : Topic : $topic - Message : $message")
                MQTTReceiveManager(this@MainActivity, topic, message)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.w(TAG, "Message published to host $MQTT_HOST")
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.w(TAG, "Connected to host: $MQTT_HOST'")
            }

        })

        Timer(MQTT_CHECK_CONNECTION, false).schedule(3000) {
            if (!mqttClient.isConnected()) {
                Log.w(TAG, "Failed connect to host '$MQTT_HOST'")
            } else {
                try {
                    mqttClient.subscribe(MQTT_TOPIC_SUBSCRIBE.trim())
                    Log.d(TAG, "Subscribed to topic '$MQTT_TOPIC_SUBSCRIBE'")
                } catch (ex: MqttException) {
                    Log.d(
                        TAG,
                        "Error subscribing to topic: $MQTT_TOPIC_SUBSCRIBE + : ${ex.printStackTrace()}"
                    )
                }
            }
        }
    }

    companion object {
        const val TAG = "MAIN_ACTIVITY"
        const val MQTT_CHECK_CONNECTION = "MQTT_CHECK_CONNECTION"
    }
}
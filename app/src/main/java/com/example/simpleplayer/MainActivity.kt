package com.example.simpleplayer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.simpleplayer.constants.*
import com.example.simpleplayer.fragments.PlayerFragment
import com.example.simpleplayer.helpers.MQTTClientHelper
import com.example.simpleplayer.managers.MQTTReceiveManager
import com.example.simpleplayer.managers.PreferenceManager
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject
import java.util.Timer
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    private val mqttClient by lazy {
        MQTTClientHelper(this)
    }

    private val mqttReceiveManager by lazy {
        MQTTReceiveManager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        setMqttCallback()

        switchFragment(PlayerFragment().newInstance())

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
                mqttReceiveManager.setAction(this@MainActivity, applicationContext, topic, message)
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

    fun switchFragment(fragment: Fragment) {
        Log.w(TAG, "Switch Fragment")
        val fm: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fm.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    companion object {
        const val TAG = "MAIN_ACTIVITY"
        const val MQTT_CHECK_CONNECTION = "MQTT_CHECK_CONNECTION"
    }
}
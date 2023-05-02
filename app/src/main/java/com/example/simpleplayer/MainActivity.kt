package com.example.simpleplayer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.lang.Exception
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

        findViewById<Button>(R.id.btnPub).setOnClickListener {
            var topic = findViewById<EditText>(R.id.editTextPubTopic).text.toString().trim()
            val payload = findViewById<EditText>(R.id.editTextMsgPayload).text.toString()
            if (topic.isNotEmpty()) {
                println(
                    try {
                        mqttClient.publish(topic, payload, 0)
                        "Published to topic '$topic'"
                    } catch (ex: MqttException) {
                        ex.printStackTrace().toString()
                    }
                )
            }
        }

        findViewById<Button>(R.id.btnSub).setOnClickListener(View.OnClickListener {
            val topic = findViewById<EditText>(R.id.editTextSubTopic).text.toString().trim()
            if (topic.isNotEmpty()) {
                println(
                    try {
                        mqttClient.subscribe(topic)
                        "Subscribed to topic '$topic'"
                    } catch (ex: MqttException) {
                        "Error subscribing to topic: $topic"
                    }
                )
            }
        })

        Timer("CheckMqttConnection", false).schedule(3000) {
            if (!mqttClient.isConnected()) {
                println("MQTT Connection failed !!!")
            }
        }

    }

    override fun onDestroy() {
        mqttClient.destroy()
        super.onDestroy()
    }

    private fun setMqttCallback() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {
                val snackbarMsg = "Connection to host lost:\n'$MQTT_HOST'"
                Log.w("Debug", snackbarMsg)
                Snackbar.make(findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.w("Debug", "Message received from host '$MQTT_HOST': $message")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.w("Debug", "Message published to host '$MQTT_HOST'")
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                val snackbarMsg = "Connected to host:\n'$MQTT_HOST'."
                Log.w("Debug", snackbarMsg)
                Snackbar.make(findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

        })
    }
}
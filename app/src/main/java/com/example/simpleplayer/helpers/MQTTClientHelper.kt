package com.example.simpleplayer.helpers

import android.content.Context
import android.util.Log
import com.example.simpleplayer.constants.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

class MQTTClientHelper(context: Context) {
    private var mqttAndroidClient: MqttAndroidClient
    private val serverUri = MQTT_HOST
    private val clientID: String = MqttClient.generateClientId()

    init {
        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientID)

        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = MQTT_CONNECTION_RECONNECT
        mqttConnectOptions.isCleanSession = MQTT_CONNECTION_CLEAN_SESSION
        mqttConnectOptions.userName = MQTT_USERNAME
        mqttConnectOptions.password = MQTT_PASSWORD.toCharArray()
        mqttConnectOptions.connectionTimeout = MQTT_CONNECTION_TIMEOUT
        mqttConnectOptions.keepAliveInterval = MQTT_CONNECTION_KEEP_ALIVE_INTERVAL

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.w(TAG, "Failed to connect :$serverUri ; $exception")
                }

            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    fun setCallback(callback: MqttCallbackExtended?) {
        mqttAndroidClient.setCallback(callback)
    }

    fun subscribe(subscriptionTopic: String, qos: Int = 0) {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.w(TAG, "Subscribed to topic '$subscriptionTopic'")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.w(TAG, "Subscription to topic '$subscriptionTopic' failed!")
                }

            })
        } catch (ex: MqttException) {
            System.err.println("Exception whilst subscribing to topic '$subscriptionTopic'")
            ex.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 0) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            mqttAndroidClient.publish(topic, message.payload, qos, false)
            Log.d(TAG, "Message published to topic `$topic`: $msg")
        } catch (ex: MqttException) {
            Log.d(TAG, "Error Publishing to $topic: " + ex.message)
            ex.printStackTrace()
        }
    }

    fun isConnected(): Boolean {
        return mqttAndroidClient.isConnected
    }

    fun destroy() {
        mqttAndroidClient.unregisterResources()
        mqttAndroidClient.disconnect()
    }

    companion object {
        const val TAG = "MQTT_CLIENT_HELPER"
    }

}
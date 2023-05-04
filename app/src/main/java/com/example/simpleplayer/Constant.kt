package com.example.simpleplayer


const val MQTT_HOST = "wss://interadsdev.com:8883"
const val MQTT_USERNAME = "admin01"
const val MQTT_PASSWORD = "zJJ9M7sKCW"
const val MQTT_CONNECTION_TIMEOUT = 3
const val MQTT_CONNECTION_KEEP_ALIVE_INTERVAL = 60
const val MQTT_CONNECTION_CLEAN_SESSION = true
const val MQTT_CONNECTION_RECONNECT = true

const val MQTT_TOPIC_ROOT = "simple_player_app"
const val MQTT_TOPIC_PUBLISH = "$MQTT_TOPIC_ROOT/test"
const val MQTT_TOPIC_SUBSCRIBE = "$MQTT_TOPIC_ROOT/test"
const val MQTT_TOPIC_CONTENT = "$MQTT_TOPIC_ROOT/content"

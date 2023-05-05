package com.example.simpleplayer.constants

const val MQTT_TOPIC_ROOT = "simple_player_app"
const val MQTT_TOPIC_PUBLISH = "$MQTT_TOPIC_ROOT/publish"
const val MQTT_TOPIC_SUBSCRIBE = "$MQTT_TOPIC_ROOT/subscribe/001"
const val MQTT_ACTION_CONTENT_DOWNLOAD = "content/download"
const val MQTT_ACTION_CONTENT_PLAYLIST = "content/playlist"
const val MQTT_ACTION_CONTENT_PLAY = "content/play"
const val MQTT_ACTION_CONTENT_STOP = "content/stop"
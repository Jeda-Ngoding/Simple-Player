package com.example.simpleplayer.interfaces

import org.json.JSONObject

interface PreferenceInterface {

    fun clearPrefs()
    fun setContentPlaylist(data: JSONObject)
    fun getContentPlaylist(): JSONObject
}
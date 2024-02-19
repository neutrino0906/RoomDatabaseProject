package com.example.roomdatabaseproject

import android.content.Context
import android.content.SharedPreferences

object SharedPref {

    const val IS_USER_LOGGED_IN = "isUserLoggedIn"

    const val NAME = "AuthenticationPref"
    const val MODE = Context.MODE_PRIVATE

    lateinit var preferences : SharedPreferences

    fun init(context: Context){
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    fun put(key: String, value: Boolean){
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean{
        return preferences.getBoolean(key, false)
    }

    fun putEmail(value : String){
        preferences.edit().putString("email", value).apply()
    }

    fun getEmail(): String{
        return preferences.getString("email", "not_found@")!!
    }
}
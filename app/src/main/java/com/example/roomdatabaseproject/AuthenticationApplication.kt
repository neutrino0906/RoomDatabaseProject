package com.example.roomdatabaseproject

import android.app.Application

class AuthenticationApplication: Application() {


    override fun onCreate() {
        super.onCreate()


        SharedPref.init(this)
    }
}
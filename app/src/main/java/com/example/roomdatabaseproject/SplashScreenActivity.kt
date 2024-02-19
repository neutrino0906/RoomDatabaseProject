package com.example.roomdatabaseproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class SplashScreenActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val loggedIn = SharedPref.getBoolean(SharedPref.IS_USER_LOGGED_IN)

        if(loggedIn){
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("userEmail", SharedPref.getEmail())
            startActivity(intent)
        }
        else{
            startActivity(Intent(this, LoginScreen::class.java))
        }

        finish()
    }
}
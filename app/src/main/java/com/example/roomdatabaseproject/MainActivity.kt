package com.example.roomdatabaseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.roomdatabaseproject.data.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
//    lateinit var auth : FirebaseAuth
    lateinit var signout_button : Button
    lateinit var googleSigninClient : GoogleSignInClient
    lateinit var userViewModel: UserViewModel
    lateinit var name: TextView
    lateinit var uid: TextView
    lateinit var email: TextView
    lateinit var phone : TextView
    lateinit var occ : TextView
    lateinit var userEmail : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.button_bg)
        FirebaseApp.initializeApp(this)
        signout_button = findViewById(R.id.signout_button)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        name = findViewById(R.id.tv_name)
        uid = findViewById(R.id.tv_id)
        email = findViewById(R.id.tv_email)
        phone = findViewById(R.id.tv_phone)
        occ = findViewById(R.id.tv_occupation)

        userEmail = intent.getStringExtra("userEmail").toString()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSigninClient = GoogleSignIn.getClient(this, gso)

        signout_button.setOnClickListener(){
//            auth.signOut()
            SharedPref.put(SharedPref.IS_USER_LOGGED_IN,false)
            googleSigninClient.revokeAccess()
//            googleSigninClient.signOut().addOnCompleteListener {
//                startActivity(Intent(this, LoginScreen::class.java))
//            }

//            userViewModel.deleteUser()
            startActivity(Intent(this, LoginScreen::class.java))

            }


        CoroutineScope(Dispatchers.IO).launch{
            val currUser = userViewModel.readUser(userEmail)

            name.text = currUser.name
            email.text = currUser.email
            uid.text = currUser.uid
            phone.text = currUser.phoneNo
            occ.text = currUser.occupation
        }







    }
}
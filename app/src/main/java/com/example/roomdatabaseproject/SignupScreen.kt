package com.example.roomdatabaseproject

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.roomdatabaseproject.data.User
import com.example.roomdatabaseproject.data.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupScreen : AppCompatActivity() {

    //Declaration
    lateinit var already_have : TextView
    private lateinit var auth: FirebaseAuth
    lateinit var signup_button : Button
    lateinit var signup_email : TextInputEditText
    lateinit var signup_email_layout : TextInputLayout
    lateinit var signup_pass : TextInputEditText
    lateinit var signup_pass_layout : TextInputLayout
    lateinit var signup_phone : TextInputEditText
    lateinit var signup_name : TextInputEditText
    lateinit var signup_name_layout : TextInputLayout
    lateinit var signup_occ : TextInputEditText
    lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_screen)
        FirebaseApp.initializeApp(this)

        window.statusBarColor = ContextCompat.getColor(this, R.color.button_bg)

        //Initialization
        auth = Firebase.auth
        already_have = findViewById(R.id.already_have)
        signup_button = findViewById(R.id.signup_button)
        signup_email = findViewById(R.id.textInputEditText_email_signup)
        signup_pass = findViewById(R.id.textInputEditText_pass_signup)
        signup_name = findViewById(R.id.textInputEditText_name_signup)
        signup_phone = findViewById(R.id.textInputEditText_phone_signup)
        signup_occ = findViewById(R.id.textInputEditText_occ_signup)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        signup_email_layout = findViewById(R.id.textInputLayout_email_signup)
        signup_pass_layout = findViewById(R.id.textInputLayout_pass_signup)
        signup_name_layout = findViewById(R.id.textInputLayout_name_signup)


        //Set spannable text
        val spannableText = SpannableString("Already have an account? Login")

        spannableText.setSpan(
            object: ClickableSpan(){
                override fun onClick(view: View) {
                    val intent = Intent(view.context, LoginScreen::class.java)
                    startActivity(intent)
                    finish()
                }
            },
            spannableText.length-5,
            spannableText.length,
            0
        )

        spannableText.setSpan(

            ForegroundColorSpan(Color.BLUE),
            spannableText.length-5,
            spannableText.length,
            0
        )


        already_have.text = spannableText
        already_have.movementMethod = LinkMovementMethod.getInstance()

        signup_button.setOnClickListener {
            onSignupClick(signup_email.text.toString(), signup_pass.text.toString())
        }

        signup_email.setOnFocusChangeListener {
            _,_ -> signup_email_layout.error=""
        }

        signup_name.setOnFocusChangeListener { _, _ -> signup_name_layout.error = ""}

        signup_pass.setOnFocusChangeListener { _, _ -> signup_pass_layout.error = ""}

    }

    private fun onSignupClick(email:String, password:String){


        if(email!="" && password!=""){
//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        SharedPref.put(SharedPref.IS_USER_LOGGED_IN,true)
//                        startActivity(Intent(this, MainActivity::class.java))
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Toast.makeText(
//                            baseContext,
//                            "Authentication failed.",
//                            Toast.LENGTH_SHORT,
//                        ).show()
////                    updateUI(null)
//                    }
//                }
            addUserToDatabase()
            CoroutineScope(Dispatchers.IO).launch{
                val currUser = userViewModel.readUser(signup_email.text.toString())
//
//                if(currUser.password==signup_pass.text.toString()){
                SharedPref.put(SharedPref.IS_USER_LOGGED_IN, true)
                SharedPref.putEmail(currUser.email)

                val intent = Intent(baseContext, MainActivity::class.java)
                intent.putExtra("userEmail", currUser.email)
                startActivity(intent)
//                }
            }


        }
        else{
            if(signup_email.text.toString()==""){
                signup_email.clearFocus()
                signup_email_layout.error = "*required"
            }
            if(signup_pass.text.toString()=="") {
                signup_pass.clearFocus()
                signup_pass_layout.error = "*required"
            }

            if(signup_name.text.toString()=="") {
                signup_name.clearFocus()
                signup_name_layout.error = "*required"
            }

            Toast.makeText(
                baseContext,
                "Please enter valid details.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun addUserToDatabase(){
        if(signup_occ.text.toString()=="") signup_occ.setText("Business")

        userViewModel.addUser(User(0, "Uid not found", signup_name.text.toString(), signup_email.text.toString(), signup_phone.text.toString(), signup_occ.text.toString(), signup_pass.text.toString()))
        Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show()
    }


}
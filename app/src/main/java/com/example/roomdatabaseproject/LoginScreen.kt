package com.example.roomdatabaseproject

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.roomdatabaseproject.data.User
import com.example.roomdatabaseproject.data.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginScreen : AppCompatActivity() {

    lateinit var sign_up: TextView
    lateinit var login_button : Button
    lateinit var login_email : TextInputEditText
    lateinit var login_pass : TextInputEditText
    lateinit var google_login : ImageButton
    lateinit var googleSigninClient : GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    lateinit var userViewModel: UserViewModel
    lateinit var login_pass_layout : TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        sign_up = findViewById(R.id.dont_have)
        login_button = findViewById(R.id.login_button)
        login_email = findViewById(R.id.textInputEditText_login_email)
        login_pass = findViewById(R.id.textInputEditText_login_pass)
        google_login = findViewById(R.id.google_login_button)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        login_pass_layout = findViewById(R.id.textInputLayout_pass_login)

        window.statusBarColor = ContextCompat.getColor(this, R.color.button_bg)

        val spannableText = SpannableString("Don't have an account? Sign up")


        spannableText.setSpan(
            object : ClickableSpan() {
                override fun onClick(view: View) {
                    val intent = Intent(view.context, SignupScreen::class.java)
                    startActivity(intent)
                    finish()
                }
            },
            spannableText.length-7,
            spannableText.length,
            0
        )
        spannableText.setSpan(
            ForegroundColorSpan(Color.BLUE),
            spannableText.length-7,
            spannableText.length,
            0
        )

        sign_up.text = spannableText

        sign_up.movementMethod = LinkMovementMethod.getInstance()

        login_button.setOnClickListener(){
            onLoginClick(login_email.text.toString(), login_pass.text.toString())
        }

        login_pass.setOnFocusChangeListener(){
            _,_ -> login_pass_layout.error = ""
        }




        // Google SignIn

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        googleSigninClient = GoogleSignIn.getClient(this, gso)

        google_login.setOnClickListener(){
            onGoogleLogin()
        }


    }

    private fun onLoginClick(email : String, password: String){
        if(email!="" && password!="") {

            var errorOcc = false


            CoroutineScope(Dispatchers.IO).launch {
                    Log.e("name", "entered")
                    val currUser : User = userViewModel.readUser(login_email.text.toString())


//                    if(currUser==null) Toast.makeText(baseContext, "User Not Found", Toast.LENGTH_SHORT).show()
                    try{
                        if (currUser.password == login_pass.text.toString()) {
                            SharedPref.put(SharedPref.IS_USER_LOGGED_IN, true)
                            SharedPref.putEmail(currUser.email)

                            val intent = Intent(baseContext, MainActivity::class.java)
                            intent.putExtra("userEmail", currUser.email)
                            startActivity(intent)
                        } else {
                            errorOcc = true
                        }
                    }
                    catch (e: Exception){
                        withContext(Dispatchers.Main){ Toast.makeText(baseContext, "User not found", Toast.LENGTH_SHORT).show() }
                    }

                    withContext(Dispatchers.Main) {
                        if (errorOcc) {
                            Toast.makeText(
                                baseContext,
                                "Incorrect password",
                                Toast.LENGTH_SHORT,
                            ).show()
                            login_pass.clearFocus()
                            login_pass_layout.error = "Please enter correct password"
                        }
                    }

                }



            if(errorOcc){
                Toast.makeText(
                    this,
                    "Incorrect password",
                    Toast.LENGTH_SHORT,
                ).show()
                login_pass_layout.error = "Please enter correct password"
            }


        }
        else{
            Toast.makeText(
                baseContext,
                "Please enter valid details.",
                Toast.LENGTH_SHORT,
            ).show()
        }

    }

    private fun onGoogleLogin(){

        val signInIntent = googleSigninClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                SharedPref.put(SharedPref.IS_USER_LOGGED_IN,true)
                SharedPref.putEmail(auth.currentUser?.email.toString())

                val intent : Intent = Intent(this , MainActivity::class.java)
                intent.putExtra("userEmail", auth.currentUser?.email.toString())
                startActivity(intent)
                addDataToDatabase(auth.currentUser)


            }else{
                Toast.makeText(this, "Couldn't Register" , Toast.LENGTH_SHORT).show()

            }
        }
//        val intent : Intent = Intent(this , MainActivity::class.java)
//        startActivity(intent)
    }


    private fun addDataToDatabase(userInfo: FirebaseUser?){


        val uid :String
        val name : String
        val email : String
        val phoneNo : String

        if(userInfo?.uid!=null)
            uid = userInfo.uid
        else
            uid = "null"

        if(userInfo?.displayName!=null)
            name = userInfo.displayName.toString()
        else
            name = "null"

        if(userInfo?.email!=null)
            email = userInfo.email.toString()
        else
            email = "null"

        if(userInfo?.phoneNumber!=null)
            phoneNo = userInfo.phoneNumber!!
        else
            phoneNo = "Phone not added"

        userViewModel.addUser(User(0,uid, name, email, phoneNo))
//        userViewModel.addUser(User(0,"userInfo.uid", "userInfo.displayName!!", "userInfo.email!!", "userInfo.phoneNumber!!"))
            Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show()
    }


}
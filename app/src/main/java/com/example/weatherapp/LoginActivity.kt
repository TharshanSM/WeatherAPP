package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLoginRegister.setOnClickListener(){
            performLogin()
        }

        lblBacktoReg.setOnClickListener(){
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            //finish()
        }

    }

    private fun performLogin() {
        val email = txtLoginEmail.text.toString()
        val pw = txtLoginPw.text.toString()
        if (email.isEmpty() || pw.isEmpty()) {
            Toast.makeText(this, "Email and Password Cannot be Null Value", Toast.LENGTH_LONG).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    //else if successful
                    Log.d("Main", "Login Successful")
                    val intent=Intent(this,WeatherActivity::class.java)
                    intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("Main", "Failed to Login ${it.message}")
                }
    }



}
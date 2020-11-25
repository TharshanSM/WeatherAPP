package com.example.weatherapp

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnReg.setOnClickListener() {
            performRegister()

        }

        lblAlreadyAcc.setOnClickListener() {
            //launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnSelectImg.setOnClickListener {
            Log.d("MainActivity", "Try to Show Photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceed and check the image
            Log.d("MainActivity", "Photo Was Selected")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            btnSelectedImgView.setImageBitmap(bitmap)
            btnSelectImg.alpha = 0f
//            val bitmapDrawable=BitmapDrawable(bitmap)
//            btnSelectImg.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister() {
        val email = txtEmail.text.toString()
        val password = txtPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and Password Cannot be Null Value", Toast.LENGTH_LONG)
                .show()
            return
        }

        Log.d("MainActivity", "Email: $email")
        Log.d("MainActivity", "Password: $password")

        //firebase authentication to create user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() {
                if (!it.isSuccessful) return@addOnCompleteListener
                //else if successful
                Log.d("MainActivity", "Successfully Created the UserID: ${it.result?.user?.uid}")

                //imageupload firebase
                uploadImageFirebase()
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Failed to Create User ${it.message}")
                Toast.makeText(this, "Failed to Create User", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImageFirebase() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("MainActivity", "Successfully Upload Image Path: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("MainActivity", "Successfully Upload Image Path: $it")
                    saveDatatoDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error in Upload Image Path")
            }
    }

    private fun saveDatatoDatabase(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, txtUsername.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("MainActivity", "Finally Save User to Database")

                //redirect to weatherpage
                val intent=Intent(this,WeatherActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error in Save Database: ${it.message}")
            }
    }

}

class User(val uid:String,val username:String,val profileImageUrl:String)
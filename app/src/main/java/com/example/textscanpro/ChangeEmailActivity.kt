package com.example.textscanpro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangeEmailActivity : AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var auth : FirebaseAuth
    private lateinit var credential: AuthCredential

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)

        auth = FirebaseAuth.getInstance()

        saveButton = findViewById(R.id.save_button)
        emailEditText = findViewById(R.id.change_email)

        saveButton.setOnClickListener {
            val newEmail = emailEditText.text.toString().trim()


            val user = auth.currentUser
            Log.i("AFASFGAS", user?.email.toString())

            user?.updateEmail(newEmail)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Email updated successfully
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to update email. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}

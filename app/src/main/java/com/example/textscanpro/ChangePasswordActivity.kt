package com.example.textscanpro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        saveButton = findViewById(R.id.save_button)

        saveButton.setOnClickListener {
            val newPassword = findViewById<EditText>(R.id.change_password).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.confirm_new_password).text.toString()

            if (newPassword.isEmpty()) {
                findViewById<EditText>(R.id.change_password).error = "Enter new password"
                findViewById<EditText>(R.id.confirm_new_password).requestFocus()
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                findViewById<EditText>(R.id.change_password).error = "Confirm new password"
                findViewById<EditText>(R.id.confirm_new_password).requestFocus()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                findViewById<EditText>(R.id.change_password).error = "Passwords do not match"
                findViewById<EditText>(R.id.confirm_new_password).requestFocus()
                return@setOnClickListener
            }

            val user = FirebaseAuth.getInstance().currentUser

            user?.updatePassword(newPassword)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to update password. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}

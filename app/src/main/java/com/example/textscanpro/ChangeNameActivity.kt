package com.example.textscanpro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ChangeNameActivity : AppCompatActivity() {
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_name)

        saveButton = findViewById(R.id.save_button)

        saveButton.setOnClickListener {


            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
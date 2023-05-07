package com.example.textscanpro

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import java.io.File

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val imageUri = intent.getStringExtra("imageUri")
        var photoFile = File(getRealPathFromURI(Uri.parse(imageUri)))
        var photoFileName = photoFile.name.replace("textscanpro_", "").replace(".jpg", "")
        val photoNameEditText = findViewById<EditText>(R.id.photoNameEditText)
        photoNameEditText.setText(photoFileName)

        val imageView: ImageView = findViewById(R.id.imageView)
        Glide.with(this)
            .load(Uri.parse(imageUri))
            .into(imageView)

        val shareButton: Button = findViewById(R.id.shareButton)
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUri))
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        }

        val deleteButton : Button = findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener{
            if (photoFile.exists()) {
                photoFile.delete()
                Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Photo not found", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        photoNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val fileName = s.toString().trim() + ".jpg"
                photoFileName = "textscanpro_" + fileName
            }

            override fun afterTextChanged(s: Editable?) {
                val newFile = File(photoFile.parent, photoFileName)
                if (photoFile.renameTo(newFile)) {
                    photoFile = newFile
                    Log.d(TAG, "File renamed to ${newFile.name}")
                } else {
                    Log.e(TAG, "Error renaming file")
                }
            }
        })
    }

    private fun getRealPathFromURI(uri: Uri?): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri!!, projection, null, null, null)
        val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val filePath = cursor.getString(columnIndex)
        cursor.close()
        return filePath
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
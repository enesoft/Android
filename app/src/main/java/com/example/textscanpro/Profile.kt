package com.example.textscanpro

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth

class Profile : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var profilePhotoImageView: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        sharedPreferences = requireActivity().getSharedPreferences("isMaleSelected", Context.MODE_PRIVATE)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        val userEmail = rootView.findViewById<TextView>(R.id.emailId)
        userEmail.text = "Welcome, " + firebaseAuth.currentUser?.email

        profilePhotoImageView = rootView.findViewById(R.id.profilePhoto)
        val isMaleSelected = sharedPreferences.getBoolean("isMaleSelected", true)
        if (isMaleSelected) {
            profilePhotoImageView.setImageResource(R.drawable.male)
        } else {
            profilePhotoImageView.setImageResource(R.drawable.female)
        }
        profilePhotoImageView.setOnClickListener {
            val editor = sharedPreferences.edit()
            val isMaleSelected = sharedPreferences.getBoolean("isMaleSelected", true)
            if (isMaleSelected) {
                profilePhotoImageView.setImageResource(R.drawable.female)
                editor.putBoolean("isMaleSelected", false)
            } else {
                profilePhotoImageView.setImageResource(R.drawable.male)
                editor.putBoolean("isMaleSelected", true)
            }
            editor.apply()
        }

        val emailCard = rootView.findViewById<CardView>(R.id.changeEmail)
        val passcodeCard = rootView.findViewById<CardView>(R.id.changePasscode)
        val nameCard = rootView.findViewById<CardView>(R.id.changeName)
        val logoutCard = rootView.findViewById<CardView>(R.id.logout)

        val emailTitle = emailCard.findViewById<TextView>(R.id.title)
        val passcodeTitle = passcodeCard.findViewById<TextView>(R.id.title)
        val nameTitle = nameCard.findViewById<TextView>(R.id.title)
        val logoutTitle = logoutCard.findViewById<TextView>(R.id.title)
        val emailImage = emailCard.findViewById<ImageView>(R.id.imageRecycle)
        val passcodeImage = passcodeCard.findViewById<ImageView>(R.id.imageRecycle)
        val nameImage = nameCard.findViewById<ImageView>(R.id.imageRecycle)
        val logoutImage = logoutCard.findViewById<ImageView>(R.id.imageRecycle)


        emailTitle.text = "Change Email"
        emailImage.setImageResource(R.drawable.email)
        passcodeTitle.text = "Change Password"
        passcodeImage.setImageResource(R.drawable.password)
        nameTitle.text = "Change Name"
        nameImage.setImageResource(R.drawable.name)
        logoutTitle.text = "Logout"
        logoutImage.setImageResource(R.drawable.logout)

        emailCard.setOnClickListener{
            val intent = Intent(context, ChangeEmailActivity::class.java)
            startActivity(intent)
        }

        nameCard.setOnClickListener{
            updateUi(ChangeNameActivity())
        }

        passcodeCard.setOnClickListener{
            updateUi(ChangePasswordActivity())
        }

        logoutCard.setOnClickListener{
            firebaseAuth.signOut()
            updateUi(SignInActivity())
        }

        return rootView
    }

    private fun updateUi(activity : Activity) {
        val intent = Intent(context, activity.javaClass)
        startActivity(intent)
        activity.finish()
    }
}
package com.example.textscanpro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.textscanpro.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import java.lang.StrictMath.hypot


class SignInActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    private val TAG = "SignInActivity"
    private val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animateLoginButton()

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("email", MODE_PRIVATE)

//        check if the user is already signed in
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (firebaseAuth.currentUser != null || account != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.registerTextView.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if (it.isSuccessful){
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.googleBtn.setOnClickListener {
            signIn()
        }
    }

    private fun updateUI(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("84783421358-5pi9vc40eqp77aca8qpf958s89ism0o4.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            val email = account.email
            Toast.makeText(this, "Signed in as $email", Toast.LENGTH_SHORT).show()
            val editor = sharedPreferences.edit()
            editor.putString("email", email)
            editor.apply()
            updateUI()
        }

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val editor = sharedPreferences.edit()
            editor.putString("email", account.email)
            editor.apply()
            updateUI()
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI()
        }
    }

    private fun animateLoginButton() {
        binding.loginButton.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val x: Int = binding.loginButton.right / 2
                val y: Int = binding.loginButton.bottom / 2

                val startRadius = 0
                val endRadius = hypot(binding.loginButton.width.toDouble(), binding.loginButton.height.toDouble()).toInt()

                val anim = ViewAnimationUtils.createCircularReveal(
                    binding.loginButton,
                    x,
                    y,
                    startRadius.toFloat(),
                    endRadius.toFloat()
                )

                anim.duration = 1000
                anim.interpolator = AccelerateDecelerateInterpolator()
                anim.start()

                binding.loginButton.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

}
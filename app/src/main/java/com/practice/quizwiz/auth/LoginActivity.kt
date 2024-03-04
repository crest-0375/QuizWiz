package com.practice.quizwiz.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.practice.quizwiz.R
import com.practice.quizwiz.databinding.ActivityLoginBinding
import com.practice.quizwiz.home.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textOfGoogleBtn = binding.loginGoogleSignIn.getChildAt(0) as TextView
        textOfGoogleBtn.text = getString(R.string.continue_with_google)
        textOfGoogleBtn.setTextColor(Color.BLACK)
        textOfGoogleBtn.textSize = 18f

        registerActivityWithSignIn()

        binding.loginEmailEt.doOnTextChanged { _, _, _, _ ->
            binding.loginEmailTil.setErrorMessage("")
        }

        binding.loginPasswordEt.doOnTextChanged { _, _, _, _ ->
            binding.loginPasswordTil.setErrorMessage("")
        }

        binding.loginSignInBtn.setOnClickListener {
            signInWithFirebase()
        }
        binding.loginGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
        binding.loginDoNotHaveAcc.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
        binding.loginForgotPass.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("241999460124-qcf72rheoc14qo39ng500svartt1v2hj.apps.googleusercontent.com")
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        signIn()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }

    private fun registerActivityWithSignIn() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val resultCode = result.resultCode
                val data = result.data

                if (resultCode == RESULT_OK && data != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                    firebaseSignInWithGoogle(task)
                }

            }
    }

    private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult((ApiException::class.java))
            Toast.makeText(applicationContext, "Welcome to Quiz Game.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
            firebaseGoogleAccount(account)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
        }

    }

    private fun firebaseGoogleAccount(account: GoogleSignInAccount) {
        val authCredentials = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(authCredentials)
    }

    private fun signInWithFirebase() {
        val email = binding.loginEmailEt.text.toString().trim()
        val password = binding.loginPasswordEt.text.toString().trim()
        if (validate(email, password)) {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                Toast.makeText(this@LoginActivity, "Welcome to QuizWiz", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
                .addOnFailureListener {
                    Toast.makeText(
                        this@LoginActivity,
                        "Something went wrong.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }
    }

    private fun validate(email: String, password: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.loginEmailTil.setErrorMessage("Please enter valid Mail address.")
            return false
        } else if (password.length < 5) {
            binding.loginPasswordTil.setErrorMessage("Password size must be 5 or more.")
            return false
        }
        return true
    }

    private fun TextInputLayout.setErrorMessage(errorMsg: String) {
        error = errorMsg
        isErrorEnabled = errorMsg.isNotEmpty()
    }

}
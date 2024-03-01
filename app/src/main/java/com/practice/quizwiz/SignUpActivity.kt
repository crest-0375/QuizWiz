package com.practice.quizwiz

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.practice.quizwiz.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_sign_up)

        binding.signUpSignUpBtn.setOnClickListener {
            signUpWithFirebase()
        }
    }

    private fun validate(email: String, password: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signUpEmailTil.setErrorMessage("Please enter valid Mail address.")
            return false
        } else if (password.length < 5) {
            binding.signUpPasswordTil.setErrorMessage("Password size must be 5 or more.")
            return false
        }
        return true
    }

    private fun signUpWithFirebase() {
        val email = binding.signUpEmailEt.text.toString().trim()
        val password = binding.signUpPasswordEt.text.toString().trim()
        if (validate(email, password)) {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                Toast.makeText(this@SignUpActivity, "Welcome to QuizWiz", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
                .addOnFailureListener {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Failure: ${it.localizedMessage}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }
    }

    private fun TextInputLayout.setErrorMessage(errorMsg: String) {
        error = errorMsg
        isErrorEnabled = errorMsg.isEmpty()
    }
}
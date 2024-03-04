package com.practice.quizwiz.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.practice.quizwiz.databinding.ActivitySignUpBinding
import com.practice.quizwiz.home.MainActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signUpSignUpBtn.setOnClickListener {
            signUpWithFirebase()
        }
        binding.signUpEmailEt.doOnTextChanged { _, _, _, _ ->
            binding.signUpEmailTil.setErrorMessage("")
        }
        binding.signUpPasswordEt.doOnTextChanged { _, _, _, _ ->
            binding.signUpPasswordTil.setErrorMessage("")
        }
        binding.alreadyHaveAcc.setOnClickListener {
            finish()
        }
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
                        "Something went wrong.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }
    }


    private fun validate(email: String, password: String): Boolean {
        Log.d("TAG", "Heloo")
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signUpEmailTil.setErrorMessage("Please enter valid Mail address.")
            return false
        } else if (password.length < 5) {
            binding.signUpPasswordTil.setErrorMessage("Password size must be 5 or more.")
            return false
        }
        return true
    }

    private fun TextInputLayout.setErrorMessage(errorMsg: String) {
        error = errorMsg
        isErrorEnabled = errorMsg.isNotEmpty()
    }
}
package com.practice.quizwiz.auth

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.practice.quizwiz.databinding.ActivityForgotPasswordBinding

class ForgotPassword : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.forgotMailEt.doOnTextChanged { _, _, _, _ -> binding.forgotMailTil.setErrorMessage("") }
        binding.forgetButton.setOnClickListener {
            val email = binding.forgotMailEt.text.toString().trim()
            if (validate(email)) {
                auth.sendPasswordResetEmail(email).addOnSuccessListener {
                    finish()
                }
                    .addOnFailureListener {
                        Toast.makeText(
                            this@ForgotPassword,
                            "Something went wrong.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun validate(email: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.forgotMailTil.setErrorMessage("Please enter valid Mail address.")
            return false
        }
        return true
    }

    private fun TextInputLayout.setErrorMessage(errorMsg: String) {
        error = errorMsg
        isErrorEnabled = errorMsg.isNotEmpty()
    }
}
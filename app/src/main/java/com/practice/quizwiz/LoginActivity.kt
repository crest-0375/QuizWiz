package com.practice.quizwiz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practice.quizwiz.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginSignInBtn.setOnClickListener {

        }
        binding.loginGoogleSignIn.setOnClickListener {

        }
        binding.loginDoNotHaveAcc.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
        binding.loginForgotPass.setOnClickListener {

        }
    }
}
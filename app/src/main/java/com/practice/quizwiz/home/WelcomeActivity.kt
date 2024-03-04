package com.practice.quizwiz.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.practice.quizwiz.R
import com.practice.quizwiz.auth.LoginActivity
import com.practice.quizwiz.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alphaAnimation = AnimationUtils.loadAnimation(this@WelcomeActivity, R.anim.splash_anim)
        binding.textView.startAnimation(alphaAnimation)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = if (auth.currentUser != null) {
                Intent(this@WelcomeActivity, MainActivity::class.java)
            } else
                Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }, 5000)
    }
}
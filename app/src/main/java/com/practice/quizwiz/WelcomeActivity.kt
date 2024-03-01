package com.practice.quizwiz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.practice.quizwiz.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alphaAnimation = AnimationUtils.loadAnimation(this@WelcomeActivity, R.anim.splash_anim)
        binding.textView.startAnimation(alphaAnimation)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
            startActivity(intent)
        }, 5000)
    }
}
package com.practice.quizwiz.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.practice.quizwiz.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val auth = FirebaseAuth.getInstance().currentUser
    private val database = FirebaseDatabase.getInstance()
    private val databaseRef = database.reference.child("Score")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                auth?.let {
                    val correct_ans = snapshot.child(it.uid).child("Correct").value
                    val wrong_ans = snapshot.child(it.uid).child("Wrong").value
                    binding.correctAnsCount.text = correct_ans.toString()
                    binding.wrongAnsCount.text = wrong_ans.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        binding.startAgain.setOnClickListener {
            finish()
        }
    }
}
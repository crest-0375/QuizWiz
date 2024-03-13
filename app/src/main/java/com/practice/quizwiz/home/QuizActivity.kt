package com.practice.quizwiz.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.practice.quizwiz.R
import com.practice.quizwiz.databinding.ActivityQuizBinding
import java.io.ByteArrayOutputStream


class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private val database = FirebaseDatabase.getInstance()
    private val databaseRef = database.reference.child("Questions")
    var question = ""
    var option1 = ""
    var option2 = ""
    var option3 = ""
    var option4 = ""
    var correctAns = "0"
    var questionCount = 0
    var questionNumber = 1
    private var userAnswer = ""
    private var userCorrect = 0
    private var userWrong = 0
    private lateinit var sharedPreferences: SharedPreferences
    private var defaultScale = 1f

    private lateinit var timer: CountDownTimer
    private val totalTime = 25000L
    private var timerContinue = false
    var leftTime = totalTime

    var auth = FirebaseAuth.getInstance()
    private var user = auth.currentUser
    private val scoreReference = database.reference

    private val questionsSet = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = this.getSharedPreferences("scale", Context.MODE_PRIVATE)
//        adjustFontScale(resources.configuration)
        questionsSet.addAll(1..5)
        questionsSet.shuffle()
        gameLogic()
        defaultScale = sharedPreferences.getFloat("textsize", 1f)
        binding.plusBtn.setOnClickListener {
            defaultScale += 0.1f
            sharedPreferences.edit().putFloat("textsize", defaultScale).apply()
            adjustFontScale(resources.configuration)
            Log.d("TAG", defaultScale.toString())
        }
        binding.removeBtn.setOnClickListener {
            defaultScale -= 0.1f
            sharedPreferences.edit().putFloat("textsize", defaultScale).apply()
            adjustFontScale(resources.configuration)
            Log.d("TAG", defaultScale.toString())
        }
        binding.shareBtn.setOnClickListener {
//            val image = textAsBitmap(binding.question.text.toString(), 1f, Color.WHITE)
            val image = getLLImage(binding.linearLayout4)
            val bytes = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            val bitmapPath: String = MediaStore.Images.Media.insertImage(
                contentResolver,
                image,
                "palette",
                "share palette"
            )
            val bitmapUri = Uri.parse(bitmapPath)

            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("image/png")
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
            startActivity(Intent.createChooser(intent, "Share Image"))
        }
        binding.finish.setOnClickListener { sendScore() }
        binding.next.setOnClickListener {
            resetTimer()
            gameLogic()
        }
        binding.option1.setOnClickListener {
            pauseTimer()
            userAnswer = "1"
            if (correctAns == userAnswer) {
                binding.option1.setBackgroundColor(Color.GREEN)
                userCorrect++
                binding.correctAns.text = userCorrect.toString()
            } else {
                binding.option1.setBackgroundColor(Color.RED)
                userWrong++
                binding.wrongAns.text = userWrong.toString()
                findAnswer()
            }
            disableOptions()
        }
        binding.option2.setOnClickListener {
            pauseTimer()
            userAnswer = "2"
            if (correctAns == userAnswer) {
                binding.option2.setBackgroundColor(Color.GREEN)
                userCorrect++
                binding.correctAns.text = userCorrect.toString()
            } else {
                binding.option2.setBackgroundColor(Color.RED)
                userWrong++
                binding.wrongAns.text = userWrong.toString()
                findAnswer()
            }
            disableOptions()
        }
        binding.option3.setOnClickListener {
            pauseTimer()
            userAnswer = "3"
            if (correctAns == userAnswer) {
                binding.option3.setBackgroundColor(Color.GREEN)
                userCorrect++
                binding.correctAns.text = userCorrect.toString()
            } else {
                binding.option3.setBackgroundColor(Color.RED)
                userWrong++
                binding.wrongAns.text = userWrong.toString()
                findAnswer()
            }
            disableOptions()
        }
        binding.option4.setOnClickListener {
            pauseTimer()
            userAnswer = "4"
            if (correctAns == userAnswer) {
                binding.option4.setBackgroundColor(Color.GREEN)
                userCorrect++
                binding.correctAns.text = userCorrect.toString()
            } else {
                binding.option4.setBackgroundColor(Color.RED)
                userWrong++
                binding.wrongAns.text = userWrong.toString()
                findAnswer()
            }
            disableOptions()
        }
    }

    private fun gameLogic() {
        restoreOptions()
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                questionCount = snapshot.childrenCount.toInt()
                if (questionNumber <= questionCount) {
                    question = snapshot.child(questionsSet.elementAt(questionNumber - 1).toString())
                        .child("q").value.toString()
                    option1 = snapshot.child(questionsSet.elementAt(questionNumber - 1).toString())
                        .child("1").value.toString()
                    option2 = snapshot.child(questionsSet.elementAt(questionNumber - 1).toString())
                        .child("2").value.toString()
                    option3 = snapshot.child(questionsSet.elementAt(questionNumber - 1).toString())
                        .child("3").value.toString()
                    option4 = snapshot.child(questionsSet.elementAt(questionNumber - 1).toString())
                        .child("4").value.toString()
                    correctAns =
                        snapshot.child(questionsSet.elementAt(questionNumber - 1).toString())
                            .child("ans").value.toString()

                    binding.question.text = question
                    binding.option1.text = option1
                    binding.option2.text = option2
                    binding.option3.text = option3
                    binding.option4.text = option4

                    binding.progressBar.visibility = View.INVISIBLE
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.linearLayout4.visibility = View.VISIBLE
                    binding.linearLayout2.visibility = View.VISIBLE
                    startTimer()
                } else {
                    val dialog = AlertDialog.Builder(this@QuizActivity)
                    dialog.setTitle("Quiz Game")
                    dialog.setMessage("Congratulations!!\nYou answered all the questions. Do you want to see result?")
                    dialog.setPositiveButton("See Result") { dialogWindow, _ ->
                        dialogWindow.dismiss()
                        sendScore()
                    }
                    dialog.setNegativeButton("Play Again") { _, _ ->
                        finish()
                    }
                    dialog.create().show()
                }
                questionNumber++
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@QuizActivity, "Something went wrong.", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun findAnswer() {
        when (correctAns) {
            "1" -> binding.option1.setBackgroundColor(Color.GREEN)
            "2" -> binding.option1.setBackgroundColor(Color.GREEN)
            "3" -> binding.option1.setBackgroundColor(Color.GREEN)
            "4" -> binding.option1.setBackgroundColor(Color.GREEN)
        }
    }

    private fun disableOptions() {
        binding.option1.isClickable = false
        binding.option2.isClickable = false
        binding.option3.isClickable = false
        binding.option4.isClickable = false
    }

    private fun restoreOptions() {
        binding.option1.setBackgroundColor(resources.getColor(R.color.light_brown, this.theme))
        binding.option2.setBackgroundColor(resources.getColor(R.color.light_brown, this.theme))
        binding.option3.setBackgroundColor(resources.getColor(R.color.light_brown, this.theme))
        binding.option4.setBackgroundColor(resources.getColor(R.color.light_brown, this.theme))

        binding.option1.isClickable = true
        binding.option2.isClickable = true
        binding.option3.isClickable = true
        binding.option4.isClickable = true
    }

    private fun startTimer() {
        timer = object : CountDownTimer(leftTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                leftTime = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                disableOptions()
                resetTimer()
                updateCountDownText()
                binding.question.text = getString(R.string.sorry_time_is_up)
            }
        }.start()
        timerContinue = true
    }

    private fun pauseTimer() {
        timer.cancel()
        timerContinue = false
    }

    private fun resetTimer() {
        pauseTimer()
        leftTime = totalTime
        updateCountDownText()
    }

    private fun updateCountDownText() {
        val remainingTime: Int = (leftTime / 1000).toInt()
        binding.time.text = remainingTime.toString()
    }

    private fun sendScore() {
        user?.let {
            val userId = it.uid
            scoreReference.child("Score").child(userId).child("Correct").setValue(userCorrect)
            scoreReference.child("Score").child(userId).child("Wrong").setValue(userWrong)
                .addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Scores sent to database successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@QuizActivity, ResultActivity::class.java))
                    finish()
                }
        }
    }

    private fun adjustFontScale(configuration: Configuration) {
        val scale = sharedPreferences.getFloat("textsize", 1f)
        configuration.fontScale = scale
        val metrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
        recreate()
    }

    private fun textAsBitmap(text: String, textSize: Float, textColor: Int): Bitmap {
        val resources = this.resources
        val scale = resources.displayMetrics.density
        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.app_icon)
        var bitmapConfig = bitmap.getConfig()
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888
        }
        bitmap = bitmap.copy(bitmapConfig, true)

        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.BLACK)
        paint.textSize = (50 * scale).toInt().toFloat()
        paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY)
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val x = (bitmap.getWidth() - bounds.width()) / 6
        val y = (bitmap.getHeight() + bounds.height()) / 5
        canvas.drawColor(Color.WHITE)
        canvas.drawText(text, x * scale, y * scale, paint)

        return bitmap
    }

    private fun getLLImage(view: View): Bitmap {
        val returnedBitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(returnedBitmap)

        val bgDrawable: Drawable? = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }
}
package com.example.k_content_app

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.result)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val score = intent.getIntExtra("score", 0)  // 총점수 (캐시)
        val totalQuestions = intent.getIntExtra("totalQuestions", 0)  // 총 문제 수
        val correctAnswers = score / 20  // 맞춘 문제 수 (한 문제당 20캐시를 얻으므로)

        val resultText = findViewById<TextView>(R.id.resultText)
        resultText.text = "   $correctAnswers/$totalQuestions\n${score}c 획득"

    }
}

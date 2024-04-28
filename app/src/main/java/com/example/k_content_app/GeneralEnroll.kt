package com.example.k_content_app

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GeneralEnroll : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general_enroll)


        // Firebase 초기화
        FirebaseApp.initializeApp(this)

        // Firebase 인증 인스턴스 생성
        auth = Firebase.auth

        val enrollbtn = findViewById<Button>(R.id.button4)
        enrollbtn.setOnClickListener {
            val emailId = findViewById(R.id.email) as EditText
            val passwordId = findViewById(R.id.pwd) as EditText
            val checkPasswordId = findViewById(R.id.checkpwd) as EditText

            val email: String = emailId.text.toString()
            val password: String = passwordId.text.toString()
            val checkPassword: String = checkPasswordId.text.toString()

            if (password != checkPassword) {
                // 비밀번호와 비밀번호 확인이 일치하지 않을 경우
                Toast.makeText(
                    baseContext,
                    "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
                    Toast.LENGTH_SHORT
                ).show()

                // 비밀번호 입력란으로 포커스 이동
                passwordId.requestFocus()
            } else {
                // 비밀번호와 비밀번호 확인이 일치할 경우
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // 회원가입 성공
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            updateUI(user)
                        } else {
                            // 회원가입 실패
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            updateUI(null)
                        }
                    }
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun reload() {
        // Implement your reload logic here if needed
    }
}


package com.example.k_content_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 파이어베이스 인증 객체 초기화
        auth = FirebaseAuth.getInstance()

        val enrollBtn = findViewById<Button>(R.id.enrollBtn)
        enrollBtn?.setOnClickListener {
            val intent = Intent(this,SelectEnroll::class.java)
            startActivity(intent)
        }
        // 로그인 버튼 클릭 시
        val googleBtn = findViewById<ImageButton>(R.id.loginGoogle)
        googleBtn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        // Google 로그인 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        // GoogleSignInClient 초기화
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 로그인 Intent 시작
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Google 로그인 요청 후 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google 로그인 성공, Firebase에 인증 정보 전달
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google 로그인 실패
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    // Firebase에 Google ID 토큰을 사용하여 인증
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Firebase 인증 성공, UI 업데이트
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // Firebase 인증 실패
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        // 사용자 인증 상태에 따라 UI 업데이트
        if (user != null) {
            // 사용자가 로그인된 상태
            // 로그인 후 처리할 작업을 수행
            Log.d(TAG, "User is logged in with uid: ${user.uid}")
        } else {
            // 사용자가 로그인되어 있지 않은 상태
            // 로그인 UI 표시
            Log.d(TAG, "User is not logged in")
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 9001 // Google 로그인 요청 코드
    }
}

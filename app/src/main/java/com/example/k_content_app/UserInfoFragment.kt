package com.example.k_content_app

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserInfoFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_info,container,false)
        val user = auth.currentUser
        val enrollInfoLayout = view.findViewById<LinearLayout>(R.id.enrollInfo)

        // "enrollInfo"를 클릭했을 때 AlertDialog를 표시합니다.
        enrollInfoLayout.setOnClickListener {
            showUserInfoDialog()
        }

        view.findViewById<Button>(R.id.btn1).setOnClickListener {
            it.findNavController().navigate(R.id.action_userInfoFragment_to_searchingFragment)
        }
        view.findViewById<TextView>(R.id.userid).setText("@"+auth.currentUser!!.uid)
        view.findViewById<TextView>(R.id.username).setText(auth.currentUser!!.displayName)
        return view
    }
    private fun showUserInfoDialog() {
        // AlertDialog를 만들고 custom_dialog.xml을 사용하여 사용자 정보를 표시합니다.
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.customdialog, null)
        builder.setView(dialogView)

        // AlertDialog에 있는 TextView를 찾습니다.
        val userIdTextView = dialogView.findViewById<TextView>(R.id.userinfoArea)
        val userEmailTextView = dialogView.findViewById<TextView>(R.id.useremailArea)
        val userDisplayTextView = dialogView.findViewById<TextView>(R.id.userDisplayArea)

        // 여기에서 사용자 정보를 가져와서 TextView에 설정합니다.
        val user = Firebase.auth.currentUser
        userIdTextView.text = "사용자 UID: ${user?.uid}"
        userEmailTextView.text = "사용자 이메일: ${user?.email}"
        userDisplayTextView.text = "사용자 디스플레이: ${user?.displayName}"

        // AlertDialog를 표시합니다.
        val dialog = builder.create()
        dialog.show()
    }
}
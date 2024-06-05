package com.example.k_content_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController


class SearchingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 레이아웃을 인플레이트 합니다.
        val view = inflater.inflate(R.layout.fragment_searching, container, false)

        // 버튼 초기화 및 클릭 리스너 설정
        val imageSearchButton = view.findViewById<Button>(R.id.imageSearchBtn)
        imageSearchButton.setOnClickListener {
            Log.d("ImageSearch", "Click ImageSearch Button")
            UploadChooser().show(parentFragmentManager, "UploadChooser")
        }

        view.findViewById<Button>(R.id.btn2).setOnClickListener {
            it.findNavController().navigate(R.id.action_searchingFragment_to_userInfoFragment)
        }

        val searchButton = view.findViewById<Button>(R.id.searchbtn)
        searchButton.setOnClickListener {
            val searchText = view.findViewById<EditText>(R.id.searchcontent).text.toString()
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("searchText", searchText)
            startActivity(intent)
        }

        return view
    }
}
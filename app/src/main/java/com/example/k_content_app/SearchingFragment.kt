package com.example.k_content_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.navigation.findNavController


class SearchingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_searching,container,false)
        //btn2가 imagebutton임을 확인하고 참조를 수정
        view.findViewById<ImageButton>(R.id.btn2).setOnClickListener {
            it.findNavController().navigate(R.id.action_searchingFragment_to_userInfoFragment)
        }
        val searchButton = view.findViewById<ImageButton>(R.id.searchbtn)

        searchButton.setOnClickListener {
            val searchText = view.findViewById<EditText>(R.id.searchcontent).text.toString()
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("searchText", searchText)
            startActivity(intent)
        }
        return view;
    }

}
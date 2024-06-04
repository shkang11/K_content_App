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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_searching,container,false)
        view.findViewById<Button>(R.id.btn2).setOnClickListener {
            it.findNavController().navigate(R.id.action_searchingFragment_to_userInfoFragment)
        }
        val searchButton = view.findViewById<Button>(R.id.searchbtn)
        val imageSearchButton = view.findViewById<Button>(R.id.imageSearchBtn)

        searchButton.setOnClickListener {
            val searchText = view.findViewById<EditText>(R.id.searchcontent).text.toString()
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("searchText", searchText)
            startActivity(intent)
        }

        imageSearchButton.setOnClickListener{
            // Log.d("ImageSearch", "Click ImageSeacrch Button ")
            UploadChooser().show(AppCompatActivity().supportFragmentManager, "")

        }
        return view;
    }

}
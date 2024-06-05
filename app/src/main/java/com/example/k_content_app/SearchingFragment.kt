package com.example.k_content_app

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

class SearchingFragment : Fragment(), ImageModel.ImageSearchCallback{

    private lateinit var imageModel: ImageModel
    // lateinit var resView : TextView
    //lateinit var imageView : ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 레이아웃을 인플레이트 합니다.
        val view = inflater.inflate(R.layout.fragment_searching, container, false)

    //    resView = view.findViewById(R.id.searchcontent)
    //    imageView = view.findViewById(R.id.imageSearchView)

        // ImageModel 초기화
        imageModel = ImageModel(requireContext())
        imageModel.callback = this  // Set the callback

        // UserInfoFragment로 이동하는 버튼
        view.findViewById<Button>(R.id.btn2).setOnClickListener {
            it.findNavController().navigate(R.id.action_searchingFragment_to_userInfoFragment)
        }

        // 검색 버튼
        val searchButton = view.findViewById<Button>(R.id.searchbtn)
        searchButton.setOnClickListener {
            val searchText = view.findViewById<EditText>(R.id.searchcontent).text.toString()
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("searchText", searchText)
            startActivity(intent)
        }

        // 이미지 검색 버튼
        val imageSearchButton = view.findViewById<Button>(R.id.imageSearchBtn)
        imageSearchButton.setOnClickListener {
            // 로그 추가
            Log.d("ImageSearch", "Click ImageSearch Button")
            imageModel.callImageSearch(requireActivity() as AppCompatActivity)
        }

        return view
    }

    override fun onImageSearchResult(result: String) {
        var resView : TextView? = view?.findViewById(R.id.searchcontent)
        resView?.setText(result)
        val searchText = result
        val intent = Intent(activity, SearchActivity::class.java)
        intent.putExtra("searchText", searchText)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Log.d("request", "GetRequestCode ")
                val uri = data?.data
                uri?.let {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                    var imageView : ImageView? = view?.findViewById(R.id.imageSearchView)
                    imageView?.setImageBitmap(bitmap)
                    imageModel.modelActivity(bitmap)
                } ?: run {
                    Log.e("request", "Uri is null")
                }
            } else {
                Log.e("request", "Result not OK")
            }
        } else {
            Log.e("request", "Request code does not match")
        }
    }

}

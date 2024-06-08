package com.example.k_content_app

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

class SearchingFragment : Fragment(), ImageModel.ImageSearchCallback {

    private lateinit var imageModel: ImageModel

    private var resView: TextView? = null
    private var imageView: ImageView? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                    imageView?.setImageBitmap(bitmap)
                    imageModel.modelActivity(bitmap)
                    Log.d("ImageSearch", "Image loaded and processed successfully")
                } catch (e: Exception) {
                    Log.e("ImageSearch", "Error loading image", e)
                }
            } ?: run {
                Log.e("ImageSearch", "Uri is null")
            }
        } else {
            Log.e("ImageSearch", "Result not OK")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 레이아웃을 인플레이트 합니다.
        val view = inflater.inflate(R.layout.fragment_searching, container, false)

        resView = view.findViewById(R.id.searchcontent)
        imageView = view.findViewById(R.id.imageSearchView)

        // ImageModel 초기화
        imageModel = ImageModel(requireContext())
        imageModel.callback = this  // Set the callback

        // UserInfoFragment로 이동하는 버튼
        view.findViewById<Button>(R.id.btn2).setOnClickListener {
            Log.d("Navigation", "Navigating to UserInfoFragment")
            it.findNavController().navigate(R.id.action_searchingFragment_to_userInfoFragment)
        }

        // 검색 버튼
        val searchButton = view.findViewById<Button>(R.id.searchbtn)
        searchButton.setOnClickListener {
            val searchText = view.findViewById<EditText>(R.id.searchcontent).text.toString()
            Log.d("Search", "Search button clicked with text: $searchText")
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("searchText", searchText)
            startActivity(intent)
        }

        // 이미지 검색 버튼
        val imageSearchButton = view.findViewById<Button>(R.id.imageSearchBtn)
        imageSearchButton.setOnClickListener {
            // 로그 추가
            Log.d("ImageSearch", "Click ImageSearch Button")
            callImageSearch(this)
        }

        // 퀴즈설명으로 넘어가는 이미지 버튼
        val quizButton = view.findViewById<ImageButton>(R.id.quizButton)
        quizButton.setOnClickListener {
            val intent = Intent(activity, GameDescriptionActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onImageSearchResult(result: String) {
        Log.d("ImageSearch", "onImageSearchResult called with result: $result")
        resView?.text = result
//        val searchText = result
//        val intent = Intent(activity, SearchActivity::class.java)
//        intent.putExtra("searchText", searchText)
//        startActivity(intent)
    }

    fun callImageSearch(fragment: Fragment) {
        Log.d("ImageSearch", "callImageSearch called")
        imageSelect(fragment)
    }

    fun imageSelect(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        Log.d("ImageSearch", "Launching image selector")
        imagePickerLauncher.launch(intent)
    }
}

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
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

class SearchingFragment : Fragment(), ImageModel.ImageSearchCallback {

    private lateinit var imageModel: ImageModel

    private var resView: TextView? = null
    private var imageView: ImageView? = null
    private var uploadChooser: UploadChooser? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                    imageView?.setImageBitmap(bitmap)
                    imageModel.modelActivity(bitmap)
                    uploadChooser?.dismiss()
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

        // 이미지 검색 버튼
        val imageSearchButton = view.findViewById<Button>(R.id.imageSearchBtn)
        imageSearchButton.setOnClickListener {
            // 로그 추가
            Log.d("ImageSearch", "Click ImageSearch Button")
           // UploadChooser().show(parentFragmentManager, "UploadChooser")
           uploadChooser =  UploadChooser().apply {
                addInterface(object :UploadChooser.UploadChooserInterface{
                    override fun cameraOnClick() {
                        Log.d("upload","cameraOnClick")
                    }

                    override fun galleryOnClick() {
                        Log.d("upload","galleryOnclick")
                        callImageSearch()
                    }
                    })
                }
                uploadChooser!!.show(parentFragmentManager, "UploadChooser")
            }


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

        return view
    }

    override fun onImageSearchResult(result: String) {
        Log.d("ImageSearch", "onImageSearchResult called with result: $result")
        resView?.text = result

        // 이미지 선택 후 검색 결과를 확인 하지 않고, 바로 다음 Step으로 넘기고 싶을 때
//        val searchText = result
//        val intent = Intent(activity, SearchActivity::class.java)
//        intent.putExtra("searchText", searchText)
//        startActivity(intent)
    }

    fun callImageSearch() {
        Log.d("ImageSearch", "callImageSearch called")
        imageSelect()
    }

    fun imageSelect() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        Log.d("ImageSearch", "Launching image selector")
        imagePickerLauncher.launch(intent)
    }
}

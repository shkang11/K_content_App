package com.example.k_content_app

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.k_content_app.ml.KContentImageModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp

class ImageModel(private val context: Context) {

    interface ImageSearchCallback {
        fun onImageSearchResult(result: String)
    }

    var callback: ImageSearchCallback? = null


    lateinit var bitmap: Bitmap
    lateinit var imageProcessor: ImageProcessor
    lateinit var labels: List<String>
    var maxIdx: Int = 0

    init {
        // 레이블 파일을 읽어서 리스트로 저장하는 함수
        labels = loadLabels(context.assets, "label.txt")

        // image processor
        imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(32, 32, ResizeOp.ResizeMethod.BILINEAR))
            .build()
    }

    // 레이블 파일을 읽는 함수
    private fun loadLabels(assetManager: AssetManager, fileName: String): List<String> {
        return assetManager.open(fileName).bufferedReader().useLines { it.toList() }
    }

    // 이미지 검색 메서드
    fun callImageSearch(activity: AppCompatActivity) {
        Log.d("call", "successCallImage ")
        imageSelect(activity)
    }

    fun imageSelect(activity: AppCompatActivity) {
        val intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        Log.d("selectImage", "selectImage ")
        activity.startActivityForResult(intent, 100)
    }

    fun modelActivity(bitmap: Bitmap) {
        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)

        tensorImage = imageProcessor.process(tensorImage)

        val model = KContentImageModel.newInstance(context)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        maxIdx = 0
        outputFeature0.forEachIndexed { index, fl ->
            if (outputFeature0[maxIdx] < fl) {
                maxIdx = index
            }
        }

        val resultLabel = labels[maxIdx]
        Log.d("Result", "imageSearchResult : $resultLabel")
        callback?.onImageSearchResult(resultLabel)  // Call the callback with the result
        model.close()
    }


}

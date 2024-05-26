package com.example.k_content_app

import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.k_content_app.ml.KContentImageModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp

class ImageModel : AppCompatActivity(){

    lateinit var bitmap: Bitmap
    lateinit var imageProcessor: ImageProcessor
    lateinit var labels: List<String>
    var maxIdx: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 레이블 파일을 읽어서 리스트로 저장하는 함수
        fun loadLabels(assetManager: AssetManager, fileName: String): List<String> {
            return assetManager.open(fileName).bufferedReader().useLines { it.toList() }
        }

        // Activity 코드 내부
        labels = loadLabels(application.assets, "label.txt")

        // image processor
        imageProcessor = ImageProcessor.Builder()
            //    .add(NormalizeOp(0.0f, 255.0f))
            //    .add(TransformToGrayscaleOp())
            .add(ResizeOp(32,32, ResizeOp.ResizeMethod.BILINEAR))
            .build()
    }

    fun callImageSearch(): String
    {
        var intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, 100)
        return labels[maxIdx]
    }

    fun modelActivity(bitmap: Bitmap)
    {
        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)

        tensorImage = imageProcessor.process(tensorImage)

        val model = KContentImageModel.newInstance(this)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        maxIdx = 0
        outputFeature0.forEachIndexed{ index, fl ->
            if (outputFeature0[maxIdx] < fl){
                maxIdx = index
            }
        }

        model.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100){
            var uri = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            // imageView.setImageBitmap(bitmap)
            modelActivity(bitmap)
        }
    }
}
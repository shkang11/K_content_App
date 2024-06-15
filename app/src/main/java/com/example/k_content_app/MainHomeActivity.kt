package com.example.k_content_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.k_content_app.benner.RecommendFragment
import com.example.k_content_app.benner.RecommendFragment02
import com.example.k_content_app.benner.RecommendFragment03
import com.example.k_content_app.benner.RecommendViewpagerAdapter
import com.example.k_content_app.databinding.ActivityMainhomeBinding

class MainHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainhomeBinding
    private lateinit var viewPager: ViewPager2
    private var currentPage = 0
    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderThread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainhomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.homeRecommendVp
        val recommendAdapter = RecommendViewpagerAdapter(this)

        recommendAdapter.addFragment(RecommendFragment())
        recommendAdapter.addFragment(RecommendFragment02())
        recommendAdapter.addFragment(RecommendFragment03())
        recommendAdapter.addFragment(RecommendFragment())
        recommendAdapter.addFragment(RecommendFragment02())
        recommendAdapter.addFragment(RecommendFragment03())

        viewPager.adapter = recommendAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // 자동 슬라이딩 설정
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 2000) // 2초 후에 자동 슬라이딩
            }
        })

        startAutoSlide()

        // 퀴즈 설명으로 넘어가는 이미지 버튼
        val quizButton = binding.quizButton
        quizButton.setOnClickListener {
            val intent = Intent(this, GameDescriptionActivity::class.java)
            startActivity(intent)
        }

        // 이벤트 버튼 ApplyActivity로 넘어가는
        val eventButton = binding.eventButton
        eventButton.setOnClickListener {
            val intent = Intent(this, ApplyActivity::class.java)
            startActivity(intent)
        }

        binding.btn3.setOnClickListener {
            // 홈 버튼 클릭 동작
            // 이미 홈 화면이므로 새 액티비티를 시작하지 않음
        }

        binding.btn1.setOnClickListener {
            // 검색 버튼 클릭 동작
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun startAutoSlide() {
        sliderThread = Thread(PagerRunnable())
        sliderThread.start()
    }

    private inner class PagerRunnable : Runnable {
        override fun run() {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(2000)
                    sliderHandler.post { setPage() }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    Thread.currentThread().interrupt()
                }
            }
        }
    }

    private fun setPage() {
        if (currentPage == viewPager.adapter?.itemCount) {
            currentPage = 0
        }
        viewPager.setCurrentItem(currentPage++, true)
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
        if (::sliderThread.isInitialized && !sliderThread.isInterrupted) {
            sliderThread.interrupt()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::sliderThread.isInitialized && sliderThread.isInterrupted) {
            startAutoSlide()
        }
    }

    private val sliderRunnable = Runnable { setPage() }
}

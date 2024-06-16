package com.example.k_content_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.k_content_app.benner.RecommendFragment
import com.example.k_content_app.benner.RecommendFragment02
import com.example.k_content_app.benner.RecommendFragment03
import com.example.k_content_app.benner.RecommendViewpagerAdapter
import com.example.k_content_app.card_benner.CardAdapter
import com.example.k_content_app.card_benner.card_benner.CardData
import com.example.k_content_app.databinding.ActivityMainhomeBinding

class MainHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainhomeBinding
    private lateinit var viewPager: ViewPager2
    private var currentPage = 0
    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderThread: Thread

    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainhomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup recommended banner
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

        // Auto sliding for recommended banner
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000)
            }
        })

        startAutoSlide()

        // Quiz button
        binding.quizButton.setOnClickListener {
            val intent = Intent(this, GameDescriptionActivity::class.java)
            startActivity(intent)
        }

        // Event button
        binding.eventButton.setOnClickListener {
            val intent = Intent(this, ApplyActivity::class.java)
            startActivity(intent)
        }

        // Home button
        binding.btn3.setOnClickListener {}

        // Search button
        binding.btn1.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // Setup RecyclerView for latest banners
        recyclerView = binding.cardList
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val cardDataList = listOf(
            CardData("Card 1", R.drawable.banner1),
            CardData("Card 2", R.drawable.banner2),
            CardData("Card 3", R.drawable.banner3),
            CardData("Card 4", R.drawable.banner1)
        )

        cardAdapter = CardAdapter(this, cardDataList)
        recyclerView.adapter = cardAdapter
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

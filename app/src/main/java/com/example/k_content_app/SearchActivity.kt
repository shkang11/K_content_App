package com.example.k_content_app

import RVAdapter
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

class SearchActivity : AppCompatActivity() {
    private val items = mutableListOf<SearchModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        items.add(
            SearchModel("https://img.insight.co.kr/static/2021/10/15/700/img_20211015155350_01si69f0.webp",
                "Squid Game",
                "서울 특별시 도붕구 도봉중앙교회")
        )
        items.add(
            SearchModel("https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMTEwMDZfMzUg%2FMDAxNjMzNDgxNTE4NjY0.Sfwz7ZHhkTGnJxDI9J5ysXbtSVZK6sgQcZvGKyqy8mog.Ku6PBAiBolztFqtO8Pw2lNN57tsOIDiXZkgB7F2QIRIg.JPEG.aksgek7%2FIMG_0804.jpg&type=sc960_832",
                "Squid Game",
                "인천광역시 옹진군 자월면 선갑도")
        )
        items.add(
            SearchModel("https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMTEwMThfMjYy%2FMDAxNjM0NTEwMTk3MTg2.ptbdmMXAae00vj9dRo4N6wiozLIACHqXLqD1sRikQ7og.7XHBZN_aj1O3x2ZFiJdJWyPCxCZ2dIuMetOX8nj4GDEg.PNG.hjn4864%2Fimage.png&type=sc960_832",
                "Squid Game",
                "서울 서초구 매헌로 99 (매헌역)양재시민의숲")
        )
        items.add(
            SearchModel("https://img.insight.co.kr/static/2017/01/12/2000/936wrv93ful9j5m015u3.jpg",
                "Guardian - The Lonely and Great God",
                "인천광역시 동구 금곡동 한미서점")
        )
        items.add(
            SearchModel("https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAxNzAzMjZfMzgg%2FMDAxNDkwNDY1Mjg1MDg3.H2RnnGHFxxDTF-3X-nqnQ6NRvpuf3lqCY3OCHVzWKzwg.HRl568AmaAdhgataEVpSB20P-lvoBp-VDYuYqkvkA5Ag.JPEG.hellohanayo%2FIMG_4895.jpg&type=sc960_832",
                "Guardian - The Lonely and Great God",
                "강원도 강릉시 주문진읍 해안로 1609")
        )
        items.add(
            SearchModel("https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAxNjEyMTNfNjkg%2FMDAxNDgxNjAzNDg0NzIw.Si4PpGHZU5yYKYPxKckrJDEQAuB_xZBjnd_EFbVdvSIg.6dau76ljS52WcClnvLCgN1Gye69TNrgxHuSShmwqMrUg.JPEG.xogns4931%2Fc9ae8d7f-2e50-4f13-8c24-75c77b891ae9.png.jpg&type=sc960_832",
                "Guardian - The Lonely and Great God",
                "전라북도 고창군 공음면 보리나라 학원공장")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        val rvAdapter = RVAdapter(baseContext,items)
        recyclerView.adapter = rvAdapter

        recyclerView.layoutManager = LinearLayoutManager(this)

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                rvAdapter.filter(newText.orEmpty())
                return true
            }
        })
    }

}
package com.example.k_content_app

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class UserInfoFragment : Fragment() {
    // RecyclerView 및 어댑터 선언
    private lateinit var userReviewList: RecyclerView
    private lateinit var userReviewListAdapter: ReviewAdapter
    private lateinit var auth: FirebaseAuth

    // Firestore 인스턴스 선언
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_info, container, false)
        auth = Firebase.auth

        // 리뷰 리사이클러뷰 및 어댑터 초기화
        userReviewList = view.findViewById(R.id.userReviewList)
        userReviewList.layoutManager = LinearLayoutManager(context)
        userReviewListAdapter = ReviewAdapter(mutableListOf()) // 빈 리스트로 초기화
        userReviewList.adapter = userReviewListAdapter

        // "enrollInfo"를 클릭했을 때 AlertDialog를 표시합니다.
        view.findViewById<LinearLayout>(R.id.enrollInfo).setOnClickListener {
            showUserInfoDialog()
        }

        view.findViewById<Button>(R.id.btn1).setOnClickListener {
            it.findNavController().navigate(R.id.action_userInfoFragment_to_searchingFragment)
        }
        view.findViewById<TextView>(R.id.userid).setText("@"+auth.currentUser!!.uid)
        view.findViewById<TextView>(R.id.username).setText(auth.currentUser!!.displayName)
        // 사용자 리뷰 데이터 가져오기
        getReviewsForCurrentUser()

        return view
    }

    private fun showUserInfoDialog() {
        // AlertDialog를 만들고 custom_dialog.xml을 사용하여 사용자 정보를 표시합니다.
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.customdialog, null)
        builder.setView(dialogView)

        // AlertDialog에 있는 TextView를 찾습니다.
        val userIdTextView = dialogView.findViewById<TextView>(R.id.userinfoArea)
        val userEmailTextView = dialogView.findViewById<TextView>(R.id.useremailArea)
        val userDisplayTextView = dialogView.findViewById<TextView>(R.id.userDisplayArea)

        // 여기에서 사용자 정보를 가져와서 TextView에 설정합니다.
        val user = Firebase.auth.currentUser
        userIdTextView.text = "사용자 UID: ${user?.uid}"
        userEmailTextView.text = "사용자 이메일: ${user?.email}"
        userDisplayTextView.text = "사용자 디스플레이: ${user?.displayName}"

        // AlertDialog를 표시합니다.
        val dialog = builder.create()
        dialog.show()
    }

    // 사용자 리뷰 데이터 가져오는 함수
    private fun getReviewsForCurrentUser() {
        val currentUserUid = Firebase.auth.currentUser?.uid
        if (currentUserUid != null) {
            db.collection("reviews")
                .whereEqualTo("uid", currentUserUid)
                .get()
                .addOnSuccessListener { documents ->
                    val reviews = mutableListOf<Review>()
                    for (document in documents) {
                        val title = document.getString("title") ?: ""
                        val content = document.getString("content") ?: ""
                        val displayName = document.getString("displayName") ?: ""
                        reviews.add(Review(displayName, title, content))
                    }
                    userReviewListAdapter.updateReviews(reviews)
                }
                .addOnFailureListener { exception ->
                    // 실패 시 처리
                }
        }
    }

    // 리뷰 데이터 클래스
    data class Review(
        val displayName: String,
        val title: String,
        val content: String
    )

    // 리뷰 어댑터
    class ReviewAdapter(private val reviews: MutableList<Review>) :
        RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

        // 뷰홀더 클래스
        class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
            val reviewTitleTextView: TextView = itemView.findViewById(R.id.ReviewTitle)
            val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.review_item, parent, false)
            return ReviewViewHolder(view)
        }

        override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
            val review = reviews[position]
            holder.usernameTextView.text = review.displayName
            holder.reviewTitleTextView.text = review.title
            holder.commentTextView.text = review.content
        }

        override fun getItemCount(): Int {
            return reviews.size
        }

        fun updateReviews(newReviews: List<Review>) {
            reviews.clear()
            reviews.addAll(newReviews)
            notifyDataSetChanged()
        }
    }
}

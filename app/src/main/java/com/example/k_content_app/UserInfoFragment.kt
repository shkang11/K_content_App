package com.example.k_content_app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class UserInfoFragment : Fragment() {
    // RecyclerView 및 어댑터 선언
    private lateinit var userReviewList: RecyclerView
    private lateinit var userReviewListAdapter: ReviewAdapter
    private lateinit var auth: FirebaseAuth

    // Firestore 인스턴스 선언
    private val db = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

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

        // 업로드 버튼 클릭 시 갤러리에서 이미지 선택
        view.findViewById<Button>(R.id.uploadBtn).setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }

        view.findViewById<Button>(R.id.btn1).setOnClickListener {
            it.findNavController().navigate(R.id.action_userInfoFragment_to_searchingFragment)
        }

        view.findViewById<TextView>(R.id.userid).text = "@" + auth.currentUser!!.uid
        view.findViewById<TextView>(R.id.username).text = auth.currentUser!!.displayName

        // 프로필 이미지 설정
        setUserProfileImage(view.findViewById(R.id.img_user))

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
                        val img = document.getString("img") ?: "@drawable/userimg"
                        reviews.add(Review(displayName, title, content, img))
                    }
                    userReviewListAdapter.updateReviews(reviews)
                }
                .addOnFailureListener { exception ->
                    // 실패 시 처리
                }
        }
    }

    private fun setUserProfileImage(imageView: ImageView) {
        val currentUserUid = Firebase.auth.currentUser?.uid
        if (currentUserUid != null) {
            db.collection("users").document(currentUserUid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val imageUrl = document.getString("img")
                        if (imageUrl != null && imageUrl.isNotEmpty()) {
                            // 이미지 URL이 존재하면 Glide를 사용하여 이미지뷰에 설정
                            Glide.with(this)
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                                .into(imageView)
                        } else {
                            // URL이 없으면 기본 이미지 설정
                            imageView.setImageResource(R.drawable.userimg)
                        }
                    } else {
                        // 문서가 없으면 기본 이미지 설정
                        imageView.setImageResource(R.drawable.userimg)
                    }
                }
                .addOnFailureListener { exception ->
                    // 실패 시 기본 이미지 설정
                    imageView.setImageResource(R.drawable.userimg)
                }
        } else {
            // 사용자 UID가 없으면 기본 이미지 설정
            imageView.setImageResource(R.drawable.userimg)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            // Show confirmation dialog
            AlertDialog.Builder(requireContext())
                .setTitle("프로필 사진 설정")
                .setMessage("선택하신 사진으로 프로필로 설정 하시겠습니까?")
                .setPositiveButton("Yes") { dialog, which ->
                    uploadImage()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val ref = storage.reference.child("images/${auth.currentUser!!.uid}/${System.currentTimeMillis()}")
            val uploadTask = ref.putFile(filePath!!)

            uploadTask.addOnSuccessListener {
                // 이미지 업로드 성공 시 Firestore에 URL 저장
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // 'users' 컬렉션의 'img' 필드에 이미지 URL 저장
                    db.collection("users").document(auth.currentUser!!.uid)
                        .update("img", imageUrl)
                        .addOnSuccessListener {
                            // 이미지 URL 저장 성공 시 처리
                            setUserProfileImage(requireView().findViewById(R.id.img_user))
                        }
                        .addOnFailureListener { e ->
                            // 이미지 URL 저장 실패 시 처리
                        }
                }
            }.addOnFailureListener { e ->
                // 업로드 실패 시 처리
            }
        }
    }

    // 리뷰 데이터 클래스
    data class Review(
        val displayName: String,
        val title: String,
        val content: String,
        val img: String
    )

    // 리뷰 어댑터
    class ReviewAdapter(private val reviews: MutableList<Review>) :
        RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

        // 뷰홀더 클래스
        class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
            val reviewTitleTextView: TextView = itemView.findViewById(R.id.ReviewTitle)
            val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
            val userImageView: ImageView = itemView.findViewById(R.id.userprofileImage)
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
            if (review.img != "@drawable/userimg") {
                Glide.with(holder.itemView.context)
                    .load(review.img)
                    .circleCrop() // 이미지 동그랗게 자르기
                    .into(holder.userImageView)
            } else {
                holder.userImageView.setImageResource(R.drawable.userimg)
            }
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

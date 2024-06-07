import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.k_content_app.R
import com.example.k_content_app.SearchModel

class RVAdapter(val context: Context, val originalList: MutableList<SearchModel>, searchText: String? = "") :RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    // 필터링된 결과를 담을 리스트
    private var filteredList: MutableList<SearchModel> = ArrayList()

    // 아이템 클릭 리스너
    private var itemClickListener: ((SearchModel) -> Unit)? = null
    init {
        // 초기에는 전체 리스트를 보여줍니다.
        filteredList.addAll(originalList)
        // 검색어가 비어있지 않은 경우에는 필터링을 수행합니다.
        if (searchText!!.isNotBlank()) {
            filter(searchText.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_item,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RVAdapter.ViewHolder, position: Int) {
        holder.bindItems(filteredList[position])
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindItems(item: SearchModel){

            val rv_img = itemView.findViewById<ImageView>(R.id.rvimgArea)
            val rv_title = itemView.findViewById<TextView>(R.id.rvtextArea)
            val rv_location = itemView.findViewById<TextView>(R.id.rvlocationArea)

            Glide.with(context)
                .load(item.imageUrl)
                .into(rv_img)
            rv_title.text = item.dramaTitle
            rv_location.text = item.location

        }

        init {
            itemView.setOnClickListener {
                // 클릭된 아이템의 위치를 가져옵니다.
                val position = adapterPosition
                // 위치가 유효한지 확인하고 클릭 리스너가 등록되어 있으면 실행합니다.
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.invoke(filteredList[position])
                }
            }
        }
    }

    // 검색어에 따라 데이터 필터링 함수
    fun filter(text: String) {
        filteredList.clear()
        val searchText = text.toLowerCase().trim()
        if (text.isEmpty()) {
            // 검색어가 비어있으면 전체 리스트를 보여줍니다.
            filteredList.addAll(originalList)
        } else {
            // 검색어가 비어있지 않으면 검색어를 포함하는 항목만 보여줍니다.
            originalList.forEach { item ->
                val titleContains = item.dramaTitle.toLowerCase().contains(searchText)
                val locationContains = item.location.toLowerCase().contains(searchText)
                if (titleContains || locationContains) {
                    filteredList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    // 아이템 클릭 리스너 설정 메서드
    fun setOnItemClickListener(listener: (SearchModel) -> Unit) {
        itemClickListener = listener
    }

}

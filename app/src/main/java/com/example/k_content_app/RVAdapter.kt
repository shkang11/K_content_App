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

class RVAdapter(val context: Context, val originalList : MutableList<SearchModel>) :RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    // 필터링된 결과를 담을 리스트
    private var filteredList: MutableList<SearchModel> = ArrayList()

    init {
        // 초기에는 전체 리스트를 보여줍니다.
        filteredList.addAll(originalList)
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
    }

    // 검색어에 따라 데이터 필터링 함수
    fun filter(text: String) {
        filteredList.clear()
        if (text.isEmpty()) {
            // 검색어가 비어있으면 전체 리스트를 보여줍니다.
            filteredList.addAll(originalList)
        } else {
            // 검색어가 비어있지 않으면 검색어를 포함하는 항목만 보여줍니다.
            val searchText = text.toLowerCase().trim()
            originalList.forEach { item ->
                if (item.dramaTitle.toLowerCase().contains(searchText)) {
                    filteredList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }
}

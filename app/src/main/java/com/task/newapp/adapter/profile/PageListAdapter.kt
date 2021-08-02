package com.task.newapp.adapter.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.models.ResponsePageList
import com.task.newapp.utils.load
import java.util.*

class PageListAdapter(
    private val applicationContext: Context,
    private val dataSet: ArrayList<ResponsePageList.Data>) :
    RecyclerView.Adapter<PageListAdapter.ViewHolder>() {

    var onItemClick: ((ArrayList<ResponsePageList.Data>, Int) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_page_name: TextView = view.findViewById(R.id.txt_page_name)
        val txt_page_member: TextView = view.findViewById(R.id.txt_page_member)
        val iv_page_star: ImageView = view.findViewById(R.id.iv_page_star)
        val iv_page_info: ImageView = view.findViewById(R.id.iv_page_info)
        val iv_page_profile: ImageView = view.findViewById(R.id.iv_page_profile)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_page_list, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.txt_page_name.text = dataSet[position].name
        viewHolder.txt_page_member.text = dataSet[position].followers.toString() + " " + applicationContext.resources.getString(R.string.followers)
        viewHolder.iv_page_profile.load(dataSet[position].icon,false)

//        viewHolder.tv_like_count.text = dataSet[position].getlikesCount.toString()
//        viewHolder.tv_comment_count.text = dataSet[position].commentsCount.toString()
//        Glide.with(applicationContext).asBitmap().load(dataSet[position].contents[0].content).into(viewHolder.iv_post_content)

//        viewHolder.itemView.setOnClickListener {
//            if (onItemClick != null) {
//                onItemClick!!.invoke(dataSet, position)
//            }
//        }
    }

    fun setData(data: ArrayList<ResponsePageList.Data>) {
        dataSet.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataSet.size


}

package com.task.newapp.adapter.profile

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso
import com.task.newapp.R
import com.task.newapp.realmDB.getUserNameFromId
import com.task.newapp.realmDB.models.Chats

class GroupListAdapter(
    private val dataSet: ArrayList<Chats>,
) :
    RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {

    var onItemClick: ((Int, Int) -> Unit)? = null


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.txt_user_name)
        val txtAccid: TextView = view.findViewById(R.id.txt_accid)
        val ivUserProfile: ImageView = view.findViewById(R.id.iv_user_profile)
        val ivMsg: ImageView = view.findViewById(R.id.iv_msg)
        val btnFollowUnfollow: TextView = view.findViewById(R.id.btn_follow_unfollow)
        val btnTime: TextView = view.findViewById(R.id.btn_time)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_follower_following, viewGroup, false)
        return ViewHolder(view)
    }

    private val isCaching = true

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.btnFollowUnfollow.visibility = GONE
        viewHolder.txtUserName.text = dataSet[position].name
        val userId = dataSet[position].group_user_with_settings.filter {
            it.status != "Inactive"
        }.map {
            it.user_id
        }
        viewHolder.txtAccid.text = getUserNameFromId(userId)

//        viewHolder.ivUserProfile.load(dataSet[position].group_data?.grp_icon ?: "", false)

        Glide.with(viewHolder.itemView.context)
            .load(dataSet[position].group_data?.grp_icon)
//            .apply(RequestOptions.skipMemoryCacheOf(!isCaching))
//            .apply(RequestOptions.diskCacheStrategyOf(if (isCaching) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
            .placeholder(R.drawable.logo)
            .fallback(R.drawable.logo)
            .error(R.drawable.logo)
            .into(viewHolder.ivUserProfile)

        viewHolder.itemView.setOnClickListener {
            if (onItemClick != null) {
                onItemClick!!.invoke(dataSet[position].id, position)
            }
        }
    }

    fun setData(data: List<Chats>) {
        dataSet.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataSet.size

}

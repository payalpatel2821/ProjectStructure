package com.task.newapp.adapter.profile

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.realmDB.getSingleUserDetails
import com.task.newapp.realmDB.models.GroupUser
import com.task.newapp.utils.getCurrentUserId
import com.task.newapp.utils.load

class GroupMemberListAdapter(
    private val dataSet: ArrayList<GroupUser>,
) :
    RecyclerView.Adapter<GroupMemberListAdapter.ViewHolder>() {

    private var limit: Int = 0

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.txt_user_name)
        val ivUserProfile: ImageView = view.findViewById(R.id.iv_user_profile)
        val btnCaptain: TextView = view.findViewById(R.id.btn_captain)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_grp_member, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val userDetail = getSingleUserDetails(dataSet[position].user_id)!!
        viewHolder.ivUserProfile.load(userDetail.profile_image, true)
        if (dataSet[position].user_id == getCurrentUserId()) {
            viewHolder.txtUserName.text = "You"
        } else {
            viewHolder.txtUserName.text = userDetail.first_name + " " + userDetail.last_name
        }

        if (dataSet[position].is_admin == 1) {
            viewHolder.btnCaptain.visibility = VISIBLE
        } else {
            viewHolder.btnCaptain.visibility = GONE
        }

    }

    fun setData(data: ArrayList<GroupUser>, limit: Int) {
        this.dataSet.addAll(data)
        this.limit = limit
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (dataSet.size > limit) {
            limit
        }else{
            5
        }
    }
}

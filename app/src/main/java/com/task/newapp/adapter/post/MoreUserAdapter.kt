package com.task.newapp.adapter.post

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.databinding.ItemMoreUserBinding
import com.task.newapp.databinding.ItemPostBinding
import com.task.newapp.models.post.ResponseGetAllPost
import com.task.newapp.models.post.ResponseGetAllPost.All_Post_Data.Tagged
import com.task.newapp.ui.activities.profile.OtherUserProfileActivity
import com.task.newapp.utils.Constants
import com.task.newapp.utils.launchActivity
import com.task.newapp.utils.load
import de.hdodenhof.circleimageview.CircleImageView

class MoreUserAdapter(
    private val activity: Activity,
    val arrayListTemp: List<Tagged>
) : RecyclerView.Adapter<MoreUserAdapter.ViewHolder>() {

    var arrayList: List<Tagged> = arrayListTemp as ArrayList<Tagged>

    inner class ViewHolder(private val layoutBinding: ItemMoreUserBinding) : RecyclerView.ViewHolder(layoutBinding.root) {

        fun populateItemRows(allTagData: Tagged) {
            layoutBinding.txtUsername.text = allTagData.first_name + " " + allTagData.last_name

            layoutBinding.itemContactImage.load(allTagData.profile_image, true, layoutBinding.txtUsername.text.trim().toString(), allTagData.profileColor)
//            layoutBinding.itemContactImage.load(allTagData.profile_image, false)

            layoutBinding.txtAccountName.text = allTagData.accountId

            layoutBinding.itemContactFrame.setOnClickListener {
                val id = allTagData.id
                activity.launchActivity<OtherUserProfileActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.user_id, id)
                }
            }

            if (bindingAdapterPosition == arrayList.size - 1) {
                layoutBinding.divider.visibility = View.GONE
            } else
                layoutBinding.divider.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutBinding: ItemMoreUserBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_more_user, parent, false)
        return ViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val allTagData: Tagged = this.arrayList[position]
        holder.populateItemRows(allTagData)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


}
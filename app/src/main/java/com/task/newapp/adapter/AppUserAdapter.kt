package com.task.newapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.models.ResponseIsAppUser
import com.task.newapp.ui.activities.profile.OtherUserProfileActivity
import com.task.newapp.utils.Constants
import com.task.newapp.utils.launchActivity
import com.task.newapp.utils.load
import de.hdodenhof.circleimageview.CircleImageView

class AppUserAdapter(
    private val activity: AppCompatActivity,
    private val data: List<ResponseIsAppUser.Data>
) : RecyclerView.Adapter<AppUserAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imageView: CircleImageView = view.findViewById(R.id.item_contact_image)
        val textViewName: TextView = view.findViewById(R.id.item_contact_name)
        val textViewNumber: TextView = view.findViewById(R.id.item_contact_number)
        val divider: View = view.findViewById(R.id.divider)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(
            R.layout.item_explore_contact_number,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewName.text = data[position].firstName + " " + data[position].lastName
        holder.imageView.load(data[position].profileImage, true, holder.textViewName.text.trim().toString(), data[position].profileColor)
        if (data[position].mobile.isNullOrEmpty()) {
            holder.textViewNumber.text = data[position].email
        } else {
            holder.textViewNumber.text = data[position].mobile
        }
        holder.itemView.setOnClickListener{
            val id=data[position].id
            activity.launchActivity<OtherUserProfileActivity> {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(Constants.user_id, id)
            }
        }
        if (position == data.size - 1) {
            holder.divider.visibility=View.GONE
        }else
            holder.divider.visibility=View.VISIBLE
    }

    override fun getItemCount(): Int {
        return data.size
    }

}
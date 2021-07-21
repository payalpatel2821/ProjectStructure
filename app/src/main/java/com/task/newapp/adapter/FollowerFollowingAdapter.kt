package com.task.newapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.appizona.yehiahd.fastsave.FastSave
import com.bumptech.glide.Glide
import com.task.newapp.R
import com.task.newapp.models.ResponseUserFollowingFollower
import com.task.newapp.utils.Constants
import com.task.newapp.utils.Constants.Companion.ProfileNavigation.*
import com.task.newapp.utils.DateTimeUtils
import com.task.newapp.utils.DialogUtils
import com.task.newapp.utils.showToast
import java.text.SimpleDateFormat
import java.util.*

class FollowerFollowingAdapter(
    private val applicationContext: Context,
    private val dataSet: ArrayList<ResponseUserFollowingFollower.Data>,
    private val from: String,
    private val by: String,
    private val activity: AppCompatActivity
) :
    RecyclerView.Adapter<FollowerFollowingAdapter.ViewHolder>() {

    var onFollowItemClick: ((String, ResponseUserFollowingFollower.Data, ViewHolder, Int, String) -> Unit)? = null
    var onItemClick: ((Int, ArrayList<ResponseUserFollowingFollower.Data>, Int) -> Unit)? = null


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_user_name: TextView = view.findViewById(R.id.txt_user_name)
        val txt_accid: TextView = view.findViewById(R.id.txt_accid)
        val iv_user_profile: ImageView = view.findViewById(R.id.iv_user_profile)
        val iv_msg: ImageView = view.findViewById(R.id.iv_msg)
        val btn_follow_unfollow: TextView = view.findViewById(R.id.btn_follow_unfollow)
        val btn_time: TextView = view.findViewById(R.id.btn_time)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_follower_following, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        when (from) {
            FROM_FOLLOWERS.fromname -> {
                viewHolder.iv_msg.visibility = GONE
                viewHolder.btn_time.visibility = GONE
                if (dataSet[position].isFollow != 1) {
                    setFollowText(viewHolder)
                }
            }
            FROM_FOLLOWINGS.fromname -> {
                viewHolder.iv_msg.visibility = GONE
                viewHolder.btn_time.visibility = GONE
                if (by != "My") {
                    if (dataSet[position].isFollow != 1) {
                        setFollowText(viewHolder)
                    }
                }
            }
            FROM_PROFILE_VIEWS.fromname -> {
                viewHolder.iv_msg.visibility = GONE
                viewHolder.btn_follow_unfollow.visibility = GONE
                viewHolder.btn_time.visibility = VISIBLE
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                viewHolder.btn_time.text=DateTimeUtils.instance!!.getConversationTimestamp(DateTimeUtils.instance!!.getLongFromDateString(dataSet[position].view_date, formatter))
            }
            FROM_FRIENDS.fromname -> {
                viewHolder.iv_msg.visibility = VISIBLE
                viewHolder.btn_follow_unfollow.visibility = GONE
                viewHolder.btn_time.visibility = GONE
            }
        }

        var my_id = FastSave.getInstance().getInt(Constants.prefUserId, 0)
        if (my_id == dataSet[position].id) {
            viewHolder.btn_follow_unfollow.visibility = GONE
        }

        viewHolder.btn_follow_unfollow.setOnClickListener {
            when (from) {
                FROM_FOLLOWERS.fromname -> {
                    if (dataSet[position].isFollow == 0) {
                        checkFollowUnfollow(viewHolder, dataSet[position], position, "follow", "")
                    } else {
                        checkFollowUnfollow(viewHolder, dataSet[position], position, "unfollow", "other")
                    }
                }
                FROM_FOLLOWINGS.fromname -> {
                    if (by == "My") {
                        checkFollowUnfollow(viewHolder, dataSet[position], position, "unfollow", "my")
                    } else {
                        if (dataSet[position].isFollow == 0) {
                            checkFollowUnfollow(viewHolder, dataSet[position], position, "follow", "")
                        } else {
                            checkFollowUnfollow(viewHolder, dataSet[position], position, "unfollow", "other")
                        }
                    }
                }
            }
        }

        viewHolder.iv_msg.setOnClickListener{
            applicationContext.showToast("Redirect to chatting activity")
        }

        viewHolder.txt_user_name.text = dataSet[position].firstName + " " + dataSet[position].lastName
        viewHolder.txt_accid.text = dataSet[position].accountId
        Glide.with(applicationContext).asBitmap().load(dataSet[position].profileImage).into(viewHolder.iv_user_profile)

        viewHolder.itemView.setOnClickListener {
            if (onItemClick != null) {
                onItemClick!!.invoke(my_id, dataSet, position)
            }
        }
    }

    fun setFollowText(viewHolder: FollowerFollowingAdapter.ViewHolder) {
        viewHolder.btn_follow_unfollow.setTextColor(applicationContext.resources.getColor(R.color.theme_color))
        viewHolder.btn_follow_unfollow.setBackgroundResource(0)
        viewHolder.btn_follow_unfollow.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        viewHolder.btn_follow_unfollow.text = applicationContext.resources.getString(R.string.follow)
    }

    fun setUnFollowText(viewHolder: ViewHolder, dataSet: ResponseUserFollowingFollower.Data) {
        viewHolder.btn_follow_unfollow.setTextColor(applicationContext.resources.getColor(R.color.black))
        viewHolder.btn_follow_unfollow.setBackgroundResource(R.drawable.bg_border)
        val padding: Int = applicationContext.resources.getDimensionPixelSize(R.dimen._5sdp)
        viewHolder.btn_follow_unfollow.setPadding(padding, 0, 0, 0)
        viewHolder.btn_follow_unfollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_right_resize, 0, 0, 0)
        viewHolder.btn_follow_unfollow.text = applicationContext.resources.getString(R.string.following)
        dataSet.isFollow = 1
    }

    fun setData(data: ArrayList<ResponseUserFollowingFollower.Data>) {
        dataSet.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataSet.size

    private fun checkFollowUnfollow(viewHolder: ViewHolder, dataSet1: ResponseUserFollowingFollower.Data, position: Int, f_uf: String, from: String) {
        if (f_uf == "follow") {
            if (onFollowItemClick != null) {
                onFollowItemClick!!.invoke(f_uf, dataSet1, viewHolder, position, from)
            }
        } else {
            DialogUtils().showConfirmationDialog(activity, "", applicationContext.resources.getString(R.string.unfollow_confirm_msg), object : DialogUtils.DialogCallbacks {
                override fun onPositiveButtonClick() {
                    if (onFollowItemClick != null) {
                        onFollowItemClick!!.invoke(f_uf, dataSet1, viewHolder, position, from)
                    }
                }

                override fun onNegativeButtonClick() {

                }

                override fun onDefaultButtonClick(actionName: String) {
                }

            })
        }
    }

    fun updateData(position: Int) {
        dataSet.removeAt(position)
        notifyDataSetChanged()
    }

}

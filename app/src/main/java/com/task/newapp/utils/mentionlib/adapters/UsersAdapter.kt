package com.task.newapp.utils.mentionlib.adapters

import android.content.Context
import com.percolate.mentions.sample.adapters.RecyclerArrayAdapter
import com.task.newapp.utils.mentionlib.adapters.UsersAdapter.UserViewHolder
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import com.squareup.picasso.Picasso
import android.text.Spannable
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.models.post.ResponseFriendsList
import com.task.newapp.utils.load
import com.task.newapp.utils.mentionlib.utils.StringUtils
import java.util.*

/**
 * Adapter to the mentions list shown to display the result of an '@' mention.
 */
class UsersAdapter(
    /**
     * [Context]
     */
    private val context: Context
//) : RecyclerArrayAdapter<User?, UserViewHolder?>() {
) : RecyclerArrayAdapter<ResponseFriendsList.Data, UserViewHolder?>() {
    /**
     * Current search string typed by the user.  It is used highlight the query in the
     * search results.  Ex: @bill.
     */
    private var currentQuery: String? = null

    /**
     * [ForegroundColorSpan].
     */
    private val colorSpan: ForegroundColorSpan

    /**
     * Setter for what user has queried.
     */
    fun setCurrentQuery(currentQuery: String) {
        if (StringUtils.isNotBlank(currentQuery)) {
            this.currentQuery = currentQuery.toLowerCase(Locale.US)
        }
    }

    /**
     * Create UI with views for user name and picture.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_mention_user, parent, false)
        return UserViewHolder(view)
    }

    /**
     * Display user name and picture.
     */
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val mentionsUser = getItem(position)
//        if (mentionsUser != null && StringUtils.isNotBlank(mentionsUser.fullName)) {
//            holder.name.setText(mentionsUser.fullName, TextView.BufferType.SPANNABLE)
//            highlightSearchQueryInUserName(holder.name.text)
//            if (StringUtils.isNotBlank(mentionsUser.imageUrl)) {
//                holder.imageView.visibility = View.VISIBLE
//                Picasso.with(context)
//                    .load(mentionsUser.imageUrl)
//                    .into(holder.imageView)
//            } else {
//                holder.imageView.visibility = View.GONE
//            }
//        }

        if (mentionsUser != null && StringUtils.isNotBlank(mentionsUser.firstName)) {

            holder.name.setText(mentionsUser.firstName + " " + mentionsUser.lastName, TextView.BufferType.SPANNABLE)
            highlightSearchQueryInUserName(holder.name.text)

            holder.txt_accid.text = mentionsUser.accountId
            holder.img_profile.load(mentionsUser.profileImage, true, holder.name.text.trim().toString(), mentionsUser.profileColor)
        }
    }

    /**
     * Highlights the current search text in the mentions list.
     */
    private fun highlightSearchQueryInUserName(userName: CharSequence) {
        if (StringUtils.isNotBlank(currentQuery)) {
            val searchQueryLocation = userName.toString().toLowerCase(Locale.US).indexOf(currentQuery!!)
            if (searchQueryLocation != -1) {
                val userNameSpannable = userName as Spannable
                userNameSpannable.setSpan(
                    colorSpan,
                    searchQueryLocation,
                    searchQueryLocation + currentQuery!!.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    /**
     * View holder for user.
     */
    class UserViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val name: TextView = itemView!!.findViewById(R.id.txt_name)
        val img_profile: ImageView = itemView!!.findViewById(R.id.img_profile)
        val txt_accid: TextView = itemView!!.findViewById(R.id.txt_accid)
    }

    init {
        val orange = ContextCompat.getColor(context, R.color.theme_color)
        colorSpan = ForegroundColorSpan(orange)
    }
}
package com.task.newapp.adapter.contact

import android.content.Intent
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.databinding.ItemExploreContactNumberBinding
import com.task.newapp.models.ResponseIsAppUser
import com.task.newapp.realmDB.deleteOneContactHistory
import com.task.newapp.realmDB.insertContactHistoryData
import com.task.newapp.realmDB.prepareContactHistoryData
import com.task.newapp.ui.activities.profile.OtherUserProfileActivity
import com.task.newapp.utils.Constants
import com.task.newapp.utils.launchActivity
import com.task.newapp.utils.load
import okhttp3.internal.notify

class ExploreAdapter(
    private val mainActivity: FragmentActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var allUser: ArrayList<ResponseIsAppUser.Data> = ArrayList<ResponseIsAppUser.Data>()
    private var isHistory: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: ItemExploreContactNumberBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_explore_contact_number, parent, false)
        return ExploreViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = allUser[position]
        (holder as ExploreViewHolder).populateItemRows(item, isHistory,position,allUser,this)

        holder.itemView.setOnClickListener {
            insertContactHistoryData(prepareContactHistoryData(item))
            mainActivity.launchActivity<OtherUserProfileActivity> {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(Constants.user_id, item.id)
            }
        }

    }

    override fun getItemCount(): Int {
        return allUser.size
    }

    fun setData(arrayList: ArrayList<ResponseIsAppUser.Data>, isHistory: Boolean) {
        this.isHistory = isHistory
        val diffCallback = CommentDiffCallback(this.allUser, arrayList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.allUser.clear()
        this.allUser.addAll(arrayList)

        diffResult.dispatchUpdatesTo(this)
    }

    class ExploreViewHolder(private val layoutBinding: ItemExploreContactNumberBinding) : RecyclerView.ViewHolder(layoutBinding.root) {

        fun populateItemRows(item: ResponseIsAppUser.Data, isHistory: Boolean, position: Int, allUser: ArrayList<ResponseIsAppUser.Data>, exploreAdapter: ExploreAdapter) {
            layoutBinding.itemContactName.text = item.firstName + " " + item.lastName
            layoutBinding.itemContactImage.load(item.profileImage, true, layoutBinding.itemContactName.text.trim().toString(), item.profileColor)
            layoutBinding.itemContactNumber.text = item.accountId
            if (isHistory) {
                layoutBinding.ivRemoveContact.visibility = VISIBLE
            } else {
                layoutBinding.ivRemoveContact.visibility = GONE
            }
            layoutBinding.ivRemoveContact.setOnClickListener{
                deleteOneContactHistory(item.id)
                allUser.removeAt(position)
                exploreAdapter.notifyDataSetChanged()
            }
        }
    }

    //-----------------------------------DiffUtil Class-----------------------------------------
    class CommentDiffCallback(
        oldCommentList: List<ResponseIsAppUser.Data>,
        newCommentList: List<ResponseIsAppUser.Data>
    ) : DiffUtil.Callback() {
        private val mOldCommentList: List<ResponseIsAppUser.Data> = oldCommentList
        private val mNewCommentList: List<ResponseIsAppUser.Data> = newCommentList
        override fun getOldListSize(): Int {
            return mOldCommentList.size
        }

        override fun getNewListSize(): Int {
            return mNewCommentList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldCommentList[oldItemPosition].id == mNewCommentList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldCommentList[oldItemPosition] == mNewCommentList[newItemPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            // Implement method if you're going to use ItemAnimator
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

    }
}



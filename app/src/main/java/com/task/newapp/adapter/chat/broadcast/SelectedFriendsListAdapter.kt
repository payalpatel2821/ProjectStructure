package com.task.newapp.adapter.post

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.databinding.ItemSelectedFriendsBinding
import com.task.newapp.models.chat.SelectFriendWrapperModel
import com.task.newapp.utils.load
import java.util.*
import kotlin.collections.ArrayList


class SelectedFriendsListAdapter(private val mActivity: Activity) : RecyclerView.Adapter<SelectedFriendsListAdapter.ViewHolder>() {
    private val TAG = javaClass.simpleName
    private var listData: ArrayList<SelectFriendWrapperModel> = ArrayList()
    private var onItemClickListener: AdapterView.OnItemClickListener? = null
    fun setOnItemClickListener(onItemClickListener: AdapterView.OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun onItemHolderClick(holder: SelectedFriendsListAdapter.ViewHolder) {
        onItemClickListener?.onItemClick(null, holder.itemView, holder.adapterPosition, holder.itemId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedFriendsListAdapter.ViewHolder {
        val layoutBinding: ItemSelectedFriendsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_selected_friends, parent, false
        )
        return ViewHolder(layoutBinding, this)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.populateItemRows(item, position, null)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    fun getItem(position: Int): SelectFriendWrapperModel {
        return listData[position]
    }

    override fun getItemCount(): Int = listData.size

    fun doRefresh(data: List<SelectFriendWrapperModel>) {
        this.listData.addAll(data)
        notifyDataSetChanged()
    }

    fun addSelected(data: SelectFriendWrapperModel) {
        this.listData.add(data)
        notifyItemInserted(listData.size - 1)
    }

    fun removeSelected(data: SelectFriendWrapperModel) {
        val index = listData.indexOf(data)
        this.listData.removeAt(index)
        notifyItemRemoved(index)
    }


    inner class ViewHolder(private val layoutBinding: ItemSelectedFriendsBinding, private val mAdapter: SelectedFriendsListAdapter) : RecyclerView.ViewHolder(layoutBinding.root),
        View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.iv_delete_friend -> {
                    listData[adapterPosition].isChecked = !listData[adapterPosition].isChecked
                    mAdapter.onItemHolderClick(this)

                }
            }
        }

        fun setData(obj: SelectFriendWrapperModel) {
            layoutBinding.txtName.text = obj.firstName
            layoutBinding.imgProfile.load(obj.profileImage ?: "", true, obj.firstName + " " + obj.lastName, obj.profileColor)
            if (obj.isEdit) {
                layoutBinding.ivDeleteFriend.visibility = View.VISIBLE

            } else {
                layoutBinding.ivDeleteFriend.visibility = View.GONE
            }

        }

        /**
         * populate rows
         *
         * @param holder
         * @param position
         */
        fun populateItemRows(obj: SelectFriendWrapperModel, position: Int, listPayload: List<Any>?) {

            setData(obj)
            /*if (listPayload == null || listPayload.isEmpty()) {

            } else {
                showLog("PAYLOAD :", Gson().toJson(listPayload))
                 for (payload in listPayload) {

                 }
            }*/
        }

        init {
            layoutBinding.ivDeleteFriend.setOnClickListener(this)
        }
    }


}

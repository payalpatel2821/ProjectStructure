package com.task.newapp.adapter.chat

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.databinding.ItemChatBinding
import com.task.newapp.models.ChatListAdapterModel
import com.task.newapp.realmDB.getHookCount
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.utils.DateTimeUtils
import com.task.newapp.utils.load
import com.task.newapp.utils.swipelayout.adapters.RecyclerSwipeAdapter


class ChatListAdapter(private val mActivity: Activity, private val listener: OnChatItemClickListener) : RecyclerSwipeAdapter<ChatListAdapter.ViewHolder>() {
    private val TAG = javaClass.simpleName
    private var onItemClickListener: OnItemClickListener? = null
    private var list_data: List<ChatListAdapterModel>? = null

    fun doRefresh(list_data: List<ChatListAdapterModel>?) {
        this.list_data = list_data
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun onItemHolderClick(holder: ViewHolder) {
        onItemClickListener?.onItemClick(null, holder.itemView, holder.adapterPosition, holder.itemId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutBinding: ItemChatBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat, parent, false)
        return ViewHolder(layoutBinding, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list_data!![position]
        holder.setData(item)
    }

    override fun getItemCount(): Int {
        return list_data!!.size
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe_layout;
    }

    interface OnChatItemClickListener {
        fun onBlockChatClick(position: Int, chats: Chats)
        fun onHookChatClick(position: Int, chats: Chats)
        fun onClearChatClick(position: Int, chats: Chats)
        fun onArchiveChatClick(position: Int, chats: Chats)
    }

    inner class ViewHolder(private val layoutBinding: ItemChatBinding, private val mAdapter: ChatListAdapter) : RecyclerView.ViewHolder(layoutBinding.root), View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.txt_block -> {
                    list_data?.get(adapterPosition)?.let { listener.onBlockChatClick(adapterPosition, it.chats) }
                }
                R.id.txt_hook_unhook -> {
                    list_data?.get(adapterPosition)?.let { listener.onHookChatClick(adapterPosition, it.chats) }
                }
                R.id.txt_clear_chat -> {
                    list_data?.get(adapterPosition)?.let { listener.onClearChatClick(adapterPosition, it.chats) }
                }
                R.id.txt_archive -> {
                    list_data?.get(adapterPosition)?.let { listener.onArchiveChatClick(adapterPosition, it.chats) }
                }
                R.id.content_layout -> {
                    mAdapter.onItemHolderClick(this)
                    closeAllItems() //close if any swipe layout is open
                }
            }
        }

        fun setData(obj: ChatListAdapterModel) {
            layoutBinding.txtChatTitle.text = obj.chats.name
            layoutBinding.txtTime.text = DateTimeUtils.instance?.formatDateTime(obj.chats.current_time, DateTimeUtils.DateFormats.yyyyMMddHHmmss.label)?.let {
                DateTimeUtils.instance?.getConversationTimestamp(
                    it.time
                )
            }
            layoutBinding.txtChatMsg.text = obj.chats.chat_list?.message_text
            //load profile picture
            if (obj.chats.is_group) {
                obj.chats.group_data?.let {
                    layoutBinding.imgProfile.load(it.grp_icon ?: "", null, true, 0, false)
                }
                //show/hide block swipe layout
                layoutBinding.txtBlock.visibility = GONE
            } else {
                obj.chats.user_data?.let {
                    layoutBinding.imgProfile.load(it.profile_image ?: "", null, true, 0, false)

                }
                //show/hide block swipe layout
                layoutBinding.txtBlock.visibility = VISIBLE

                //show/hide online dot
                layoutBinding.imgOnline.visibility = if (obj.chats.is_online) VISIBLE else GONE

            }

            //show/hide hook chat divider
            if (adapterPosition == getHookCount()) {
                layoutBinding.divider.visibility = VISIBLE

            } else {
                layoutBinding.divider.visibility = GONE

            }

            //show/hide hook icon
            if (obj.chats.is_hook) {
                layoutBinding.imgHook.visibility = VISIBLE
                layoutBinding.txtHookUnhook.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, R.drawable.ic_unhook), null, null)
                layoutBinding.txtHookUnhook.text = mActivity.resources.getString(R.string.unhook)
            } else {
                layoutBinding.imgHook.visibility = GONE
                layoutBinding.txtHookUnhook.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, R.drawable.ic_hook), null, null)
                layoutBinding.txtHookUnhook.text = mActivity.resources.getString(R.string.hook)
            }

            mItemManger.bind(layoutBinding.root, adapterPosition)

        }


        init {
            layoutBinding.contentLayout.setOnClickListener(this)
            layoutBinding.txtBlock.setOnClickListener(this)
            layoutBinding.txtHookUnhook.setOnClickListener(this)
            layoutBinding.txtClearChat.setOnClickListener(this)
            layoutBinding.txtArchive.setOnClickListener(this)

        }
    }
}
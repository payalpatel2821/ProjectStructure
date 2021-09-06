package com.task.newapp.adapter.chat

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.adapter.chat.ChatListAdapter.ChatListAdapterPayloadType.*
import com.task.newapp.databinding.ItemChatBinding
import com.task.newapp.realmDB.getHookCount
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.realmDB.wrapper.ChatsWrapperModel
import com.task.newapp.utils.*
import com.task.newapp.utils.swipelayout.SwipeLayout
import com.task.newapp.utils.swipelayout.adapters.RecyclerSwipeAdapter


class ChatListAdapter(private val mActivity: Activity, private val listener: OnChatItemClickListener) : RecyclerSwipeAdapter<ChatListAdapter.ViewHolder>() {
    private val TAG = javaClass.simpleName
    private var onItemClickListener: OnItemClickListener? = null
    var listData = arrayListOf<ChatsWrapperModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun doRefresh(list_data: ArrayList<ChatsWrapperModel>) {
        this.listData = list_data
        notifyDataSetChanged()
    }

    fun setData(newMessages: ArrayList<ChatsWrapperModel>, isRefresh: Boolean) {
        //-----------------Add New-----------------------

        val diffCallback = ChatListDiffCallback(this.listData, newMessages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.listData.clear()
        this.listData.addAll(newMessages)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getData(): ArrayList<ChatsWrapperModel> {
        return listData
    }

    fun updateOnlineOfflineStatus(position: Int, isOnline: Boolean) {
        notifyItemChanged(position, ONLINE_OFFLINE)
    }

    fun updateNewMessage(position: Int, chats: ChatsWrapperModel) {
        notifyItemChanged(position, NEW_MESSAGE)
    }

    fun updateTypeIndicator(position: Int, isTyping: Boolean) {
        this.listData[position].isTyping = isTyping
        notifyItemChanged(position, TYPING)
    }

    fun addUnarchivedChat() {
        notifyItemInserted(listData.size - 1)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun onItemHolderClick(holder: ViewHolder) {
        onItemClickListener?.onItemClick(null, holder.itemView, holder.bindingAdapterPosition, holder.itemId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutBinding: ItemChatBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat, parent, false)
        return ViewHolder(layoutBinding, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.populateItemRows(item, position, null)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payload: List<Any>) {
        val item = listData[position]
        holder.populateItemRows(item, position, payload)
    }


    override fun getItemCount(): Int {
        return listData.size
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
                    listData[bindingAdapterPosition].let { listener.onBlockChatClick(bindingAdapterPosition, it.chats) }
                }
                R.id.txt_hook_unhook -> {
                    listData[bindingAdapterPosition].let { listener.onHookChatClick(bindingAdapterPosition, it.chats) }
                }
                R.id.txt_delete_chat -> {
                    listData[bindingAdapterPosition].let { listener.onClearChatClick(bindingAdapterPosition, it.chats) }
                }
                R.id.txt_archive -> {
                    listData[bindingAdapterPosition].let { listener.onArchiveChatClick(bindingAdapterPosition, it.chats) }
                }
                R.id.content_layout -> {
                    mAdapter.onItemHolderClick(this)
                }
            }
            closeAllItems() //close if any swipe layout is open
        }

        fun setData(obj: ChatsWrapperModel) {


            layoutBinding.txtChatTitle.text = obj.chats.name
            layoutBinding.txtTime.text = DateTimeUtils.instance?.formatDateTime(obj.chats.currentTime, DateTimeUtils.DateFormats.yyyyMMddHHmmss.label)?.let {
                DateTimeUtils.instance?.getConversationTimestamp(
                    it.time
                )
            }
            layoutBinding.txtChatMsg.text = obj.chats.chatList?.messageText
            //load profile picture
            if (obj.chats.isGroup) {
                obj.chats.groupData?.let {
                    layoutBinding.imgProfile.load(it.icon ?: "", false)
                }
                //show/hide block swipe layout
                layoutBinding.txtBlock.visibility = GONE
            } else {
                obj.chats.userData?.let {
                    layoutBinding.imgProfile.load(it.profileImage ?: "", true, obj.chats.name, obj.chats.userData?.profileColor)

                }
                //show/hide block swipe layout
                layoutBinding.txtBlock.visibility = VISIBLE

                //show/hide online dot
                layoutBinding.imgOnline.visibility = if (obj.chats.isOnline) VISIBLE else GONE
                getUserStatusEmitEvent(App.fastSave.getInt(Constants.prefUserId, 0), obj.chats.id)
            }

            //show/hide hook chat divider
            if (bindingAdapterPosition == getHookCount()) {
                layoutBinding.divider.visibility = VISIBLE

            } else {
                layoutBinding.divider.visibility = GONE

            }

            //show/hide hook icon
            if (obj.chats.isHook) {
                layoutBinding.imgHook.visibility = VISIBLE
                layoutBinding.txtHookUnhook.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, R.drawable.ic_unhook), null, null)
                layoutBinding.txtHookUnhook.text = mActivity.resources.getString(R.string.unhook)
            } else {
                layoutBinding.imgHook.visibility = GONE
                layoutBinding.txtHookUnhook.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, R.drawable.ic_hook), null, null)
                layoutBinding.txtHookUnhook.text = mActivity.resources.getString(R.string.hook)
            }

            //show/hide online dot
            if (obj.chats.isOnline) {
                layoutBinding.imgOnline.visibility = VISIBLE
            } else {
                layoutBinding.imgOnline.visibility = GONE
            }
            if (obj.isTyping) {
                layoutBinding.txtChatMsg.visibility = GONE
                layoutBinding.typing.visibility = VISIBLE
                layoutBinding.typing.startAnimation()
            } else {
                layoutBinding.txtChatMsg.visibility = VISIBLE
                layoutBinding.typing.visibility = GONE
                layoutBinding.typing.stopAnimation()
            }
        }

        /**
         * populate rows
         *
         * @param holder
         * @param position
         */
        fun populateItemRows(obj: ChatsWrapperModel, position: Int, listPayload: List<Any>?) {

            if (listPayload == null || listPayload.isEmpty()) {
                setData(obj)
            } else {
                showLog("PAYLOAD :", Gson().toJson(listPayload))
                for (payload in listPayload) {
                    when (payload as ChatListAdapterPayloadType) {
                        ONLINE_OFFLINE -> {
                            if (obj.chats.isOnline) {
                                layoutBinding.imgOnline.visibility = VISIBLE
                            } else {
                                layoutBinding.imgOnline.visibility = GONE
                            }
                        }
                        PROFILE_PIC_CHANGE -> {
                        }
                        NEW_MESSAGE -> {
                            layoutBinding.txtTime.text = DateTimeUtils.instance?.formatDateTime(obj.chats.currentTime, DateTimeUtils.DateFormats.yyyyMMddHHmmss.label)?.let {
                                DateTimeUtils.instance?.getConversationTimestamp(
                                    it.time
                                )
                            }
                            layoutBinding.txtChatMsg.text = obj.chats.chatList?.messageText
                        }
                        UNARCHIVE_CHAT -> {
                            setData(obj)
                        }
                        TYPING -> {
                            if (obj.isTyping) {
                                layoutBinding.txtChatMsg.visibility = GONE
                                layoutBinding.typing.visibility = VISIBLE
                                layoutBinding.typing.startAnimation()
                            } else {
                                layoutBinding.txtChatMsg.visibility = VISIBLE
                                layoutBinding.typing.visibility = GONE
                                layoutBinding.typing.stopAnimation()
                            }
                            setData(obj)
                        }
                    }
                }
            }
            layoutBinding.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, layoutBinding.swipeLayout.findViewById(R.id.ll_right_swipe))
            layoutBinding.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, layoutBinding.swipeLayout.findViewById(R.id.ll_left_swipe))
            mItemManger.bind(layoutBinding.root, bindingAdapterPosition)
        }

        init {
            layoutBinding.contentLayout.setOnClickListener(this)
            layoutBinding.txtBlock.setOnClickListener(this)
            layoutBinding.txtHookUnhook.setOnClickListener(this)
            layoutBinding.txtDeleteChat.setOnClickListener(this)
            layoutBinding.txtArchive.setOnClickListener(this)

        }
    }

    enum class ChatListAdapterPayloadType {
        ONLINE_OFFLINE,
        PROFILE_PIC_CHANGE,
        NEW_MESSAGE,
        UNARCHIVE_CHAT,
        TYPING
    }

    //-----------------------------------DiffUtil Class-----------------------------------------
    class ChatListDiffCallback(oldChatsList: List<ChatsWrapperModel>, newChatsList: List<ChatsWrapperModel>) : DiffUtil.Callback() {
        private val mOldChatsList: List<ChatsWrapperModel> = oldChatsList
        private val mNewChatsList: List<ChatsWrapperModel> = newChatsList
        override fun getOldListSize(): Int {
            return mOldChatsList.size
        }

        override fun getNewListSize(): Int {
            return mNewChatsList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldChatsList[oldItemPosition].chats == mNewChatsList[newItemPosition].chats
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldPostData: ChatsWrapperModel = mOldChatsList[oldItemPosition]
            val newPostData: ChatsWrapperModel = mNewChatsList[newItemPosition]

            return oldPostData == newPostData
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            // Implement method if you're going to use ItemAnimator
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

    }

}
package com.task.newapp.adapter.chat

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.task.newapp.R
import com.task.newapp.databinding.ItemChatTextLeftBinding
import com.task.newapp.databinding.ItemChatTextRightBinding
import com.task.newapp.realmDB.models.ChatList
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.utils.*


class OneToOneChatAdapter(private val mActivity: Activity, private val listener: OnChatItemClickListener) : RecyclerView.Adapter<OneToOneChatAdapter.RecyclerViewHolder>() {
    private val TAG = javaClass.simpleName
    private var onItemClickListener: OnItemClickListener? = null
    //private var listData: List<ChatList>? = null

    var messages = listOf<ChatList>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    enum class ChatItemViewType(val layoutResourceId: Int) {
        TYPE_TEXT_LEFT(R.layout.item_chat_text_left),
        TYPE_TEXT_RIGHT(R.layout.item_chat_text_right),
        TYPE_IMAGE_LEFT(R.layout.item_chat_text_left),
        TYPE_IMAGE_RIGHT(R.layout.item_chat_text_right),
        TYPE_AUDIO_LEFT(R.layout.item_chat_text_left),
        TYPE_AUDIO_RIGHT(R.layout.item_chat_text_right),
        TYPE_VIDEO_LEFT(R.layout.item_chat_text_left),
        TYPE_VIDEO_RIGHT(R.layout.item_chat_text_right),
        TYPE_DOCUMENT_LEFT(R.layout.item_chat_text_left),
        TYPE_DOCUMENT_RIGHT(R.layout.item_chat_text_right),
        TYPE_VOICE_LEFT(R.layout.item_chat_text_left),
        TYPE_VOICE_RIGHT(R.layout.item_chat_text_right),
        TYPE_STORY_LEFT(R.layout.item_chat_text_left),
        TYPE_STORY_RIGHT(R.layout.item_chat_text_right),
        TYPE_CONTACT_LEFT(R.layout.item_chat_text_left),
        TYPE_CONTACT_RIGHT(R.layout.item_chat_text_right),
        TYPE_DATE_LABEL(R.layout.item_chat_text_right),
        TYPE_TYPING_INDICATOR(R.layout.item_chat_text_right)
    }

    fun doRefresh(list_data: List<ChatList>) {
        this.messages = list_data
        notifyDataSetChanged()
    }

    fun updateOnlineOfflineStatus(position: Int, isOnline: Boolean) {
        notifyItemChanged(position, ChatListAdapterPayloadType.ONLINE_OFFLINE)
    }

    fun addUnarchivedChat() {
        notifyItemInserted(messages.size - 1)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun onItemHolderClick(holder: RecyclerViewHolder) {
        onItemClickListener?.onItemClick(null, holder.itemView, holder.adapterPosition, holder.itemId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        return when (viewType as ChatItemViewType) {
            ChatItemViewType.TYPE_TEXT_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(layoutBinding)
            }
            ChatItemViewType.TYPE_TEXT_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(layoutBinding)
            }
            ChatItemViewType.TYPE_IMAGE_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(layoutBinding)
            }
            ChatItemViewType.TYPE_IMAGE_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(layoutBinding)
            }
            ChatItemViewType.TYPE_AUDIO_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(layoutBinding)
            }
            ChatItemViewType.TYPE_AUDIO_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(layoutBinding)
            }
            ChatItemViewType.TYPE_VIDEO_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(layoutBinding)
            }
            ChatItemViewType.TYPE_VIDEO_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(layoutBinding)
            }
            ChatItemViewType.TYPE_DOCUMENT_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(layoutBinding)
            }
            ChatItemViewType.TYPE_DOCUMENT_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(layoutBinding)
            }
            ChatItemViewType.TYPE_VOICE_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(layoutBinding)
            }
            ChatItemViewType.TYPE_VOICE_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(layoutBinding)
            }
            ChatItemViewType.TYPE_STORY_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(layoutBinding)
            }
            ChatItemViewType.TYPE_STORY_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(layoutBinding)
            }
            ChatItemViewType.TYPE_CONTACT_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(layoutBinding)
            }
            ChatItemViewType.TYPE_CONTACT_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(layoutBinding)
            }
            ChatItemViewType.TYPE_DATE_LABEL -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(layoutBinding)
            }
            ChatItemViewType.TYPE_TYPING_INDICATOR -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(layoutBinding)
            }
        }

    }

    /*  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
          val item = listData!![position]
          holder.populateItemRows(item, position, null)
      }*/
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = messages[position]
        holder.bind(item, null)
    }
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int, payload: List<Any>) {
        val item = messages[position]
        holder.bind(item, payload)
    }


    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val chatMessage = messages[position]
        return when (Constants.Companion.MessageType.getMessageTypeFromText(chatMessage.type ?: Constants.Companion.MessageType.TEXT.type)) {
            Constants.Companion.MessageType.TYPE_INDICATOR -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            Constants.Companion.MessageType.LABEL -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            Constants.Companion.MessageType.TEXT -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            Constants.Companion.MessageType.MIX -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            Constants.Companion.MessageType.LOCATION -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            Constants.Companion.MessageType.CONTACT -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            Constants.Companion.MessageType.AUDIO -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            Constants.Companion.MessageType.VOICE -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            Constants.Companion.MessageType.DOCUMENT -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            Constants.Companion.MessageType.STORY -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            Constants.Companion.MessageType.VIDEO -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            Constants.Companion.MessageType.LINK -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            Constants.Companion.MessageType.DATE -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
        }


    }

    interface OnChatItemClickListener {
        fun onBlockChatClick(position: Int, chats: Chats)
        fun onHookChatClick(position: Int, chats: Chats)
        fun onClearChatClick(position: Int, chats: Chats)
        fun onArchiveChatClick(position: Int, chats: Chats)
    }


    /* inner class ViewHolder(private val layoutBinding: ItemChatBinding, private val mAdapter: OneToOneChatAdapter) : RecyclerView.ViewHolder(layoutBinding.root), View.OnClickListener {
         override fun onClick(v: View) {
             when (v.id) {
                  R.id.txt_block -> {
                      list_data?.get(adapterPosition)?.let { listener.onBlockChatClick(adapterPosition, it) }
                  }
                  R.id.txt_hook_unhook -> {
                      list_data?.get(adapterPosition)?.let { listener.onHookChatClick(adapterPosition, it) }
                  }
                  R.id.txt_clear_chat -> {
                      list_data?.get(adapterPosition)?.let { listener.onClearChatClick(adapterPosition, it) }
                  }
                  R.id.txt_archive -> {
                      list_data?.get(adapterPosition)?.let { listener.onArchiveChatClick(adapterPosition, it) }
                  }
                  R.id.content_layout -> {
                      mAdapter.onItemHolderClick(this)
                      closeAllItems() //close if any swipe layout is open
                  }
             }
         }

         fun setData(obj: ChatList) {
              layoutBinding.txtChatTitle.text = obj.name
              layoutBinding.txtTime.text = DateTimeUtils.instance?.formatDateTime(obj.current_time, DateTimeUtils.DateFormats.yyyyMMddHHmmss.label)?.let {
                  DateTimeUtils.instance?.getConversationTimestamp(
                      it.time
                  )
              }
              layoutBinding.txtChatMsg.text = obj.chat_list?.message_text
              //load profile picture
              if (obj.is_group) {
                  obj.group_data?.let {
                      layoutBinding.imgProfile.load(it.grp_icon ?: "")//, true, obj.name, obj.group_data?.grp_profile_color)
                  }
                  //show/hide block swipe layout
                  layoutBinding.txtBlock.visibility = GONE
              } else {
                  obj.user_data?.let {
                      layoutBinding.imgProfile.load(it.profile_image ?: "")//, true, obj.name, obj.user_data?.profile_color)

                  }
                  //show/hide block swipe layout
                  layoutBinding.txtBlock.visibility = VISIBLE

                  //show/hide online dot
                  layoutBinding.imgOnline.visibility = if (obj.is_online) VISIBLE else GONE
                  getUserStatusEmitEvent(App.fastSave.getInt(Constants.prefUserId, 0), obj.id)
              }

              //show/hide hook chat divider
              if (adapterPosition == getHookCount()) {
                  layoutBinding.divider.visibility = VISIBLE

              } else {
                  layoutBinding.divider.visibility = GONE

              }

              //show/hide hook icon
              if (obj.is_hook) {
                  layoutBinding.imgHook.visibility = VISIBLE
                  layoutBinding.txtHookUnhook.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, R.drawable.ic_unhook), null, null)
                  layoutBinding.txtHookUnhook.text = mActivity.resources.getString(R.string.unhook)
              } else {
                  layoutBinding.imgHook.visibility = GONE
                  layoutBinding.txtHookUnhook.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(mActivity, R.drawable.ic_hook), null, null)
                  layoutBinding.txtHookUnhook.text = mActivity.resources.getString(R.string.hook)
              }

              //show/hide online dot
              if (obj.is_online) {
                  layoutBinding.imgOnline.visibility = VISIBLE
              } else {
                  layoutBinding.imgOnline.visibility = GONE
              }

         }

         */
    /**
     * populate rows
     *
     * @param holder
     * @param position
     *//*
        fun populateItemRows(obj: ChatList, position: Int, listPayload: List<Any>?) {

            if (listPayload == null || listPayload.isEmpty()) {
                setData(obj)
            } else {
                showLog("PAYLOAD :", Gson().toJson(listPayload))
                for (payload in listPayload) {
                    when (payload as ChatListAdapterPayloadType) {
                        ChatListAdapterPayloadType.ONLINE_OFFLINE -> {
                        }
                        ChatListAdapterPayloadType.PROFILE_PIC_CHANGE -> TODO()
                        ChatListAdapterPayloadType.NEW_MESSAGE -> TODO()
                        ChatListAdapterPayloadType.DELETE_MESSAGE -> {
                            setData(obj)
                        }
                    }
                }
            }
        }

        init {
            layoutBinding.contentLayout.setOnClickListener(this)
            layoutBinding.txtBlock.setOnClickListener(this)
            layoutBinding.txtHookUnhook.setOnClickListener(this)
            layoutBinding.txtClearChat.setOnClickListener(this)
            layoutBinding.txtArchive.setOnClickListener(this)

        }
    }*/


    sealed class RecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(chats: ChatList, listPayload: List<Any>?)

        class TextViewHolderLeft(private val binding: ItemChatTextLeftBinding) : RecyclerViewHolder(binding) {
            override fun bind(chats: ChatList, listPayload: List<Any>?) {
                binding.txtMessage.text = chats.message_text
            }
        }

        class TextViewHolderRight(private val binding: ItemChatTextRightBinding) : RecyclerViewHolder(binding) {
            override fun bind(chats: ChatList, listPayload: List<Any>?) {
                binding.txtMessage.text = chats.message_text
            }
        }

    }

    enum class ChatListAdapterPayloadType {
        ONLINE_OFFLINE,
        PROFILE_PIC_CHANGE,
        NEW_MESSAGE,
        DELETE_MESSAGE
    }


}
package com.task.newapp.adapter.chat

import android.app.Activity
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.task.newapp.R
import com.task.newapp.databinding.ItemChatDateLabelBinding
import com.task.newapp.databinding.ItemChatTextLeftBinding
import com.task.newapp.databinding.ItemChatTextRightBinding
import com.task.newapp.databinding.ItemChatTypingIndicatorLeftBinding
import com.task.newapp.realmDB.models.ChatList
import com.task.newapp.utils.Constants
import com.task.newapp.utils.Constants.Companion.MessageStatus
import com.task.newapp.utils.Constants.Companion.MessageStatus.*
import com.task.newapp.utils.isIncoming


class OneToOneChatAdapter(private val mActivity: Activity, private val listener: OnChatItemClickListener) : RecyclerView.Adapter<OneToOneChatAdapter.RecyclerViewHolder>() {
    private val TAG = javaClass.simpleName
    private var onItemClickListener: OnItemClickListener? = null
    //private var listData: List<ChatList>? = null

    var messages = arrayListOf<ChatList>()
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
        TYPE_TYPING_INDICATOR(R.layout.item_chat_typing_indicator_left);

        companion object {
            fun getViewTypeFromId(viewTypeId: Int): ChatItemViewType {
                ChatItemViewType.values().forEach {
                    if (it.layoutResourceId == viewTypeId) {
                        return it
                    }
                }
                return TYPE_TEXT_RIGHT
            }
        }
    }

    fun doRefresh(list_data: ArrayList<ChatList>) {
        this.messages = list_data
        notifyDataSetChanged()
    }

    fun updateOnlineOfflineStatus(position: Int, isOnline: Boolean) {
        notifyItemChanged(position, ChatListAdapterChangedPayloadType.ONLINE_OFFLINE)
    }

    fun addUnarchivedChat() {
        notifyItemInserted(messages.size - 1)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun onItemHolderClick(holder: RecyclerViewHolder) {
        onItemClickListener?.onItemClick(null, holder.itemView, holder.bindingAdapterPosition, holder.itemId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        return when (ChatItemViewType.getViewTypeFromId(viewType)) {
            ChatItemViewType.TYPE_TEXT_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_TEXT_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_IMAGE_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_IMAGE_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_AUDIO_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_AUDIO_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_VIDEO_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_VIDEO_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_DOCUMENT_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_DOCUMENT_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_VOICE_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_VOICE_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_STORY_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_STORY_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_CONTACT_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_CONTACT_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding)
            }
            ChatItemViewType.TYPE_DATE_LABEL -> {
                val layoutBinding: ItemChatDateLabelBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_date_label, parent, false)
                RecyclerViewHolder.DateViewHolder(layoutBinding)
            }
            ChatItemViewType.TYPE_TYPING_INDICATOR -> {
                val layoutBinding: ItemChatTypingIndicatorLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_typing_indicator_left, parent, false)
                RecyclerViewHolder.TypingIndicatorViewHolder(layoutBinding)
            }
        }

    }

    fun setData(newMessages: ArrayList<ChatList>, isRefresh: Boolean) {
        //-----------------Add New-----------------------

        val diffCallback = ChatDiffCallback(this.messages, newMessages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.messages.clear()
        this.messages.addAll(newMessages)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getData(): ArrayList<ChatList> {
        return messages
    }

    fun getChatItemFromLocalId(localChatId: Long): ChatList? {
        messages.forEachIndexed { index, chatList ->
            if (chatList.localChatId == localChatId) {
                return chatList
            }
        }
        return null
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
            Constants.Companion.MessageType.TYPE_INDICATOR -> ChatItemViewType.TYPE_TYPING_INDICATOR.layoutResourceId
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
            Constants.Companion.MessageType.DATE -> if (isIncoming(chatMessage)) ChatItemViewType.TYPE_DATE_LABEL.layoutResourceId else ChatItemViewType.TYPE_DATE_LABEL.layoutResourceId
        }


    }

    interface OnChatItemClickListener {
        //fun onBlockChatClick(position: Int, chats: Chats)
        //fun onHookChatClick(position: Int, chats: Chats)
        //fun onClearChatClick(position: Int, chats: Chats)
        //fun onArchiveChatClick(position: Int, chats: Chats)
    }


    sealed class RecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(chats: ChatList, listPayload: List<Any>?)

        class TextViewHolderLeft(private val mActivity: Activity, private val binding: ItemChatTextLeftBinding) : RecyclerViewHolder(binding) {
            override fun bind(chats: ChatList, listPayload: List<Any>?) {
                binding.txtMessage.text = chats.messageText
            }
        }

        class TextViewHolderRight(private val mActivity: Activity, private val binding: ItemChatTextRightBinding) : RecyclerViewHolder(binding) {
            override fun bind(chats: ChatList, listPayload: List<Any>?) {
                binding.txtMessage.text = chats.messageText
                val messageStatus = MessageStatus.getMessageStatusFromId(chats.tick)
                binding.txtMessage.setTextColor(ContextCompat.getColor(mActivity, getChatTextColor(messageStatus)));
                updateChatBubbleBackground(mActivity, getChatBubbleColor(messageStatus), binding.llContent.background, binding.llContent)

            }
        }

        class DateViewHolder(private val binding: ItemChatDateLabelBinding) : RecyclerViewHolder(binding) {
            override fun bind(chats: ChatList, listPayload: List<Any>?) {

            }

        }

        class TypingIndicatorViewHolder(private val binding: ItemChatTypingIndicatorLeftBinding) : RecyclerViewHolder(binding) {
            override fun bind(chats: ChatList, listPayload: List<Any>?) {
                binding.typing.startAnimation()
            }

        }

        fun updateChatBubbleBackground(mActivity: Activity, color: Int, drawable: Drawable, view: LinearLayout) {
            val bgDrawable: LayerDrawable = drawable as LayerDrawable /*drawable*/
            bgDrawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(mActivity, color), android.graphics.PorterDuff.Mode.SRC)
            // val bg_layer: GradientDrawable = bgDrawable.findDrawableByLayerId(R.id.chat_bg) as GradientDrawable /*findDrawableByLayerId*/
            // bg_layer.setColor(color) /*set color to layer*/
            view.background = bgDrawable /*set drawable to image*/


        }

        fun getChatBubbleColor(messageStatus: MessageStatus): Int {
            return when (messageStatus) {
                SENT -> {
                    R.color.chat_sent_message_bubble_color
                }
                DELIVERED -> {
                    R.color.chat_delivered_message_bubble_color
                }
                READ -> {
                    R.color.chat_seen_message_bubble_color
                }
            }
        }

        fun getChatTextColor(messageStatus: MessageStatus): Int {
            return when (messageStatus) {
                SENT -> {
                    R.color.semi_black_transparent
                }
                DELIVERED -> {
                    R.color.black
                }
                READ -> {
                    R.color.black
                }
            }
        }
    }

    enum class ChatListAdapterChangedPayloadType {
        ONLINE_OFFLINE,
        PROFILE_PIC_CHANGE,
        NEW_MESSAGE,
        DELETE_MESSAGE
    }

    //-----------------------------------DiffUtil Class-----------------------------------------
    class ChatDiffCallback(oldMessagesList: List<ChatList>, newMessagesList: List<ChatList>) : DiffUtil.Callback() {
        private val mOldMessagesList: List<ChatList> = oldMessagesList
        private val mNewMessagesList: List<ChatList> = newMessagesList
        override fun getOldListSize(): Int {
            return mOldMessagesList.size
        }

        override fun getNewListSize(): Int {
            return mNewMessagesList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldMessagesList[oldItemPosition] == mNewMessagesList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldPostData: ChatList = mOldMessagesList[oldItemPosition]
            val newPostData: ChatList = mNewMessagesList[newItemPosition]

            return oldPostData == newPostData
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            // Implement method if you're going to use ItemAnimator
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

    }


}
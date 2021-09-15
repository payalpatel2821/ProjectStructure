package com.task.newapp.adapter.chat

import android.app.Activity
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.gson.Gson
import com.task.newapp.R
import com.task.newapp.adapter.chat.OneToOneChatAdapter.ChatItemViewType
import com.task.newapp.adapter.chat.OneToOneChatAdapter.OneToOneChatAdapterChangedPayloadType.CHAT_ITEM_SELECTION
import com.task.newapp.databinding.ItemChatAudioLeftBinding
import com.task.newapp.databinding.ItemChatAudioRightBinding
import com.task.newapp.databinding.ItemChatDateLabelBinding
import com.task.newapp.databinding.ItemChatImageLeftBinding
import com.task.newapp.databinding.ItemChatImageRightBinding
import com.task.newapp.databinding.ItemChatTextLeftBinding
import com.task.newapp.databinding.ItemChatTextRightBinding
import com.task.newapp.databinding.ItemChatTypingIndicatorLeftBinding
import com.task.newapp.databinding.ItemChatVoiceLeftBinding
import com.task.newapp.databinding.ItemChatVoiceRightBinding
import com.task.newapp.realmDB.wrapper.ChatListWrapperModel
import com.task.newapp.utils.Constants.Companion.ChatContentType
import com.task.newapp.utils.Constants.Companion.ChatContentType.AUDIO
import com.task.newapp.utils.Constants.Companion.ChatContentType.CONTACT
import com.task.newapp.utils.Constants.Companion.ChatContentType.DOCUMENT
import com.task.newapp.utils.Constants.Companion.ChatContentType.IMAGE
import com.task.newapp.utils.Constants.Companion.ChatContentType.LOCATION
import com.task.newapp.utils.Constants.Companion.ChatContentType.VIDEO
import com.task.newapp.utils.Constants.Companion.ChatContentType.VOICE
import com.task.newapp.utils.Constants.Companion.MessageStatus
import com.task.newapp.utils.Constants.Companion.MessageStatus.DELIVERED
import com.task.newapp.utils.Constants.Companion.MessageStatus.READ
import com.task.newapp.utils.Constants.Companion.MessageStatus.SENT
import com.task.newapp.utils.Constants.Companion.MessageType
import com.task.newapp.utils.DateTimeUtils
import com.task.newapp.utils.isIncoming
import com.task.newapp.utils.load
import com.task.newapp.utils.showLog


class OneToOneChatAdapter(private val mActivity: Activity, private val listener: OnChatItemClickListener) : RecyclerView.Adapter<OneToOneChatAdapter.RecyclerViewHolder>() {
    private val TAG = javaClass.simpleName
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null
    private var isMultiSelectionEnable = false
    //private var listData: List<ChatList>? = null

    var messages = arrayListOf<ChatListWrapperModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    enum class ChatItemViewType(val layoutResourceId: Int) {
        TYPE_TEXT_LEFT(R.layout.item_chat_text_left),
        TYPE_TEXT_RIGHT(R.layout.item_chat_text_right),
        TYPE_IMAGE_LEFT(R.layout.item_chat_image_left),
        TYPE_IMAGE_RIGHT(R.layout.item_chat_image_right),
        TYPE_AUDIO_LEFT(R.layout.item_chat_audio_left),
        TYPE_AUDIO_RIGHT(R.layout.item_chat_audio_right),
        TYPE_VIDEO_LEFT(R.layout.item_chat_text_left),
        TYPE_VIDEO_RIGHT(R.layout.item_chat_text_right),
        TYPE_DOCUMENT_LEFT(R.layout.item_chat_text_left),
        TYPE_DOCUMENT_RIGHT(R.layout.item_chat_text_right),
        TYPE_VOICE_LEFT(R.layout.item_chat_voice_left),
        TYPE_VOICE_RIGHT(R.layout.item_chat_voice_right),
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

    fun doRefresh(list_data: ArrayList<ChatListWrapperModel>) {
        this.messages = list_data
        notifyDataSetChanged()
    }

    fun getSelectedMessageCount(): Int {
        return messages.map { it.isSelect }.filter { isSelect -> isSelect }.count()
    }

    fun getIncomingSelectedMessageCount(): Int {
        return messages.filter { chatMessage -> chatMessage.isSelect && isIncoming(chatMessage.chatList) }.count()
    }

    fun deselectAllMessages() {
        messages.forEachIndexed { index, chatListWrapperModel ->
            chatListWrapperModel.isSelect = false
            //  notifyItemChanged(index)
        }
        isMultiSelectionEnable = false
        notifyItemRangeChanged(0, messages.size)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener

    }

    fun onItemHolderClick(holder: RecyclerViewHolder) {
        onItemClickListener?.onItemClick(null, holder.itemView, holder.bindingAdapterPosition, holder.itemId)
    }

    fun onItemHolderLongClick(holder: RecyclerViewHolder) {
        onItemLongClickListener?.onItemLongClick(null, holder.itemView, holder.bindingAdapterPosition, holder.itemId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        return when (ChatItemViewType.getViewTypeFromId(viewType)) {
            ChatItemViewType.TYPE_TEXT_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_TEXT_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_IMAGE_LEFT -> {
                val layoutBinding: ItemChatImageLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_image_left, parent, false)
                RecyclerViewHolder.ImageViewHolderLeft(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_IMAGE_RIGHT -> {
                val layoutBinding: ItemChatImageRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_image_right, parent, false)
                RecyclerViewHolder.ImageViewHolderRight(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_AUDIO_LEFT -> {
                val layoutBinding: ItemChatAudioLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_audio_left, parent, false)
                RecyclerViewHolder.AudioViewHolderLeft(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_AUDIO_RIGHT -> {
                val layoutBinding: ItemChatAudioRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_audio_right, parent, false)
                RecyclerViewHolder.AudioViewHolderRight(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_VIDEO_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_VIDEO_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_DOCUMENT_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_DOCUMENT_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_VOICE_LEFT -> {
                val layoutBinding: ItemChatVoiceLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_voice_left, parent, false)
                RecyclerViewHolder.VoiceViewHolderLeft(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_VOICE_RIGHT -> {
                val layoutBinding: ItemChatVoiceRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_voice_right, parent, false)
                RecyclerViewHolder.VoiceViewHolderRight(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_STORY_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_STORY_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_CONTACT_LEFT -> {
                val layoutBinding: ItemChatTextLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_left, parent, false)
                RecyclerViewHolder.TextViewHolderLeft(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_CONTACT_RIGHT -> {
                val layoutBinding: ItemChatTextRightBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_text_right, parent, false)
                RecyclerViewHolder.TextViewHolderRight(mActivity, layoutBinding, this)
            }
            ChatItemViewType.TYPE_DATE_LABEL -> {
                val layoutBinding: ItemChatDateLabelBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_date_label, parent, false)
                RecyclerViewHolder.DateViewHolder(layoutBinding, this)
            }
            ChatItemViewType.TYPE_TYPING_INDICATOR -> {
                val layoutBinding: ItemChatTypingIndicatorLeftBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_chat_typing_indicator_left, parent, false)
                RecyclerViewHolder.TypingIndicatorViewHolder(layoutBinding, this)
            }
        }

    }

    fun setData(newMessages: ArrayList<ChatListWrapperModel>, isRefresh: Boolean) {
        //-----------------Add New-----------------------

        val diffCallback = ChatDiffCallback(this.messages, newMessages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.messages.clear()
        this.messages.addAll(newMessages)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getData(): ArrayList<ChatListWrapperModel> {
        return messages
    }

    fun getChatItemFromLocalId(localChatId: Long): ChatListWrapperModel? {
        messages.forEachIndexed { index, chatList ->
            if (chatList.chatList.localChatId == localChatId) {
                return chatList
            }
        }
        return null
    }

    fun isChatTypingIndicatorAdded(): Boolean {
        messages.forEachIndexed { index, chatList ->
            if (chatList.chatList.localChatId == (-1).toLong()) {
                return true
            }
        }
        return false
    }


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
        return when (MessageType.getMessageTypeFromText(chatMessage.chatList.type ?: MessageType.TEXT.type)) {
            MessageType.TYPE_INDICATOR -> ChatItemViewType.TYPE_TYPING_INDICATOR.layoutResourceId
            MessageType.LABEL -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            MessageType.TEXT -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            MessageType.MIX -> {

                when (ChatContentType.getChatContentTypeFromText(chatMessage.chatList.chatContents?.type ?: "")) {
                    IMAGE -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_IMAGE_LEFT.layoutResourceId else ChatItemViewType.TYPE_IMAGE_RIGHT.layoutResourceId
                    VIDEO -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
                    AUDIO -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_AUDIO_LEFT.layoutResourceId else ChatItemViewType.TYPE_AUDIO_RIGHT.layoutResourceId
                    VOICE -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_VOICE_LEFT.layoutResourceId else ChatItemViewType.TYPE_VOICE_RIGHT.layoutResourceId
                    CONTACT -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
                    DOCUMENT -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
                    LOCATION -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
                    else -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId

                }
            }
            MessageType.STORY -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            MessageType.LINK -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
            MessageType.DATE -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_DATE_LABEL.layoutResourceId else ChatItemViewType.TYPE_DATE_LABEL.layoutResourceId
            else -> if (isIncoming(chatMessage.chatList)) ChatItemViewType.TYPE_TEXT_LEFT.layoutResourceId else ChatItemViewType.TYPE_TEXT_RIGHT.layoutResourceId
        }


    }

    interface OnChatItemClickListener {
        fun onIncomingMessageItemClick(isIncoming: Boolean, position: Int, chats: ChatListWrapperModel)
    }


    sealed class RecyclerViewHolder(private val binding: ViewBinding, private val mAdapter: OneToOneChatAdapter) : RecyclerView.ViewHolder(binding.root), OnClickListener, OnLongClickListener {
        abstract fun bind(chats: ChatListWrapperModel, listPayload: List<Any>?)
        override fun onClick(v: View?) {
            if (mAdapter.isMultiSelectionEnable) {
                mAdapter.messages[bindingAdapterPosition].isSelect = !mAdapter.messages[bindingAdapterPosition].isSelect
                mAdapter.onItemHolderClick(this)
                mAdapter.listener.onIncomingMessageItemClick(isIncoming(mAdapter.messages[bindingAdapterPosition].chatList), bindingAdapterPosition, mAdapter.messages[bindingAdapterPosition])
                refreshChatItemSelection()
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (!mAdapter.isMultiSelectionEnable)
                mAdapter.isMultiSelectionEnable = true
            if (mAdapter.isMultiSelectionEnable) {
                mAdapter.messages[bindingAdapterPosition].isSelect = !mAdapter.messages[bindingAdapterPosition].isSelect
                mAdapter.onItemHolderLongClick(this)
                mAdapter.listener.onIncomingMessageItemClick(isIncoming(mAdapter.messages[bindingAdapterPosition].chatList), bindingAdapterPosition, mAdapter.messages[bindingAdapterPosition])
                refreshChatItemSelection()
            }
            return true
        }

        class TextViewHolderLeft(private val mActivity: Activity, private val binding: ItemChatTextLeftBinding, private val mAdapter: OneToOneChatAdapter) : RecyclerViewHolder(binding, mAdapter) {
            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun bind(chats: ChatListWrapperModel, listPayload: List<Any>?) {

                if (listPayload == null || listPayload.isEmpty()) {
                    setData(chats)
                } else {
                    showLog("PAYLOAD :", Gson().toJson(listPayload))
                    for (payload in listPayload) {
                        when (payload as OneToOneChatAdapterChangedPayloadType) {
                            CHAT_ITEM_SELECTION -> refreshChatItemSelection()
                        }
                    }
                }
            }

            fun setData(chats: ChatListWrapperModel) {
                binding.txtMessage.text = chats.chatList.messageText
                refreshChatItemSelection()

            }

        }

        class TextViewHolderRight(private val mActivity: Activity, private val binding: ItemChatTextRightBinding, private val mAdapter: OneToOneChatAdapter) : RecyclerViewHolder(binding, mAdapter) {
            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun bind(chats: ChatListWrapperModel, listPayload: List<Any>?) {
                if (listPayload == null || listPayload.isEmpty()) {
                    setData(chats)
                } else {
                    showLog("PAYLOAD :", Gson().toJson(listPayload))
                    for (payload in listPayload) {
                        when (payload as OneToOneChatAdapterChangedPayloadType) {
                            CHAT_ITEM_SELECTION -> refreshChatItemSelection()
                        }
                    }
                }

            }

            fun setData(chats: ChatListWrapperModel) {
                refreshChatItemSelection()
                binding.txtMessage.text = chats.chatList.messageText
                val messageStatus = MessageStatus.getMessageStatusFromId(chats.chatList.tick)
                binding.txtMessage.setTextColor(ContextCompat.getColor(mActivity, getChatTextColor(messageStatus)));
                updateChatBubbleBackground(mActivity, getChatBubbleColor(messageStatus), binding.llContent.background, binding.llContent)
            }

        }

        class DateViewHolder(private val binding: ItemChatDateLabelBinding, private val mAdapter: OneToOneChatAdapter) : RecyclerViewHolder(binding, mAdapter) {
            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun bind(chats: ChatListWrapperModel, listPayload: List<Any>?) {

            }

        }

        class TypingIndicatorViewHolder(private val binding: ItemChatTypingIndicatorLeftBinding, private val mAdapter: OneToOneChatAdapter) : RecyclerViewHolder(binding, mAdapter) {
            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun bind(chats: ChatListWrapperModel, listPayload: List<Any>?) {
                binding.typing.startAnimation()
            }


        }

        class AudioViewHolderLeft(private val mActivity: Activity, private val binding: ItemChatAudioLeftBinding, private val mAdapter: OneToOneChatAdapter) : RecyclerViewHolder(binding, mAdapter) {
            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun bind(chats: ChatListWrapperModel, listPayload: List<Any>?) {
                if (listPayload == null || listPayload.isEmpty()) {
                    setData(chats)
                } else {
                    showLog("PAYLOAD :", Gson().toJson(listPayload))
                    for (payload in listPayload) {
                        when (payload as OneToOneChatAdapterChangedPayloadType) {
                            CHAT_ITEM_SELECTION -> refreshChatItemSelection()
                        }
                    }
                }

            }

            fun setData(chats: ChatListWrapperModel) {
                refreshChatItemSelection()
                chats.chatList.chatContents?.let {
                    binding.txtTitle.text = it.title
                    binding.txtDuration.text = DateTimeUtils.instance?.formatDurationFromSeconds(it.duration)
                }
            }

        }

        class AudioViewHolderRight(private val mActivity: Activity, private val binding: ItemChatAudioRightBinding, private val mAdapter: OneToOneChatAdapter) : RecyclerViewHolder(binding, mAdapter) {
            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun bind(chats: ChatListWrapperModel, listPayload: List<Any>?) {
                if (listPayload == null || listPayload.isEmpty()) {
                    setData(chats)
                } else {
                    showLog("PAYLOAD :", Gson().toJson(listPayload))
                    for (payload in listPayload) {
                        when (payload as OneToOneChatAdapterChangedPayloadType) {
                            CHAT_ITEM_SELECTION -> refreshChatItemSelection()
                        }
                    }
                }
            }

            fun setData(chats: ChatListWrapperModel) {
                refreshChatItemSelection()
                val messageStatus = MessageStatus.getMessageStatusFromId(chats.chatList.tick)
                updateChatBubbleBackground(mActivity, getChatBubbleColor(messageStatus), binding.llContent.background, binding.llContent)
                chats.chatList.chatContents?.let {
                    binding.txtTitle.text = it.title
                    binding.txtDuration.text = DateTimeUtils.instance?.formatDurationFromSeconds(it.duration)

                }
            }

        }

        class VoiceViewHolderLeft(private val mActivity: Activity, private val binding: ItemChatVoiceLeftBinding, private val mAdapter: OneToOneChatAdapter) : RecyclerViewHolder(binding, mAdapter) {
            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun bind(chats: ChatListWrapperModel, listPayload: List<Any>?) {
                if (listPayload == null || listPayload.isEmpty()) {
                    setData(chats)
                } else {
                    showLog("PAYLOAD :", Gson().toJson(listPayload))
                    for (payload in listPayload) {
                        when (payload as OneToOneChatAdapterChangedPayloadType) {
                            CHAT_ITEM_SELECTION -> refreshChatItemSelection()
                        }
                    }
                }

            }

            fun setData(chats: ChatListWrapperModel) {
                refreshChatItemSelection()
                chats.chatList.chatContents?.let {
                    // binding.txtTitle.text = it.title
                    binding.txtDuration.text = DateTimeUtils.instance?.formatDurationFromSeconds(it.duration)

                }
            }
        }

        class VoiceViewHolderRight(private val mActivity: Activity, private val binding: ItemChatVoiceRightBinding, private val mAdapter: OneToOneChatAdapter) : RecyclerViewHolder(binding, mAdapter) {
            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun bind(chats: ChatListWrapperModel, listPayload: List<Any>?) {
                if (listPayload == null || listPayload.isEmpty()) {
                    setData(chats)
                } else {
                    showLog("PAYLOAD :", Gson().toJson(listPayload))
                    for (payload in listPayload) {
                        when (payload as OneToOneChatAdapterChangedPayloadType) {
                            CHAT_ITEM_SELECTION -> refreshChatItemSelection()
                        }
                    }
                }
            }

            fun setData(chats: ChatListWrapperModel) {
                refreshChatItemSelection()
                val messageStatus = MessageStatus.getMessageStatusFromId(chats.chatList.tick)
                updateChatBubbleBackground(mActivity, getChatBubbleColor(messageStatus), binding.llContent.background, binding.llContent)
                chats.chatList.chatContents?.let {
                    //binding.txtTitle.text = it.title
                    binding.txtDuration.text = DateTimeUtils.instance?.formatDurationFromSeconds(it.duration)

                }
            }
        }

        class ImageViewHolderLeft(private val mActivity: Activity, private val binding: ItemChatImageLeftBinding, private val mAdapter: OneToOneChatAdapter) : RecyclerViewHolder(binding, mAdapter) {
            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun bind(chats: ChatListWrapperModel, listPayload: List<Any>?) {

                if (listPayload == null || listPayload.isEmpty()) {
                    setData(chats)
                } else {
                    showLog("PAYLOAD :", Gson().toJson(listPayload))
                    for (payload in listPayload) {
                        when (payload as OneToOneChatAdapterChangedPayloadType) {
                            CHAT_ITEM_SELECTION -> refreshChatItemSelection()
                        }
                    }
                }
            }

            fun setData(chats: ChatListWrapperModel) {
                binding.ivImage.load(chats.chatList.chatContents?.content)
                refreshChatItemSelection()

            }

        }

        class ImageViewHolderRight(private val mActivity: Activity, private val binding: ItemChatImageRightBinding, private val mAdapter: OneToOneChatAdapter) : RecyclerViewHolder(binding, mAdapter) {
            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun bind(chats: ChatListWrapperModel, listPayload: List<Any>?) {

                if (listPayload == null || listPayload.isEmpty()) {
                    setData(chats)
                } else {
                    showLog("PAYLOAD :", Gson().toJson(listPayload))
                    for (payload in listPayload) {
                        when (payload as OneToOneChatAdapterChangedPayloadType) {
                            CHAT_ITEM_SELECTION -> refreshChatItemSelection()
                        }
                    }
                }
            }

            fun setData(chats: ChatListWrapperModel) {
                binding.ivImage.load(chats.chatList.chatContents?.content)
                refreshChatItemSelection()

            }

        }

        fun refreshChatItemSelection() {
            if (mAdapter.messages[bindingAdapterPosition].isSelect) {
                this.itemView.setBackgroundColor(ContextCompat.getColor(mAdapter.mActivity, R.color.chat_message_selection_bg_color))
                this.itemView.alpha = 0.7f
            } else {
                this.itemView.setBackgroundColor(ContextCompat.getColor(mAdapter.mActivity, R.color.transparent))
                this.itemView.alpha = 1f
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

    enum class OneToOneChatAdapterChangedPayloadType {
        CHAT_ITEM_SELECTION
    }

    //-----------------------------------DiffUtil Class-----------------------------------------
    class ChatDiffCallback(oldMessagesList: List<ChatListWrapperModel>, newMessagesList: List<ChatListWrapperModel>) : DiffUtil.Callback() {
        private val mOldMessagesList: List<ChatListWrapperModel> = oldMessagesList
        private val mNewMessagesList: List<ChatListWrapperModel> = newMessagesList
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
            val oldPostData: ChatListWrapperModel = mOldMessagesList[oldItemPosition]
            val newPostData: ChatListWrapperModel = mNewMessagesList[newItemPosition]

            return oldPostData == newPostData
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            // Implement method if you're going to use ItemAnimator
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

    }


}
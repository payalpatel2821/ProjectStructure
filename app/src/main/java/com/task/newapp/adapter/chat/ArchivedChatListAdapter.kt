package com.task.newapp.adapter.chat

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.*
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.task.newapp.R
import com.task.newapp.databinding.ItemArchiveChatBinding
import com.task.newapp.realmDB.models.BroadcastTable
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.utils.DateTimeUtils
import com.task.newapp.utils.load
import com.task.newapp.utils.showLog
import com.task.newapp.utils.swipelayout.adapters.RecyclerSwipeAdapter


class ArchivedChatListAdapter(private val mActivity: Activity, private val listener: OnChatItemClickListener) : RecyclerSwipeAdapter<ArchivedChatListAdapter.ViewHolder>(), Filterable {
    private val TAG = javaClass.simpleName
    private var onItemClickListener: OnItemClickListener? = null
    private var listData: ArrayList<Chats> = ArrayList()
    private var filteredListData: ArrayList<Chats> = ArrayList()


    fun doRefresh(data: List<Chats>) {
        listData.addAll(data)
        filteredListData.addAll(data)
        notifyDataSetChanged()
    }

    fun updateOnlineOfflineStatus(position: Int, isOnline: Boolean) {
        notifyItemChanged(position, ChatListAdapterPayloadType.ONLINE_OFFLINE)
    }

    fun addUnarchivedChat() {
        notifyItemInserted(filteredListData.size - 1)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun onItemHolderClick(holder: ViewHolder) {
        onItemClickListener?.onItemClick(null, holder.itemView, holder.adapterPosition, holder.itemId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutBinding: ItemArchiveChatBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_archive_chat, parent, false)
        return ViewHolder(layoutBinding, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredListData[position]
        /*holder.setData(item)*/
        holder.populateItemRows(item, position, null)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payload: List<Any>) {
        val item = filteredListData[position]
        /*holder.setData(item)*/
        holder.populateItemRows(item, position, payload)
    }


    override fun getItemCount(): Int {
        return filteredListData.size
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe_layout;
    }

    interface OnChatItemClickListener {

        fun onClearChatClick(position: Int, chats: Chats)
        fun onUnarchiveChatClick(position: Int, chats: Chats)
    }

    inner class ViewHolder(private val layoutBinding: ItemArchiveChatBinding, private val mAdapter: ArchivedChatListAdapter) : RecyclerView.ViewHolder(layoutBinding.root), View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.txt_clear_chat -> {
                    filteredListData[adapterPosition].let { listener.onClearChatClick(adapterPosition, it) }
                }
                R.id.txt_unarchive -> {
                    filteredListData[adapterPosition].let {
                        listener.onUnarchiveChatClick(adapterPosition, it) }
                }
                R.id.content_layout -> {
                    mAdapter.onItemHolderClick(this)
                    closeAllItems() //close if any swipe layout is open
                }
            }
        }

        fun setData(obj: Chats) {
            layoutBinding.txtChatTitle.text = obj.name
            layoutBinding.txtTime.text = DateTimeUtils.instance?.formatDateTime(obj.current_time, DateTimeUtils.DateFormats.yyyyMMddTHHmmsssss.label)?.let {
                DateTimeUtils.instance?.getConversationTimestamp(
                    it.time
                )
            }
            layoutBinding.txtChatMsg.text = obj.chat_list?.message_text
            //load profile picture
            if (obj.is_group) {
                obj.group_data?.let {
                    layoutBinding.imgProfile.load(it.grp_icon ?: "")
                }

            } else {
                obj.user_data?.let {
                    layoutBinding.imgProfile.load(it.profile_image ?: "")

                }
                //show/hide online dot
                layoutBinding.imgOnline.visibility = if (obj.is_online) VISIBLE else GONE

            }

        }


        /**
         * populate rows
         *
         * @param holder
         * @param position
         */
        fun populateItemRows(obj: Chats, position: Int, listPayload: List<Any>?) {


            if (listPayload == null || listPayload.isEmpty()) {
                setData(obj)
            } else {
                showLog("PAYLOAD :", Gson().toJson(listPayload))
                for (payload in listPayload) {
                    when (payload as ChatListAdapterPayloadType) {
                        ChatListAdapterPayloadType.ONLINE_OFFLINE -> {
                            if (obj.is_online) {
                                layoutBinding.imgOnline.visibility = VISIBLE
                            } else {
                                layoutBinding.imgOnline.visibility = GONE
                            }
                        }
                        ChatListAdapterPayloadType.PROFILE_PIC_CHANGE -> TODO()
                        ChatListAdapterPayloadType.NEW_MESSAGE -> TODO()
                        ChatListAdapterPayloadType.UNARCHIVE_CHAT -> {
                            setData(obj)
                        }
                    }
                }
            }

            mItemManger.bind(layoutBinding.root, adapterPosition)
        }

        init {
            layoutBinding.contentLayout.setOnClickListener(this)
            layoutBinding.txtClearChat.setOnClickListener(this)
            layoutBinding.txtUnarchive.setOnClickListener(this)

        }
    }

    enum class ChatListAdapterPayloadType {
        ONLINE_OFFLINE,
        PROFILE_PIC_CHANGE,
        NEW_MESSAGE,
        UNARCHIVE_CHAT
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                filteredListData = if (charString.isEmpty()) {
                    listData
                } else {
                    val filteredList: MutableList<Chats> = ArrayList()
                    for (row in listData) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.name?.lowercase()?.contains(charString.lowercase()) == true) {
                            filteredList.add(row)
                        }
                    }
                    filteredList as ArrayList<Chats>
                }
                val filterResults = FilterResults()
                filterResults.values = filteredListData
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredListData = filterResults.values as ArrayList<Chats>
                notifyDataSetChanged()
            }
        }
    }
}
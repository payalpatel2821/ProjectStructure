package com.task.newapp.adapter.chat.broadcast

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.task.newapp.R
import com.task.newapp.databinding.ItemBroadcastListBinding
import com.task.newapp.realmDB.models.BroadcastTable
import com.task.newapp.utils.DateTimeUtils
import com.task.newapp.utils.load
import com.task.newapp.utils.showLog
import com.task.newapp.utils.swipelayout.adapters.RecyclerSwipeAdapter


class BroadcastChatListAdapter(private val mActivity: Activity, private val listener: OnChatItemClickListener) : RecyclerSwipeAdapter<BroadcastChatListAdapter.ViewHolder>() {
    private val TAG = javaClass.simpleName
    private var onItemClickListener: OnItemClickListener? = null
    private var listData: List<BroadcastTable>? = null

    fun doRefresh(list_data: List<BroadcastTable>?) {
        this.listData = list_data
        notifyDataSetChanged()
    }


    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun onItemHolderClick(holder: ViewHolder) {
        onItemClickListener?.onItemClick(null, holder.itemView, holder.adapterPosition, holder.itemId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutBinding: ItemBroadcastListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_broadcast_list, parent, false)
        return ViewHolder(layoutBinding, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData!![position]
        holder.populateItemRows(item, position, null)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payload: List<Any>) {
        val item = listData!![position]
        holder.populateItemRows(item, position, payload)
    }


    override fun getItemCount(): Int {
        return listData!!.size
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe_layout;
    }


    interface OnChatItemClickListener {
        fun onClearChatClick(position: Int, broadcastChat: BroadcastTable)
        fun onDeleteBroadcastChatClick(position: Int, broadcastChat: BroadcastTable)
    }

    inner class ViewHolder(private val layoutBinding: ItemBroadcastListBinding, private val mAdapter: BroadcastChatListAdapter) : RecyclerView.ViewHolder(layoutBinding.root), View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.txt_clear_chat -> {
                    listData?.get(adapterPosition)?.let { listener.onClearChatClick(adapterPosition, it) }
                }
                R.id.txt_delete_broadcast -> {
                    mItemManger.closeAllItems()
                    listData?.get(adapterPosition)?.let { listener.onDeleteBroadcastChatClick(adapterPosition, it) }
                }
                R.id.content_layout -> {
                    mAdapter.onItemHolderClick(this)

                }
            }
        }

        fun setData(obj: BroadcastTable) {
            layoutBinding.txtChatTitle.text = obj.broad_name
            layoutBinding.txtTime.text = DateTimeUtils.instance?.formatDateTime(obj.updated_at, DateTimeUtils.DateFormats.yyyyMMddTHHmmsssss.label)?.let {
                DateTimeUtils.instance?.getConversationTimestamp(
                    it.time
                )
            }
            layoutBinding.txtChatMsg.text = obj.chats.last()?.message_text
            layoutBinding.imgProfile.load(obj.broad_icon ?: "", false)
        }

        /**
         * populate rows
         *
         * @param holder
         * @param position
         */
        fun populateItemRows(obj: BroadcastTable, position: Int, listPayload: List<Any>?) {


            if (listPayload == null || listPayload.isEmpty()) {
                setData(obj)
            } else {
                showLog("PAYLOAD :", Gson().toJson(listPayload))
                /* for (payload in listPayload) {

                 }*/
            }
            mItemManger.bind(layoutBinding.root, adapterPosition)

        }

        init {
            layoutBinding.contentLayout.setOnClickListener(this)
            layoutBinding.txtClearChat.setOnClickListener(this)
            layoutBinding.txtDeleteBroadcast.setOnClickListener(this)

        }
    }
}
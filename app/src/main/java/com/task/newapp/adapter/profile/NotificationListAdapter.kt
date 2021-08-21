package com.task.newapp.adapter.profile

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.databinding.ItemNotificationDialogBinding
import com.task.newapp.models.NotificationToneWrapper

class NotificationListAdapter(
    private val applicationContext: Context,
    private val dataSet: List<NotificationToneWrapper>
) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    private var onItemClickListener: AdapterView.OnItemClickListener? = null
    //var onItemClick: ((ArrayList<ResponsePageList.Data>, Int) -> Unit)? = null

    class ViewHolder(private val layoutBinding: ItemNotificationDialogBinding, private val mAdapter: NotificationListAdapter) : RecyclerView.ViewHolder(layoutBinding.root), View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.rb_notification -> {
                    mAdapter.onItemHolderClick(this)
                }

            }

        }

        fun setData(applicationContext: Context, obj: NotificationToneWrapper) {
            layoutBinding.rbNotification.text = obj.notificationTone.soundName
            if (obj.isChecked) {
                layoutBinding.ivIsCheck.visibility = VISIBLE
                layoutBinding.rbNotification.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme_color))
            }else{
                layoutBinding.ivIsCheck.visibility = GONE
                layoutBinding.rbNotification.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            }
        }
        init {
            layoutBinding.rbNotification.setOnClickListener(this)


        }
    }

    fun setOnItemClickListener(onItemClickListener: AdapterView.OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun onItemHolderClick(holder: ViewHolder) {
        onItemClickListener?.onItemClick(null, holder.itemView, holder.adapterPosition, holder.itemId)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutBinding: ItemNotificationDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context), R.layout.item_notification_dialog, viewGroup, false)
        return ViewHolder(layoutBinding, this)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val obj = dataSet[position]
        viewHolder.setData(applicationContext, obj)

    }

    override fun getItemCount() = dataSet.size

    fun selectSingleItem(position: Int) {
        dataSet.forEachIndexed { index, notificationToneWrapper ->
            notificationToneWrapper.isChecked = index == position

        }
        notifyDataSetChanged()
    }

    fun getCheckedItem(): Int {
        dataSet.forEach {
            if (it.isChecked) {
                return dataSet.indexOf(it)
            }
        }
        return 0

    }
}

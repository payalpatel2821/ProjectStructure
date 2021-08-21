package com.task.newapp.adapter.post

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.task.newapp.R
import com.task.newapp.databinding.ItemSelectFriendBinding
import com.task.newapp.models.chat.SelectFriendWrapperModel
import com.task.newapp.utils.load
import com.task.newapp.utils.showLog
import java.util.*
import kotlin.collections.ArrayList


class SelectFriendsListAdapter(private val mActivity: Activity) : RecyclerView.Adapter<SelectFriendsListAdapter.ViewHolder>(), Filterable {
    private val TAG = javaClass.simpleName
    private var listData: ArrayList<SelectFriendWrapperModel> = ArrayList()
    private var filteredListData: ArrayList<SelectFriendWrapperModel> = ArrayList()
    private var onItemClickListener: AdapterView.OnItemClickListener? = null
    fun setOnItemClickListener(onItemClickListener: AdapterView.OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun onItemHolderClick(holder: SelectFriendsListAdapter.ViewHolder) {
        onItemClickListener?.onItemClick(null, holder.itemView, holder.adapterPosition, holder.itemId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectFriendsListAdapter.ViewHolder {
        val layoutBinding: ItemSelectFriendBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_select_friend, parent, false
        )
        return ViewHolder(layoutBinding, this)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredListData[position]
        holder.populateItemRows(item, position, null)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    fun getItem(position: Int): SelectFriendWrapperModel {
        return filteredListData[position]
    }

    fun getItemPosition(data: SelectFriendWrapperModel): Int {
        return filteredListData.indexOf(data)
    }

    override fun getItemCount(): Int = filteredListData.size

    fun doRefresh(data: List<SelectFriendWrapperModel>) {
        listData.addAll(data)
        filteredListData.addAll(data)
        notifyDataSetChanged()
    }

    fun updateCheckUncheck(isChecked: Boolean, position: Int) {
        this.filteredListData[position].isChecked = isChecked
        notifyItemChanged(position, "updateCheckUncheck")
    }

    inner class ViewHolder(private val layoutBinding: ItemSelectFriendBinding, private val mAdapter: SelectFriendsListAdapter) : RecyclerView.ViewHolder(layoutBinding.root), View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.content_layout -> {
                    /*listData[adapterPosition].isChecked = !listData[adapterPosition].isChecked
                    layoutBinding.imgCheck.isSelected = !layoutBinding.imgCheck.isSelected
                    */
                    mAdapter.onItemHolderClick(this)

                }
            }
        }

        fun setData(obj: SelectFriendWrapperModel) {
            layoutBinding.txtName.text = obj.firstName + " " + obj.lastName
            layoutBinding.txtUserName.text = obj.userName
            layoutBinding.imgProfile.load(obj.profileImage ?: "", true, obj.firstName + " " + obj.lastName, obj.profileColor)
            layoutBinding.imgCheck.isSelected = obj.isChecked

        }

        /**
         * populate rows
         *
         * @param holder
         * @param position
         */
        fun populateItemRows(obj: SelectFriendWrapperModel, position: Int, listPayload: List<Any>?) {


            if (listPayload == null || listPayload.isEmpty()) {
                setData(obj)
            } else {
                showLog("PAYLOAD :", Gson().toJson(listPayload))
                for (payload in listPayload) {
                    when (payload) {
                        "updateCheckUncheck" -> {
                            layoutBinding.imgCheck.isSelected = obj.isChecked
                        }
                    }
                }
            }
        }

        init {
            layoutBinding.contentLayout.setOnClickListener(this)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filteredListData = listData
                } else {
                    val filteredList: MutableList<SelectFriendWrapperModel> = ArrayList()
                    for (row in listData) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.firstName.lowercase().contains(charString.lowercase()) || row.lastName.lowercase().contains(charString.lowercase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredListData = filteredList as ArrayList<SelectFriendWrapperModel>
                }
                val filterResults = FilterResults()
                filterResults.values = filteredListData
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredListData = filterResults.values as ArrayList<SelectFriendWrapperModel>
                notifyDataSetChanged()
            }
        }
    }


}

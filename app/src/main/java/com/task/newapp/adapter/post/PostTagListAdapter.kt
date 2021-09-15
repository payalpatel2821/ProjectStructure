package com.task.newapp.adapter.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.databinding.ItemTaglistPostBinding
import com.task.newapp.models.OtherUserModel
import com.task.newapp.models.post.ResponseGetAllPost
import com.task.newapp.utils.load
import com.task.newapp.utils.showLog


class PostTagListAdapter(
    context: FragmentActivity,
    arrayListPattern: ArrayList<OtherUserModel>

) : RecyclerView.Adapter<PostTagListAdapter.ViewHolder>() {
    var context: FragmentActivity = context as FragmentActivity
    var arrayList: ArrayList<OtherUserModel> = arrayListPattern as ArrayList<OtherUserModel>
    var onItemClick: ((String, String) -> Unit)? = null

    var commaSeperatedIds: String = ""
    var commaSeperatedNames: String = ""
    var arrayListTemp: ArrayList<OtherUserModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostTagListAdapter.ViewHolder {
        val layoutBinding: ItemTaglistPostBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_taglist_post, parent, false
        )
        return ViewHolder(layoutBinding)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val otherUserModel: OtherUserModel = arrayList[position]
        viewHolder.populateItemRows(otherUserModel)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int = arrayList.size

    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    inner class ViewHolder(val layoutBinding: ItemTaglistPostBinding) : RecyclerView.ViewHolder(layoutBinding.root) {

        fun populateItemRows(otherUserModel: OtherUserModel) {
            layoutBinding.txtName.text = (otherUserModel.firstName ?: "") + " " + (otherUserModel.lastName ?: "")

//            otherUserModel.profileImage?.let {
            layoutBinding.imgProfile.load(otherUserModel.profileImage, true, layoutBinding.txtName.text.trim().toString(), otherUserModel.profileColor)
//            }

            if (arrayListTemp.contains(otherUserModel)) {
                layoutBinding.imgCheck.isSelected = true
            } else {
                layoutBinding.imgCheck.isSelected = otherUserModel.isSelected
            }

            //Add New
            if (otherUserModel.isSelected) {  // if not added in array then only add
                if (!arrayListTemp.contains(otherUserModel)) {
                    arrayListTemp.add(otherUserModel)
                }
            } else if (arrayListTemp.contains(otherUserModel)) {
//                arrayListTemp.remove(otherUserModel)

                val findedModel = arrayListTemp?.find {
                    it.id == otherUserModel.id
                }
                findedModel?.let {
                    val indexOfTemp = arrayListTemp?.indexOf(findedModel)
                    arrayListTemp.removeAt(indexOfTemp)
                }

            }

            itemView.setOnClickListener {
                otherUserModel.isSelected = !otherUserModel.isSelected
                layoutBinding.imgCheck.isSelected = !layoutBinding.imgCheck.isSelected

                //Add New
                if (otherUserModel.isSelected) {
                    if (!arrayListTemp.contains(otherUserModel)) {
                        arrayListTemp.add(otherUserModel)
                    }
                } else {
//                    arrayListTemp.remove(otherUserModel)

                    val findedModel = arrayListTemp?.find {
                        it.id == otherUserModel.id
                    }
                    findedModel?.let {
                        val indexOfTemp = arrayListTemp?.indexOf(findedModel)
                        arrayListTemp.removeAt(indexOfTemp)
                    }

                }
                setSelectedName()
            }
        }
    }

    fun setData(data: ArrayList<OtherUserModel>, isRefresh: Boolean) {

        if (isRefresh) {
            arrayList.clear()
            arrayList = ArrayList()
        }
        arrayList.addAll(data)
        notifyDataSetChanged()
        showLog("all_post", this.arrayList.size.toString())
    }

    fun getData(): Int {
        return arrayList.size
    }

    fun setSelectedName() {
        try {
            commaSeperatedNames = arrayListTemp.filter(OtherUserModel::isSelected)
                .joinToString(separator = ", ") { if (it.isSelected) (it.firstName ?: "") + " " + (it.lastName ?: "") else "" }

            commaSeperatedIds = arrayListTemp.filter(OtherUserModel::isSelected)
                .joinToString(separator = ",") { if (it.isSelected) it.id.toString() else "" }

            onItemClick?.invoke(commaSeperatedNames, commaSeperatedIds)

            showLog("commaSeparatedString", "$commaSeperatedNames---$commaSeperatedIds")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
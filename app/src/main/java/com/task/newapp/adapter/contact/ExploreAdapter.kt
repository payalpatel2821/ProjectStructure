package com.task.newapp.adapter.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.databinding.ItemExploreContactNumberBinding
import com.task.newapp.models.ResponseIsAppUser

class ExploreAdapter(
    private val mainActivity: FragmentActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var allUser: ArrayList<ResponseIsAppUser.Data> = ArrayList<ResponseIsAppUser.Data>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: ItemExploreContactNumberBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_explore_contact_number, parent, false)
        return ExploreViewHolder(layoutBinding)


       // return ExploreViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return allUser.size
    }

    fun setData(arrayList: ArrayList<ResponseIsAppUser.Data>) {
        allUser.addAll(arrayList)
        notifyDataSetChanged()
    }

    class ExploreViewHolder(private val layoutBinding: ItemExploreContactNumberBinding) :  RecyclerView.ViewHolder(layoutBinding.root) {
//        var textViewLetter: TextView =
//            itemView.findViewById<View>(R.id.textview_country) as TextView

        fun bindTo(letter: String) {
//            textViewLetter.text = letter
        }
    }
}
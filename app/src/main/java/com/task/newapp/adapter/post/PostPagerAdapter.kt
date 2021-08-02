package com.task.newapp.adapter.post

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.task.newapp.R
import com.task.newapp.models.post.Post_Uri_Model
import java.util.*

class PostPagerAdapter(context: Context, images: ArrayList<Post_Uri_Model>) : PagerAdapter() {

    var mContext: Context? = null
    var arrayList = images
    private var mLayoutInflater: LayoutInflater? = null

    init {
        this.mContext = context
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = mLayoutInflater!!.inflate(R.layout.item_post_pager, container, false)
        val imageView: ImageView = itemView.findViewById<View>(R.id.imageViewMain) as ImageView
        val imgPlay: ImageView = itemView.findViewById<View>(R.id.imgPlay) as ImageView

        Glide.with(mContext!!).load(arrayList[position].file_path).into(imageView)

        if (arrayList[position].type == "image") imgPlay.visibility = View.GONE
        else imgPlay.visibility = View.VISIBLE

        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}
package com.task.newapp.adapter.post

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.task.newapp.R

class ThoughtColorPatternAdapter(context: Activity, colorList: IntArray, colorFontList: IntArray, drawableList: TypedArray, type: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var context: Context = context as Context
    var colorList: IntArray = colorList as IntArray
    var colorFontList: IntArray = colorFontList as IntArray
    var drawableList: TypedArray = drawableList as TypedArray
    var type: Int = type as Int
    var onItemClick: ((Int, Int, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_thought, parent, false)
        return StatusHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        populateItemRows(viewHolder as StatusHolder, position)
    }

    private fun populateItemRows(holder: StatusHolder, position: Int) {
        when (type) {
            0 -> {
                val color: Int = colorList[position]
                holder.imgColorPattern.setBackgroundColor(color)

                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(color, position, 0)
                }
            }
            1 -> {
                val pattern: Int = drawableList.getResourceId(position, 0)
                Glide.with(context).load(pattern).into(holder.imgColorPattern)

                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(pattern, position, 1)
                }
            }
            2 -> {
                val color: Int = colorFontList[position]
                holder.imgColorPattern.setBackgroundColor(color)

                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(color, position, 2)
                }
            }
        }
    }

    override fun getItemCount(): Int = if (type == 0) colorList.size else if (type == 1) drawableList.length() else colorFontList.size

    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    inner class StatusHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgColorPattern: ImageView = itemView.findViewById(R.id.imgColorPattern)
    }


}
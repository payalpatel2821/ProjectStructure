package com.task.newapp.utils.photoeditor.tools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.R
import com.task.newapp.ui.activities.post.EditImageActivity
import java.util.*

//import how.messenger.thepublicmedia.R;
/**
 * @author [Burhanuddin Rashid](https://github.com/burhanrashid52)
 * @version 0.1.2
 * @since 5/23/2018
 */
internal class EditingToolsAdapter(
    private val mOnItemSelected: OnItemSelected,
    var editImageActivity: EditImageActivity
) : RecyclerView.Adapter<EditingToolsAdapter.ViewHolder>() {

    private val mToolList: MutableList<ToolModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_editing_tools, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mToolList[position]
        holder.txtTool.text = item.mToolName
        holder.imgToolIcon.setImageResource(item.mToolIcon)

//        if (position == 1) {
//            if (((EditImageActivity) editImageActivity).mPhotoEditor.addedViews.size() == 0) {
//                holder.itemView.setVisibility(View.GONE);
//            } else {
//                holder.itemView.setVisibility(View.VISIBLE);
//            }
//        } else if (position == 2) {
//            if (((EditImageActivity) editImageActivity).mPhotoEditor.redoViews.size() == 0) {
//                holder.itemView.setVisibility(View.GONE);
//            } else {
//                holder.itemView.setVisibility(View.VISIBLE);
//            }
//        }
    }

    override fun getItemCount(): Int {
        return mToolList.size
    }

    interface OnItemSelected {
        fun onToolSelected(toolType: ToolType?)
    }

    internal inner class ToolModel(
        val mToolName: String,
        val mToolIcon: Int,
        val mToolType: ToolType
    )

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgToolIcon: ImageView = itemView.findViewById(R.id.imgToolIcon)
        var txtTool: TextView = itemView.findViewById(R.id.txtTool)

        init {
            itemView.setOnClickListener(View.OnClickListener {
                mOnItemSelected.onToolSelected(
                    mToolList[layoutPosition].mToolType
                )
            })
        }
    }

    init {
        //        mToolList.add(new ToolModel("cancle", R.drawable.ic_close, ToolType.CLOSE));
//        mToolList.add(new ToolModel("undo", R.drawable.ic_undo, ToolType.UNDO));
//        mToolList.add(new ToolModel("redo", R.drawable.ic_redo, ToolType.REDO));
        mToolList.add(ToolModel("Brush", R.drawable.edit_pencil, ToolType.BRUSH))
        mToolList.add(ToolModel("Eraser", R.drawable.edit_ereser, ToolType.ERASER))
        mToolList.add(ToolModel("Text", R.drawable.edit_text, ToolType.TEXT))
//        mToolList.add(ToolModel("Filter", R.drawable.edit_filter, ToolType.FILTER))
//        mToolList.add(ToolModel("Crop", R.drawable.edit_crop1, ToolType.CROP))
        mToolList.add(ToolModel("Sticker", R.drawable.edit_sticker, ToolType.STICKER))
    }
}
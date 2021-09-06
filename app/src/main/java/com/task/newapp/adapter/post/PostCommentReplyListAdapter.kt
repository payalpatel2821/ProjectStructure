package com.task.newapp.adapter.post

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.databinding.ItemCommentReplyBinding
import com.task.newapp.models.post.ResponseGetAllPostComments.AllPostCommentData
import com.task.newapp.ui.fragments.post.PostFragment
import com.task.newapp.utils.Constants
import com.task.newapp.utils.OnCommentItemClickListener
import com.task.newapp.utils.load
import com.task.newapp.utils.parseDate
import com.task.newapp.utils.swipelayout.SwipeLayout
import com.task.newapp.utils.swipelayout.adapters.RecyclerSwipeAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PostCommentReplyListAdapter(
    context: FragmentActivity,
    arrayListPattern: ArrayList<AllPostCommentData.CommentReply>,
    post_by_me: Int,
    adapterPosition: Int

) : RecyclerSwipeAdapter<PostCommentReplyListAdapter.ViewHolder>() {

    var context: FragmentActivity = context as FragmentActivity
    var arrayList: ArrayList<AllPostCommentData.CommentReply> = arrayListPattern as ArrayList<AllPostCommentData.CommentReply>
    var onItemClick: ((String, String) -> Unit)? = null
    private var onCommentItemClickListener: OnCommentItemClickListener? = null
    var checkedPosition = -1
    var post_by_me: Int = post_by_me
    var positionMain: Int = adapterPosition
    var comment_id = 0
    private val isCaching = true

    fun setOnItemClickListener(onItemClickListener: OnCommentItemClickListener) {
        this.onCommentItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCommentReplyListAdapter.ViewHolder {
        val layoutBinding: ItemCommentReplyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_comment_reply, parent, false
        )
        return ViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        with(viewHolder) {
            with(arrayList[position]) {
                layoutBinding.textReply.text = comment_text
                layoutBinding.usernm.text = (user!!.first_name ?: "") + " " + (user!!.last_name ?: "")

                //---------------------------Set profile image----------------------------------
                val profileImg: String = user!!.profile_image + "?q=" + System.currentTimeMillis()
//                Glide.with(context)
//                    .load(profileImg)
//                    .placeholder(R.drawable.logo)
//                    .skipMemoryCache(!isCaching)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .into(layoutBinding.imgView)

                layoutBinding.imgView.load(profileImg, true, layoutBinding.usernm.text.toString(), user.profile_color)

                //---------------------------Set time----------------------------------
                val updatedAt = updated_at.replace("T", " ").replace(".000000Z", "")

                val c = Calendar.getInstance()
                val df = SimpleDateFormat("yyyy-MM-dd")
                val formattedDate = df.format(c.time)
                val date: ArrayList<String> = updatedAt.split(" ") as ArrayList<String>
                Log.println(Log.ASSERT, "formattedDate--", date[0])
                val date_split = date[0].split("-").toTypedArray()
                val time = date[1].split(":").toTypedArray()

                val HHmmFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                val hhmmampmFormat = SimpleDateFormat("hh:mm a", Locale.US)
                val msg_time: String? = parseDate(updatedAt, HHmmFormat, hhmmampmFormat)
                if (formattedDate == date[0]) {
                    layoutBinding.blockDate.text = msg_time
                } else {
                    layoutBinding.blockDate.text = date_split[2] + "/" + date_split[1] + "/" + date_split[0]
                }
            }

            layoutBinding.deleteReply.setOnClickListener {
                //Delete Reply
                onCommentItemClickListener!!.onDeleteCommentReplyClick(0, position)
            }

            //--------------------------swipeLayout-------------------------------
            if (checkedPosition == -1) {

//                Add New
                layoutBinding.edittxt.setText("")

                layoutBinding.cvReplyBox.visibility = View.GONE
                PostFragment.instance.isreplybox = false
            } else {
                if (checkedPosition == adapterPosition) {

                    //Add New
//                    layoutBinding.edittxt.setText("")

                    layoutBinding.cvReplyBox.visibility = View.VISIBLE
                    PostFragment.instance.isreplybox = true
                } else {

                    //Add New
                    layoutBinding.edittxt.setText("")

                    layoutBinding.cvReplyBox.visibility = View.GONE
                    PostFragment.instance.isreplybox = false
                }
            }

            if (post_by_me == 0) {

                if (App.fastSave.getInt(Constants.prefUserId, 0) == arrayList[position].user!!.id) {
                    layoutBinding.swipeLayoutReply.isSwipeEnabled = true
                    layoutBinding.txtDelete.visibility = View.VISIBLE

                } else {
                    layoutBinding.swipeLayoutReply.isSwipeEnabled = false
                    layoutBinding.txtDelete.visibility = View.GONE
                }

            } else {
                //----------------------My Post------------------------

//                layoutBinding.deleteReply.visibility = View.VISIBLE
                layoutBinding.swipeLayoutReply.showMode = SwipeLayout.ShowMode.PullOut

                layoutBinding.swipeLayoutReply.addDrag(
                    SwipeLayout.DragEdge.Right,
                    layoutBinding.swipeLayoutReply.findViewById(R.id.bottom_wrapperReply)
                )
                layoutBinding.swipeLayoutReply.addDrag(
                    SwipeLayout.DragEdge.Left,
                    layoutBinding.swipeLayoutReply.findViewById(R.id.bottom_wraperDelete)
                )

                layoutBinding.swipeLayoutReply.isLeftSwipeEnabled = true
                layoutBinding.swipeLayoutReply.isRightSwipeEnabled = true

//                layoutBinding.swipeLayoutReply.isLeftSwipeEnabled = allPostCommentData.commentReply == null
                layoutBinding.swipeLayoutReply.surfaceView.setOnClickListener {
                    //comment_id = allPostCommentData.id
                }
//                layoutBinding.sendReply.setOnClickListener {
//                    sendReply(
//                        layoutBinding.edittxt.text.toString().trim(),
//                        allPostCommentData.postId,
//                        layoutBinding.textReply,
//                        adapterPosition,
//                        layoutBinding.replyBox,
//                        layoutBinding.edittxt
//                    )
//                }
            }

            //                layoutBinding.imgReplySwipe.setOnClickListener {
            layoutBinding.txtReply.setOnClickListener {
                if (layoutBinding.cvReplyBox.visibility == View.VISIBLE) {
                    layoutBinding.cvReplyBox.visibility = View.GONE
                    PostFragment.instance.isreplybox = false
                    layoutBinding.swipeLayoutReply.close(true)
                } else {
                    layoutBinding.cvReplyBox.visibility = View.VISIBLE
                    PostFragment.instance.isreplybox = true
                    layoutBinding.swipeLayoutReply.close(true)
                    layoutBinding.edittxt.isFocusableInTouchMode = true
                    layoutBinding.edittxt.requestFocus()
                    if (checkedPosition != adapterPosition) {
                        notifyItemChanged(checkedPosition)
                        checkedPosition = adapterPosition
                        //                            ((Show_Post) context).rec_comments.setFocusable(true);
//                            ((Show_Post) context).rec_comments.focusableViewAvailable(v);
                    }
                }
            }
            layoutBinding.edittxt.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (layoutBinding.edittxt.hasFocus()) {
                        v.parent.requestDisallowInterceptTouchEvent(true)
                        when (event.action and MotionEvent.ACTION_MASK) {
                            MotionEvent.ACTION_SCROLL -> {
                                v.parent.requestDisallowInterceptTouchEvent(false)
                                return true
                            }
                        }
                    }
                    return false
                }
            })
            layoutBinding.cancel.setOnClickListener {
                layoutBinding.cvReplyBox.visibility = View.GONE
                PostFragment.instance.isreplybox = false
                layoutBinding.edittxt.setText("")
            }

            mItemManger.bind(layoutBinding.root, adapterPosition)
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int = arrayList.size

    inner class ViewHolder(val layoutBinding: ItemCommentReplyBinding) : RecyclerView.ViewHolder(layoutBinding.root), View.OnClickListener {
        init {
            layoutBinding.imgDeleteSwipe.setOnClickListener(this)
            layoutBinding.sendReply.setOnClickListener(this)
            layoutBinding.txtDelete.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            when (v.id) {
//                R.id.imgDeleteSwipe -> {
//                    layoutBinding.swipeLayoutReply.close(true)
//                    onCommentItemClickListener!!.onDeleteCommentReplyClick(positionMain, adapterPosition)
//                }
                R.id.txtDelete -> {
                    layoutBinding.swipeLayoutReply.close(true)
                    onCommentItemClickListener!!.onDeleteCommentReplyClick(positionMain, adapterPosition)
                }

//                R.id.delete_reply -> {
//                    layoutBinding.swipeLayout.close(true)
//                    onCommentItemClickListener!!.onDeleteCommentReplyClick(adapterPosition)
//                }
                R.id.sendReply -> {
                    comment_id = arrayList[adapterPosition].id


                    onCommentItemClickListener!!.onCommentReplyClick(
                        positionMain,
                        adapterPosition,
                        comment_id,
                        layoutBinding.edittxt.text.toString().trim(),
                        arrayList[adapterPosition].main_comment_id
                    )
                }
            }
        }
    }

    fun getData(): Int {
        return arrayList.size
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayoutReply
    }

    fun setData(allPostCommentDataList: ArrayList<AllPostCommentData.CommentReply>, post_by_me: Int) {
//        this.allPostCommentDataList = allPostCommentDataList
        this.post_by_me = post_by_me
//        notifyDataSetChanged()

        //-----------------Add New-----------------------
        val diffCallback = CommentDiffCallback(this.arrayList, allPostCommentDataList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.arrayList.clear()
        this.arrayList.addAll(allPostCommentDataList)
//        notifyDataSetChanged()

        diffResult.dispatchUpdatesTo(this)
    }

    //-----------------------------------DiffUtil Class-----------------------------------------
    class CommentDiffCallback(
        oldCommentList: List<AllPostCommentData.CommentReply>,
        newCommentList: List<AllPostCommentData.CommentReply>
    ) : DiffUtil.Callback() {
        private val mOldCommentList: List<AllPostCommentData.CommentReply> = oldCommentList
        private val mNewCommentList: List<AllPostCommentData.CommentReply> = newCommentList
        override fun getOldListSize(): Int {
            return mOldCommentList.size
        }

        override fun getNewListSize(): Int {
            return mNewCommentList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldCommentList[oldItemPosition].id === mNewCommentList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldPostData: AllPostCommentData.CommentReply = mOldCommentList[oldItemPosition]
            val newPostData: AllPostCommentData.CommentReply = mNewCommentList[newItemPosition]

            return oldPostData == newPostData
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            // Implement method if you're going to use ItemAnimator
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

    }
}
package com.task.newapp.adapter.post

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.models.post.ResponsePostCommentDetails.*
import com.task.newapp.ui.fragments.post.PostFragment
import com.task.newapp.utils.Constants
import com.task.newapp.utils.SeeMoreTextView
import com.task.newapp.utils.parseDate
import com.task.newapp.utils.swipelayout.SwipeLayout
import com.task.newapp.utils.swipelayout.adapters.RecyclerSwipeAdapter
import java.text.SimpleDateFormat
import java.util.*

class Post_Comment_Adapter(context: Activity, post_id: Int, dataList: ArrayList<Data>, post_by_me: Int) :
    RecyclerSwipeAdapter<RecyclerView.ViewHolder>() {

    val VIEW_TYPE_ITEM = 0
    val VIEW_TYPE_LOADING = 1
    var context: Activity
    var dataList: ArrayList<Data>
    var post_by_me: Int
    var comment_id = 0
    var post_id: Int
    private val isCaching = true
    private var checkedPosition = -1

    init {
        this.context = context
        this.dataList = dataList
        this.post_by_me = post_by_me
        this.post_id = post_id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val row: View = LayoutInflater.from(parent.context).inflate(R.layout.post_comment_adpter, parent, false)
            StatusHolder(row)
        } else {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //ProgressBar would be displayed
    }

    fun setdata(dataList: ArrayList<Data>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    fun adddata(all_post_comment: Data) {
        dataList!!.add(all_post_comment)
        notifyItemInserted(dataList!!.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList!![position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StatusHolder) {
            populateItemRows(holder as StatusHolder, position)
        } else if (holder is LoadingViewHolder) {
            showLoadingView(holder as LoadingViewHolder, position)
        }
    }

    private fun populateItemRows(holder: StatusHolder, position: Int) {
        val profile_img: String = dataList!![position].user.profileImage + "?q=" + System.currentTimeMillis()
        val userName: String = dataList!![position].user.firstName + " " + dataList!![position].user.lastName
//        setImageToImageView(context, if (profile_img.contains("default.png")) "" else profile_img, userName, isCaching, holder.img_view)
        Glide.with(context)
            .load(profile_img)
            .placeholder(R.drawable.default_dp)
            .skipMemoryCache(!isCaching)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.img_view)

        holder.username.text = userName
        holder.comment.setContent(dataList!![position].commentText)
        if (checkedPosition == -1) {
            holder.reply_box.visibility = View.GONE
            PostFragment.instance.isreplybox = false
        } else {
            if (checkedPosition == position) {
                holder.reply_box.visibility = View.VISIBLE
                PostFragment.instance.isreplybox = true
            } else {
                holder.reply_box.setVisibility(View.GONE)
                PostFragment.instance.isreplybox = false
            }
        }
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = df.format(c.time)
        val date: List<String> = dataList!![position].updatedAt.split(" ")
        Log.println(Log.ASSERT, "formattedDate--", date[0])
        val date_split = date[0].split("-".toRegex()).toTypedArray()
        val time = date[1].split(":".toRegex()).toTypedArray()
        val HHmmFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val hhmmampmFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val msg_time: String? = parseDate(dataList!![position].updatedAt, HHmmFormat, hhmmampmFormat)
        if (formattedDate == date[0]) {
            holder.block_date.text = msg_time
        } else {
            holder.block_date.text = date_split[2] + "/" + date_split[1] + "/" + date_split[0]
        }
        if (dataList!![position].commentReply != null) {
            holder.show_reply_box.visibility = View.VISIBLE
            holder.text.setContent(dataList!![position].commentReply.commentText)
            holder.usernm.text = dataList!![position].commentReply.user.firstName + " " + dataList!![position].commentReply.user.lastName
        } else {
            holder.show_reply_box.visibility = View.GONE
            holder.text.setContent("")
        }
        if (post_by_me == 0) {
            if (App.fastSave.getInt(Constants.prefUserId, 0) == dataList!![position].user.id) {
                holder.swipeLayout.isSwipeEnabled = true
                holder.delete_reply.visibility = View.VISIBLE
            } else {
                holder.swipeLayout.isSwipeEnabled = false
                holder.delete_reply.visibility = View.GONE
            }
        } else {
            holder.delete_reply.visibility = View.VISIBLE
            holder.swipeLayout.showMode = SwipeLayout.ShowMode.PullOut
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.swipeLayout.findViewById(R.id.bottom_wrapper1))
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wraper))
            holder.swipeLayout.isLeftSwipeEnabled = dataList!![position].commentReply == null
            holder.swipeLayout.surfaceView.setOnClickListener {
                comment_id = dataList!![position].id
                //                    Toast.makeText(context, " Click : " + dataList.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
            holder.reply.setOnClickListener {
                if (holder.reply_box.visibility == View.VISIBLE) {
                    holder.reply_box.visibility = View.GONE
                    PostFragment.instance.isreplybox = false
                    holder.swipeLayout.close(true)
                } else {
                    PostFragment.instance.isreplybox = true
                    holder.reply_box.visibility = View.VISIBLE
                    holder.swipeLayout.close(true)
                    holder.edittxt.isFocusableInTouchMode = true
                    holder.edittxt.requestFocus()
                    if (checkedPosition != position) {
                        notifyItemChanged(checkedPosition)
                        checkedPosition = position
                        //                            ((Show_Post) context).rec_comments.setFocusable(true);
//                            ((Show_Post) context).rec_comments.focusableViewAvailable(v);
                    }
                }
            }
            holder.edittxt.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (holder.edittxt.hasFocus()) {
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
            holder.cancel.setOnClickListener {
                holder.reply_box.visibility = View.GONE
                PostFragment.instance.isreplybox = false
                holder.edittxt.setText("")
            }
            holder.send.setOnClickListener {
                comment_id = dataList[position].id
                addreply(holder.edittxt.text.toString().trim { it <= ' ' }, dataList!![position].postId, holder.text, position, holder.reply_box, holder.edittxt)
            }
        }
//        holder.delete_reply.setOnClickListener {
//            holder.swipeLayout.close(true)
//            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                .setContentText("Do you want to delete this reply ?")
//                .setConfirmText("Confirm")
//                .setCancelButtonBackgroundColor(context.getResources().getColor(R.color.colorPrimary))
//                .setConfirmButtonBackgroundColor(context.getResources().getColor(R.color.colorPrimary))
//                .setConfirmClickListener(object : OnSweetClickListener() {
//                    fun onClick(sDialog: SweetAlertDialog) {
//                        sDialog.dismissWithAnimation()
//                        if (isNetworkConnected(context)) {
//                            if (position < dataList!!.size) {
//                                comment_id = dataList!![position].getComment_reply().getId()
//                                deletecomment(position, "reply")
//                            }
//                        } else {
//                            Toast.makeText(context, R.string.check_connection, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                })
//                .setCancelButton("Cancel", object : OnSweetClickListener() {
//                    fun onClick(sDialog: SweetAlertDialog) {
//                        sDialog.dismissWithAnimation()
//                    }
//                })
//                .show()
//        }
//        holder.Delete.setOnClickListener(View.OnClickListener {
//            holder.swipeLayout.close(true)
//            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                .setContentText("Do you want to delete this comment ?")
//                .setConfirmText("Confirm")
//                .setCancelButtonBackgroundColor(context.getResources().getColor(R.color.colorPrimary))
//                .setConfirmButtonBackgroundColor(context.getResources().getColor(R.color.colorPrimary))
//                .setConfirmClickListener(object : OnSweetClickListener() {
//                    fun onClick(sDialog: SweetAlertDialog) {
//                        sDialog.dismissWithAnimation()
//                        if (isNetworkConnected(context)) {
//                            if (position < dataList!!.size) {
//                                comment_id = dataList!![position].getId()
//                                deletecomment(position, "main")
//                            }
//                        } else {
//                            Toast.makeText(context, R.string.check_connection, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                })
//                .setCancelButton("Cancel", object : OnSweetClickListener() {
//                    fun onClick(sDialog: SweetAlertDialog) {
//                        sDialog.dismissWithAnimation()
//                    }
//                })
//                .show()
//        })
        mItemManger.bind(holder.itemView, position)
    }

    fun addreply(replytext: String?, postid: Int, replytxt: SeeMoreTextView, position: Int, reply_box: RelativeLayout, editText: EditText) {
//        val dialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
//        dialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.colorPrimary))
//        //        dialog.setTitleText("Fetching Data");
//        dialog.setCancelable(false)
//        dialog.show()
//        val comment: RequestBody = create.create(parse.parse("text/plain"), "comment")
//        val commenttext: RequestBody = create.create(parse.parse("text/plain"), replytext)
//        val call1: Call<Add_Post_Comment_Response> = apiInterface.add_postcomment(comment_id, 1, comment, commenttext, postid, App.prefs.getString(AppConstants.login_token, ""))
//        call1.enqueue(object : Callback<Add_Post_Comment_Response> {
//            override fun onResponse(call: Call<Add_Post_Comment_Response>, response: Response<Add_Post_Comment_Response>) {
//                dialog.dismiss()
//                if (App.getAppInstance().isUnAuthorized(context, response.code())) {
//                    return
//                }
//                if (response.isSuccessful()) {
//                    if (response.body().getSuccess() === 1) {
//                        val postSocket = PostSocket(
//                            prefs.getInt(AppConstants.user_id, 0),
//                            post_id
//                        )
//                        val json: String = Gson().toJson(postSocket)
//                        socket.emit(SocketConstant.post_comment, json)
//                        replytxt.setContent(replytext)
//                        editText.setText("")
//                        reply_box.setVisibility(View.GONE)
//                        checkedPosition = -1
//                        val comment_reply: All_Post_Comment.Comment_Reply = Comment_Reply()
//                        comment_reply.setId(response.body().getData().getId())
//                        comment_reply.setComment_text(response.body().getData().getComment_text())
//                        comment_reply.setUpdated_at(response.body().getData().getUpdated_at())
//                        val user_detail = User_Detail()
//                        user_detail.setFirst_name(response.body().getData().getFirst_name())
//                        user_detail.setLast_name(response.body().getData().getLast_name())
//                        comment_reply.setUser(user_detail)
//                        dataList!![position].setComment_reply(comment_reply)
//                        comments.setText(response.body().getData().getTotal_comment().toString() + " Comments")
//                        notifyDataSetChanged()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<Add_Post_Comment_Response>, t: Throwable) {
//                Log.e("TAG", " ." + t.message)
//                dialog.dismiss()
//            }
//        })
    }

    fun deletecomment(position: Int, reply: String) {
//        val dialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
//        dialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.colorPrimary))
//        //        dialog.setTitleText("Fetching Data");
//        dialog.setCancelable(false)
//        dialog.show()
//        val call1: Call<Get_Post_Comment_Response> = apiInterface.commentdelete(comment_id, App.prefs.getString(AppConstants.login_token, ""))
//        call1.enqueue(object : Callback<Get_Post_Comment_Response> {
//            override fun onResponse(call: Call<Get_Post_Comment_Response>, response: Response<Get_Post_Comment_Response>) {
//                dialog.dismiss()
//                if (App.getAppInstance().isUnAuthorized(context, response.code())) {
//                    return
//                }
//                if (response.isSuccessful()) {
//                    if (response.body().getSuccess() === 1) {
////                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        if (reply == "reply") {
//                            val view: View = Show_Post.getInstance().manager.findViewByPosition(position)
//                            if (view != null) {
////                                RelativeLayout show_reply_box = view.findViewById(R.id.show_reply_box);
////                                show_reply_box.setVisibility(View.GONE);
//                                dataList!![position].setComment_reply(null)
//                                notifyItemChanged(position)
//                            }
//                        } else {
//                            dataList!!.removeAt(position)
//                            comments.setText(response.body().getData().toString() + " Comments")
//                            notifyItemRemoved(position)
//                            notifyItemRangeChanged(position, dataList!!.size)
//                        }
//
//
////                        notifyDataSetChanged();
//                        val postSocket = PostSocket(
//                            prefs.getInt(AppConstants.user_id, 0),
//                            post_id
//                        )
//                        val json: String = Gson().toJson(postSocket)
//                        socket.emit(SocketConstant.post_comment, json)
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<Get_Post_Comment_Response>, t: Throwable) {
//                dialog.dismiss()
//            }
//        })
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
        }
    }

    internal inner class StatusHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var swipeLayout: SwipeLayout
        var Delete: TextView
        var delete_reply: TextView
        var reply: TextView
        var cancel: TextView
        var send: TextView
        var img_view: ImageView = itemView.findViewById(R.id.img_view)
        var comment: SeeMoreTextView = itemView.findViewById(R.id.comment)
        var text: SeeMoreTextView
        var username: TextView = itemView.findViewById<TextView>(R.id.username)
        var block_date: TextView
        var usernm: TextView
        var reply_box: RelativeLayout
        var show_reply_box: RelativeLayout
        var edittxt: EditText

        init {
            comment.setSeeMoreText("Show More", "Show Less")
            comment.setTextMaxLength(200)
            block_date = itemView.findViewById<TextView>(R.id.block_date)
            reply_box = itemView.findViewById<RelativeLayout>(R.id.reply_box)
            show_reply_box = itemView.findViewById<RelativeLayout>(R.id.show_reply_box)
            swipeLayout = itemView.findViewById(R.id.swipe)
            Delete = itemView.findViewById<TextView>(R.id.Delete)
            delete_reply = itemView.findViewById<TextView>(R.id.delete_reply)
            reply = itemView.findViewById<TextView>(R.id.reply)
            cancel = itemView.findViewById<TextView>(R.id.cancel)
            send = itemView.findViewById<TextView>(R.id.send)
            usernm = itemView.findViewById<TextView>(R.id.usernm)
            text = itemView.findViewById(R.id.text)
            text.setSeeMoreText("Show More", "Show Less")
            text.setTextMaxLength(200)
            edittxt = itemView.findViewById<EditText>(R.id.edittxt)
        }
    }

    override fun getItemCount(): Int = dataList.size
}
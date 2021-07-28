package com.task.newapp.adapter.post

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.PostCommentAdpterBinding
import com.task.newapp.models.post.*
import com.task.newapp.models.post.ResponseGetAllPostComments.*
import com.task.newapp.ui.fragments.post.PostFragment
import com.task.newapp.utils.*
import com.task.newapp.utils.swipelayout.SwipeLayout
import com.task.newapp.utils.swipelayout.adapters.RecyclerSwipeAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostCommentAdapter(context: AppCompatActivity, post_id: Int, allPostCommentDataList: ArrayList<AllPostCommentData>, post_by_me: Int) :
    RecyclerSwipeAdapter<PostCommentAdapter.ViewHolder>() {

    val VIEW_TYPE_ITEM = 0
    val VIEW_TYPE_LOADING = 1
    var context: AppCompatActivity = context
    var allPostCommentDataList: ArrayList<AllPostCommentData>
    var post_by_me: Int
    var comment_id = 0
    var post_id: Int
    private val isCaching = true
    private var checkedPosition = -1
    private val mCompositeDisposable = CompositeDisposable()

    init {
        this.allPostCommentDataList = allPostCommentDataList
        this.post_by_me = post_by_me
        this.post_id = post_id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCommentAdapter.ViewHolder {
        val layoutBinding: PostCommentAdpterBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.post_comment_adpter, parent, false)
        return ViewHolder(layoutBinding, this)
    }

    fun setData(allPostCommentDataList: ArrayList<AllPostCommentData>, post_by_me: Int) {
        this.allPostCommentDataList = allPostCommentDataList
        this.post_by_me = post_by_me
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<AllPostCommentData> = this.allPostCommentDataList

    fun adddata(all_post_commentData: AllPostCommentData) {
        allPostCommentDataList!!.add(all_post_commentData)
        notifyItemInserted(allPostCommentDataList!!.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if (holder is ViewHolder) {

        val data = allPostCommentDataList!![position]
        holder.populateItemRows(data)
//        }
//        else if (holder is LoadingViewHolder) {
//            showLoadingView(holder as LoadingViewHolder, position)
//        }
    }

    fun addreply(replytext: String, postid: Int, replytxt: TextView, position: Int, reply_box: RelativeLayout, editText: EditText) {
//        val dialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
//        dialog.getProgressHelper().setBarColor(context.resources.getColor(R.color.colorPrimary))
        //        dialog.setTitleText("Fetching Data");
//        dialog.setCancelable(false)
//        dialog.show()


//        val comment: RequestBody = create.create(parse.parse("text/plain"), "comment")
//        val commenttext: RequestBody = create.create(parse.parse("text/plain"), replytext)
//        val call1: Call<Add_Post_Comment_Response> = apiInterface.add_postcomment(
//            comment_id, 1, comment, commenttext, postid,
//            App.prefs.getString(AppConstants.login_token, "")
//        )
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

        //-----------------------------------------------------------------------------------------------
        try {
            openProgressDialog(context)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.comment_id to comment_id,
                Constants.is_comment_reply to 1,
                Constants.type to "comment",
                Constants.comment_text to replytext,
                Constants.post_id to postid
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .add_postcomment(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseAddPostComment>() {
                        override fun onNext(responseAddPostComment: ResponseAddPostComment) {
                            Log.v("onNext: ", responseAddPostComment.toString())
//                                act!!.showToast(responseGetAllPost.message)

                            if (responseAddPostComment != null) {
                                if (responseAddPostComment.success == 1) {
                                    val postSocket = PostSocket(App.fastSave.getInt(Constants.prefUserId, 0), post_id)
                                    val json: String = Gson().toJson(postSocket)

                                    App.socket!!.emit(Constants.post_comment, json)
//                                    replytxt.setContent(replytext)
                                    replytxt.text = replytext
                                    editText.setText("")
                                    reply_box.visibility = View.GONE
                                    checkedPosition = -1

                                    val data = responseAddPostComment.data

                                    val commentReplyUser = AllPostCommentData.CommentReply.User(
                                        data!!.first_name!!,
                                        "",
                                        0,
                                        data!!.last_name!!,
                                        ""
                                    )

                                    val comment_reply = AllPostCommentData.CommentReply(
                                        0,
                                        data!!.comment_text!!,
                                        "",
                                        data!!.id,
                                        0,
                                        0,
                                        0,
                                        "",
                                        data!!.updated_at!!,
                                        commentReplyUser,
                                        0
                                    )

                                    allPostCommentDataList!![position].commentReply = comment_reply
//                                    comments.setText(response.body().getData().getTotal_comment().toString() + " Comments")
                                    notifyDataSetChanged()
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            hideProgressDialog()
        }
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
        return R.id.swipeLayout
    }

//    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var progressBar: ProgressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
//    }

    inner class ViewHolder(private val layoutBinding: PostCommentAdpterBinding, private val mAdapter: PostCommentAdapter) : RecyclerView.ViewHolder(layoutBinding.root) {

        init {
//            comment.setSeeMoreText("Show More", "Show Less")
//            comment.setTextMaxLength(200)
//            text.setSeeMoreText("Show More", "Show Less")
//            text.setTextMaxLength(200)
        }

        fun populateItemRows(allPostCommentData: AllPostCommentData) {
            val userName: String = allPostCommentData.user!!.firstName + " " + allPostCommentData.user!!.lastName
            layoutBinding.username.text = userName

            val profile_img: String = allPostCommentData.user!!.profileImage + "?q=" + System.currentTimeMillis()
            Glide.with(context)
                .load(profile_img)
                .placeholder(R.drawable.default_dp)
                .skipMemoryCache(!isCaching)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(layoutBinding.imgView)

//        holder.comment.setContent(dataList!![position].commentText)
            layoutBinding.comment.text = allPostCommentDataList!![position].commentText
            if (checkedPosition == -1) {
                layoutBinding.replyBox.visibility = View.GONE
                PostFragment.instance.isreplybox = false
            } else {
                if (checkedPosition == adapterPosition) {
                    layoutBinding.replyBox.visibility = View.VISIBLE
                    PostFragment.instance.isreplybox = true
                } else {
                    layoutBinding.replyBox.visibility = View.GONE
                    PostFragment.instance.isreplybox = false
                }
            }

            val updatedAt = allPostCommentData.updatedAt.replace("T", " ").replace(".000000Z", "")

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

            if (allPostCommentDataList!![position].commentReply == null) {
                layoutBinding.showReplyBox.visibility = View.GONE
//            holder.text.setContent("")
                layoutBinding.text.text = ""
            } else {
                layoutBinding.showReplyBox.visibility = View.VISIBLE
//            holder.text.setContent(data.commentReply?.commentText)
                layoutBinding.text.text = allPostCommentData.commentReply?.commentText
                layoutBinding.usernm.text = allPostCommentData.commentReply?.user!!.firstName + " " + allPostCommentData.commentReply?.user!!.lastName
            }

            if (post_by_me == 0) {
                if (App.fastSave.getInt(Constants.prefUserId, 0) == allPostCommentData.user!!.id) {
                    layoutBinding.swipeLayout.isSwipeEnabled = true
                    layoutBinding.deleteReply.visibility = View.VISIBLE
                } else {
                    layoutBinding.swipeLayout.isSwipeEnabled = false
                    layoutBinding.deleteReply.visibility = View.GONE
                }
            } else {
                layoutBinding.deleteReply.visibility = View.VISIBLE
                layoutBinding.swipeLayout.showMode = SwipeLayout.ShowMode.PullOut
                layoutBinding.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, layoutBinding.swipeLayout.findViewById(R.id.bottom_wrapper1))
                layoutBinding.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, layoutBinding.swipeLayout.findViewById(R.id.bottom_wraper))
                layoutBinding.swipeLayout.isLeftSwipeEnabled = allPostCommentData.commentReply == null
                layoutBinding.swipeLayout.surfaceView.setOnClickListener {
                    comment_id = allPostCommentData.id
                }
                layoutBinding.reply.setOnClickListener {
                    if (layoutBinding.replyBox.visibility == View.VISIBLE) {
                        layoutBinding.replyBox.visibility = View.GONE
                        PostFragment.instance.isreplybox = false
                        layoutBinding.swipeLayout.close(true)
                    } else {
                        layoutBinding.replyBox.visibility = View.VISIBLE
                        PostFragment.instance.isreplybox = true
                        layoutBinding.swipeLayout.close(true)
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
                    layoutBinding.replyBox.visibility = View.GONE
                    PostFragment.instance.isreplybox = false
                    layoutBinding.edittxt.setText("")
                }
                layoutBinding.send.setOnClickListener {
                    comment_id = allPostCommentData.id
                    addreply(layoutBinding.edittxt.text.toString().trim(), allPostCommentData.postId, layoutBinding.text, adapterPosition, layoutBinding.replyBox, layoutBinding.edittxt)
                }
            }
            layoutBinding.deleteReply.setOnClickListener {
                layoutBinding.swipeLayout.close(true)
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

                //Set Dialog
                DialogUtils().showConfirmationDialog(context, "", context.resources.getString(R.string.delete_replay), object : DialogUtils.DialogCallbacks {
                    override fun onPositiveButtonClick() {
                        if (context.isNetworkConnected()) {
                            if (adapterPosition < allPostCommentDataList!!.size) {
                                comment_id = allPostCommentData.commentReply!!.id
                                deletecomment(adapterPosition, "reply")
                            }
                        } else {
                            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onNegativeButtonClick() {

                    }

                    override fun onDefaultButtonClick(actionName: String) {
                    }

                })

            }
            layoutBinding.Delete.setOnClickListener(View.OnClickListener {
                layoutBinding.swipeLayout.close(true)
                DialogUtils().showConfirmationDialog(context, "", context.resources.getString(R.string.delete_comment), object : DialogUtils.DialogCallbacks {
                    override fun onPositiveButtonClick() {
                        if (context.isNetworkConnected()) {
                            if (adapterPosition < allPostCommentDataList!!.size) {
                                comment_id = allPostCommentData.id
                                deletecomment(adapterPosition, "main")
                            }
                        } else {
                            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onNegativeButtonClick() {

                    }

                    override fun onDefaultButtonClick(actionName: String) {
                    }

                })
            })
            mItemManger.bind(layoutBinding.root, adapterPosition)
        }
    }

    override fun getItemCount(): Int = allPostCommentDataList.size
}
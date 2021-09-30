package com.task.newapp.adapter.post

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.StrictMode
import android.text.*
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.toSpannable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.*
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.percolate.mentions.sample.adapters.RecyclerItemClickListener
import com.squareup.picasso.Picasso
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ItemPostBinding
import com.task.newapp.models.post.ResponseFriendsList
import com.task.newapp.models.post.ResponseGetAllPost.All_Post_Data
import com.task.newapp.models.post.ResponsePostComment
import com.task.newapp.ui.activities.profile.MyProfileActivity
import com.task.newapp.ui.activities.profile.OtherUserProfileActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.mentionlib.adapters.UsersAdapter
import com.task.newapp.utils.mentionlib.adapters.utils.SuggestionsListener
import com.task.newapp.utils.mentionlib.models.Mention
import com.task.newapp.utils.mentionlib.utils.Mentionable
import com.task.newapp.utils.mentionlib.utils.Mentions
import com.task.newapp.utils.mentionlib.utils.QueryListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONException
import java.util.HashMap


class PostFragAdapter(var context: Activity, all_post: List<All_Post_Data>) : RecyclerView.Adapter<PostFragAdapter.StatusHolder>(),
    QueryListener, SuggestionsListener {

    private val VIEW_TYPE_LOCATION = 2

    private val VIEW_TYPE_THOUGHT = 1
    private val VIEW_TYPE_ITEM = 0

    //    private val VIEW_TYPE_LOADING = 1
    var HEIGHT = 0
    var WIDTH = 0
    var all_post: ArrayList<All_Post_Data> = all_post as ArrayList<All_Post_Data>
    var onItemClick: ((View, Int, String, String) -> Unit)? = null
    var displayMetrics: DisplayMetrics
    lateinit var fontArrayThoughts: Array<String>

    //    lateinit var postList: Array<String>
    private val isCaching = true

    //-------------------------------------Mention-------------------------------------
    private var mentions: Mentions? = null
    lateinit var popupWindow: PopupWindow
    lateinit var mentionsEmptyView: TextView
    lateinit var mentions_list_layout: FrameLayout
    lateinit var mentions_list: RecyclerView
    private var usersAdapter: UsersAdapter? = null
    var jsonArrayMention = JsonArray()
    private val mCompositeDisposable = CompositeDisposable()
    lateinit var edtComment: EditText
    lateinit var layoutComment: RelativeLayout
    private var allfriendList: ArrayList<ResponseFriendsList.Data> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusHolder {
//        return
//            VIEW_TYPE_ITEM -> {
//        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
//        return StatusHolder(view, this)

        val layoutBinding: ItemPostBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_post, parent, false)

        var myview = StatusHolder(layoutBinding, this)
        myview.edtCommentListener()

//        initMention(activity = context, edtComment = layoutBinding.edtComment)
//        initPopupWindow(layoutBinding.layoutComment)

        return myview //StatusHolder(layoutBinding, this)

//            }
//            VIEW_TYPE_THOUGHT -> {
//                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_post_thought, parent, false)
//                ThoughtHolder(view)
//            }
//            else -> {
//                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.post_item_loading, parent, false)
//                LoadingViewHolder(view)
//            }
//    }
    }

    override fun getItemViewType(position: Int): Int {
//        return when (all_post!![position].type) {
//            "thought" -> VIEW_TYPE_THOUGHT
//            else -> VIEW_TYPE_ITEM
//        }

        return VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(viewHolder: StatusHolder, position: Int) {
//        when (viewHolder) {
//            is StatusHolder -> {
        val allPostData: All_Post_Data = this.all_post[position]
        viewHolder.populateItemRows(allPostData, null)

//            }
//            is LoadingViewHolder -> {
//                populateItemRows(viewHolder as RecyclerView.ViewHolder, position, null)
//            }
//            is LoadingViewHolder -> {
//                showLoadingView(viewHolder as LoadingViewHolder, position)
//            }
//        }
    }

    override fun onBindViewHolder(viewHolder: StatusHolder, position: Int, payloads: List<Any>) {

        val allPostData: All_Post_Data = this.all_post[position]
        viewHolder.populateItemRows(allPostData, payloads)
    }


    fun getScreenSize(context: Context) {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        HEIGHT = displayMetrics.heightPixels
        WIDTH = displayMetrics.widthPixels
    }

//    fun getCommentsList(): ArrayList<String> {
//        val pCommentList: ArrayList<String> = ArrayList()
//        for (i in postList.indices) {
//            pCommentList.add(postList[i])
//        }
//        return pCommentList
//    }

//    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
//        //ProgressBar would be displayed
//    }

    fun setdata(all_postNew: ArrayList<All_Post_Data>) {
//        if (isRefresh) {
//            this.all_post.clear()
//            this.all_post = ArrayList()
//        }
//        this.all_post.addAll(all_postNew)
//        notifyItemInserted(0)
//        showLog("all_post", this.all_post.size.toString())

        //-----------------Add New-----------------------

        val diffCallback = PostDiffCallback(this.all_post, all_postNew)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.all_post.clear()
        this.all_post.addAll(all_postNew)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getdata(): ArrayList<All_Post_Data> {
        return all_post
    }

    fun updateLikesCount(count: Int, position: Int, isLike: Int) {
        Log.e("updateLikesCount: ", count.toString())
        this.all_post[position].likesCount = count
        if (isLike >= 0) this.all_post[position].isLike = isLike

        notifyItemChanged(position, "likePayload")
    }

    fun updateSave(isSave: Int, position: Int) {
        Log.e("updateSave: ", isSave.toString())

        this.all_post[position].isSave = isSave
        notifyItemChanged(position, "savePayload")
    }

    fun updateFollowUnFollow(isFollow: Int, position: Int) {
        Log.e("updateFollow: ", isFollow.toString())

        this.all_post[position].isFollow = isFollow
        notifyItemChanged(position, "followPayload")
    }

    fun updateComment(data: ResponsePostComment.Data, position: Int) {
        this.all_post[position].latest_comment = data
        this.all_post[position].commentsCount = data.totalComment
        notifyItemChanged(position, "commentPayload")
    }

    fun updateTurnOnOffComment(turnOffComment: Int, position: Int) {
        this.all_post[position].turnOffComment = turnOffComment
        notifyItemChanged(position, "turnOnOffCommentPayload")
    }

    fun postComment() {
        notifyDataSetChanged()
    }

    fun clear() {
        this.all_post.clear()
        notifyDataSetChanged()
    }

    fun removePost(position: Int) {
        all_post!!.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = all_post!!.size  //if (all_post!!.isEmpty()) 0 else 5

//    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
//    }

    inner class StatusHolder(private val layoutBinding: ItemPostBinding, private val mAdapter: PostFragAdapter) : RecyclerView.ViewHolder(layoutBinding.root), View.OnClickListener {

        fun populateItemRows(all_post_data: All_Post_Data, listPayload: List<Any>?) {

            if (!(listPayload == null || listPayload.isEmpty())) {
                Log.e("populateItemRows: ", listPayload.toString())

                for (payload in listPayload) {
                    when (payload) {
                        "likePayload" -> {
                            when (all_post_data!!.isLike) {
                                0 -> layoutBinding.imgLike.setImageResource(R.drawable.ic_not_like)
                                1 -> layoutBinding.imgLike.setImageResource(R.drawable.ic_like)
                            }
                            layoutBinding.txtLikeCount.text = all_post_data.likesCount.toString()
                        }
                        "savePayload" -> {
                            when (all_post_data!!.isSave) {
                                0 -> layoutBinding.imgSave.setImageResource(R.drawable.ic_nonsave)
                                1 -> layoutBinding.imgSave.setImageResource(R.drawable.ic_save)
                            }
                        }
//                        "followPayload" -> {
//                            when (all_post_data!!.isFollow) {
//                                0 -> layoutBinding.imgSave.setImageResource(R.drawable.ic_nonsave)
//                                1 -> layoutBinding.imgSave.setImageResource(R.drawable.ic_save)
//                            }
//                        }
                        "commentPayload" -> {
                            //last Comment show
                            layoutBinding.edtComment.setText("")
                            layoutBinding.edtComment.clearFocus()

//                            if (all_post_data!!.latest_comment?.commentText.isNullOrEmpty()) {
//                                layoutBinding.txtComment.visibility = View.GONE
//                            } else {
//                                layoutBinding.txtComment.text = all_post_data.latest_comment.commentText
//                                layoutBinding.txtComment.visibility = View.VISIBLE
//                            }

                            //-----------------------Add New--------------------------
                            all_post_data.latest_comment?.commentText?.let {
                                if (all_post_data.latest_comment.commentText.isNullOrEmpty()) {
                                    layoutBinding.txtComment.visibility = View.GONE
                                } else {
                                    layoutBinding.txtComment.visibility = View.VISIBLE

                                    //----------------------Set title as per mention-----------------
                                    setMentionLastCommentOrNormal(all_post_data.latest_comment)
                                }
                            }

                            layoutBinding.txtCommentCount.text = all_post_data.commentsCount.toString()
                        }
                        "turnOnOffCommentPayload" -> {
                            //last Comment show
                            layoutBinding.layoutComment.visibility = if (all_post_data.turnOffComment == 0) View.VISIBLE else View.GONE
                        }
                    }
                }

            } else {
//                var layoutBinding: StatusHolder = (holderCommon as StatusHolder)

                //layoutBinding.postImg.visibility = View.VISIBLE
                if (all_post_data?.postContents!!.isNotEmpty()) {

                    layoutBinding.edtComment.setText("")

                    when (all_post_data!!.type) {
                        "thought", "article" -> {

//                            var layoutBinding: StatusHolder = (holderCommon as StatusHolder)

                            //set thought layout
                            layoutBinding.postImg.visibility = View.VISIBLE
                            layoutBinding.rlThought.visibility = View.VISIBLE
                            layoutBinding.txtLocation.visibility = View.GONE
                            layoutBinding.flMediaContent.visibility = View.GONE

                            setThought(all_post_data?.postContents!![0].thoughtType, all_post_data?.postContents!![0])

                        }
                        "location" -> {

                            //set location layout
                            layoutBinding.txtMore.visibility = View.GONE
                            layoutBinding.rlThought.visibility = View.GONE
                            layoutBinding.flMediaContent.visibility = View.GONE

                            //-----------------------Location-----------------------
                            layoutBinding.txtLocation.visibility = if (all_post_data.isLocation == 1) View.VISIBLE else View.GONE
                            all_post_data.location?.let {
                                layoutBinding.txtLocation.text = all_post_data.location
                            }
                        }
                        else -> {
                            //------------------------Set ViewPager2---------------------------

                            //Set Adapter
//                            val adapter = PostFragImageVideoViewPager2Adapter(context, all_post_data?.postContents)
//                            layoutBinding.recyclerView.adapter = adapter

                            //                            val zoomOutPageTransformer = ZoomOutPageTransformer()
//                            layoutBinding.viewPager2.setPageTransformer { page, position ->
//                                zoomOutPageTransformer.transformPage(page, position)
//                            }
                            //layoutBinding.dotsIndicator.setViewPager2(layoutBinding.viewPager)

//                            if (all_post_data?.postContents.size > 1) {
//                                layoutBinding.dotsIndicator.visibility = View.VISIBLE
//                            } else {
//                                layoutBinding.dotsIndicator.visibility = View.GONE
//                            }

                            //-------------------------------Add New-------------------------------
//                            layoutBinding.postImg.visibility = View.GONE
//                            layoutBinding.dotsIndicator.visibility = View.VISIBLE
//
//                            val adapter1 = FeedAdapter(context)
//
//                            layoutBinding.recyclerView.layoutManager = CenterLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//                            layoutBinding.recyclerView.adapter = adapter1
//
//                            all_post_data?.postContents.forEachIndexed { index, postContent ->
//                                adapter1.add(postContent)
//                            }
//
//                            if (layoutBinding.recyclerView.handingVideoHolder != null) {
//                                layoutBinding.recyclerView.handingVideoHolder.playVideo()
//                            }

                            //-------------------------------Add New-------------------------------

//                            you can pass local file uri, but make sure it exists
                            val p: Picasso = Picasso.with(context)
                            val adapter = PostFragImageVideoViewPager2Adapter(context, all_post_data?.postContents, p)
                            var mLayoutManager = LinearLayoutManager(context)
                            mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

                            //-------------------------Add New-------------------------
                            ZoomHelper.getInstance().addOnZoomStateChangedListener(adapter)

                            layoutBinding.recyclerView.layoutManager = mLayoutManager
                            layoutBinding.recyclerView.itemAnimator = DefaultItemAnimator()

                            val snapHelper: SnapHelper = PagerSnapHelper()
                            layoutBinding.recyclerView.onFlingListener = null
                            snapHelper.attachToRecyclerView(layoutBinding.recyclerView)

                            //todo before setAdapter
                            layoutBinding.recyclerView.setActivity(context)

                            //optional - to play only first visible video
                            layoutBinding.recyclerView.setPlayOnlyFirstVideo(false) // false by default

                            layoutBinding.recyclerView.setVisiblePercent(50f) // percentage of View that needs to be visible to start playing

                            if (all_post_data?.postContents.size > 1) View.VISIBLE else View.GONE

                            layoutBinding.recyclerView.adapter = adapter

                            //call this functions when u want to start autoplay on loading async lists (eg firebase)
                            layoutBinding.recyclerView.smoothScrollBy(0, 1)
                            layoutBinding.recyclerView.smoothScrollBy(0, -1)

                            layoutBinding.pageIndicator.attachToRecyclerView(layoutBinding.recyclerView)

                            //---------------------set normal layout--------------------------
                            layoutBinding.postImg.setImageBitmap(null)
                            layoutBinding.postImg.visibility = View.GONE
                            layoutBinding.txtMore.visibility = View.GONE
                            layoutBinding.txtLocation.visibility = View.GONE
                            layoutBinding.rlThought.visibility = View.GONE
                            layoutBinding.flMediaContent.visibility = View.VISIBLE

//                            Glide.with(context).asBitmap()
//                                .load(all_post_data!!.postContents[0].thumb)
//                                //                .thumbnail(0.25f)
//                                .centerCrop()
//                                .apply(RequestOptions.skipMemoryCacheOf(!isCaching))
//                                .apply(RequestOptions.diskCacheStrategyOf(if (isCaching) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
//                                .error(R.drawable.gallery_post)
//                                .into(object : CustomTarget<Bitmap?>() {
//                                    override fun onLoadCleared(placeholder: Drawable?) {
//                                        layoutBinding.imgView.visibility = View.VISIBLE
//                                    }
//
//                                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap?>?) {
//                                        try {
//                                            layoutBinding.imgDefault.visibility = View.GONE
//                                            layoutBinding.postImg.setImageBitmap(bitmap)
//
//                                            val pxl = bitmap.getPixel(500, 500)
//                                            layoutBinding.imgShadow.setColorFilter(pxl)
//                                        } catch (ignore: Exception) {
//                                        }
//                                    }
//                                })
                        }
                    }

//                    initMention(activity = context, edtComment = layoutBinding.edtComment)
//                    initPopupWindow(layoutBinding.layoutComment)

//                    layoutBinding.edtComment.setOnTouchListener { v, event -> // change the background color
//                        initMention(activity = context, edtComment = layoutBinding.edtComment)
//                        initPopupWindow(layoutBinding.layoutComment)
//                        false
//                    }
                    //-----------------------------Common---------------------------------
                    layoutBinding.layoutComment.visibility = if (all_post_data.turnOffComment == 0) View.VISIBLE else View.GONE

                    //-------------------------last Comment show--------------------------
//                    if (all_post_data.latest_comment?.commentText.isNullOrEmpty()) {
//                        layoutBinding.txtComment.visibility = View.GONE
//                    } else {
//                        layoutBinding.txtComment.text = all_post_data.latest_comment.commentText
//                        layoutBinding.txtComment.visibility = View.VISIBLE
//                    }ṣ

                    //------------------------Add New--------------------
                    all_post_data.latest_comment?.commentText?.let {
                        if (all_post_data.latest_comment.commentText.isNullOrEmpty()) {
                            layoutBinding.txtComment.visibility = View.GONE
                        } else {
                            layoutBinding.txtComment.visibility = View.VISIBLE

                            //----------------------Set title as per mention-----------------
                            setMentionLastCommentOrNormal(all_post_data.latest_comment)
                        }
                    }

                    //-------------------------Like Dislike--------------------------
                    when (all_post_data!!.isLike) {
                        0 -> layoutBinding.imgLike.setImageResource(R.drawable.ic_not_like)
                        1 -> layoutBinding.imgLike.setImageResource(R.drawable.ic_like)
                    }

                    //-------------------------Save Unsave--------------------------
                    when (all_post_data!!.isSave) {
                        0 -> layoutBinding.imgSave.setImageResource(R.drawable.ic_nonsave)
                        1 -> layoutBinding.imgSave.setImageResource(R.drawable.ic_save)
                    }

                    //-------------------------Set Count--------------------------
                    layoutBinding.txtLikeCount.text = all_post_data.likesCount.toString()
                    layoutBinding.txtCommentCount.text = all_post_data.commentsCount.toString()

//                    layoutBinding.nameTxt.setTextMaxLength(100)

                    //-------------------------set Post Button enable/diable --------------------------
//                    layoutBinding.edtComment.addTextChangedListener(object : TextWatcher {
//                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                        }
//
//                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                        }
//
//                        override fun afterTextChanged(s: Editable?) {
//                            Log.e("afterTextChanged: ", s.toString())
//                            if (s.toString().trim().isEmpty()) {
//                                layoutBinding.txtPost.setTextColor(context.resources.getColor(R.color.gray3))
////                                enableOrDisableImageViewTint(context, s.toString().trim().isNotEmpty(), layoutBinding.txtPost)
//                            } else {
//                                layoutBinding.txtPost.setTextColor(context.resources.getColor(R.color.gray4))
////                                enableOrDisableImageViewTint(context, true, layoutBinding.txtPost)
//
//                            }
//                        }
//                    })

//                    layoutBinding.edtComment.setOnFocusChangeListener { v, hasFocus ->
//                        if (hasFocus) {
//                            initMention(activity = context, edtComment = v as EditText)
//                            initPopupWindow(layoutBinding.layoutComment)
//                        }
//                    }


                    //-----------------------------Turn on/off comment layout hide/show---------------------------------

//                    if (all_post_data.isPostForPage == 1) {
//                        if (all_post_data.page.isNotEmpty()) {
//
//                            if (!all_post_data.title.isNullOrEmpty()) {
//                                layoutBinding.titleTxt.text = all_post_data.title
//                                layoutBinding.titleTxt.visibility = View.VISIBLE
//                            } else {
//                                layoutBinding.titleTxt.visibility = View.GONE
//                            }
//                            layoutBinding.txtUsername.text = all_post_data.page[0].name
////                            layoutBinding.txtUsername.setContent(all_post_data.page[0].name)
//                        }
//                    } else {
                    var fullName = (all_post_data.user.firstName ?: "") + " " + (all_post_data.user.lastName ?: "")

                    //Check User Tag and add with name
                    if (all_post_data.tagged.isEmpty()) {
                        layoutBinding.txtTagUsername.visibility = View.GONE
                        layoutBinding.txtUsername.visibility = View.VISIBLE
                        layoutBinding.txtUsername.text = fullName
                    } else {
                        layoutBinding.txtTagUsername.visibility = View.VISIBLE
                        layoutBinding.txtUsername.visibility = View.GONE

                        var commaSeperatedTagsNames = all_post_data.tagged[0].let {
                            (it.first_name ?: "").plus(" ").plus(it.last_name ?: "")
                        }
                        if (all_post_data.tagged.size > 1) {
                            commaSeperatedTagsNames = commaSeperatedTagsNames.plus("<font color='#AAA1A1'> and </font>").plus(all_post_data.tagged.size - 1).plus(" others.")
                        }

                        val styledText = "$fullName <font color='#AAA1A1'> is with </font>$commaSeperatedTagsNames"
                        layoutBinding.txtTagUsername.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE)
                        Log.e("commaSeperatedTagsNames", "$commaSeperatedTagsNames,$adapterPosition")
                    }
//                    }

                    if (all_post_data.title.isNullOrEmpty()) {
                        layoutBinding.titleTxt.visibility = View.GONE
                    } else {
                        layoutBinding.titleTxt.visibility = View.VISIBLE

                        //----------------------Set title as per mention-----------------
                        setMentionOrNormal(all_post_data)
                    }

                    layoutBinding.imgView.load(all_post_data!!.user.profileImage, true, fullName, all_post_data!!.user.profileColor)

//                    layoutBinding.imgPlaypause.visibility = View.VISIBLE
//
//                    if (all_post_data!!.postContents[0].type == "video")
//                        layoutBinding.imgPlaypause.visibility = View.VISIBLE
//                    else
//                        layoutBinding.imgPlaypause.visibility = View.INVISIBLE


                    layoutBinding.root.post { layoutBinding.root.requestLayout() }

//                    if (all_post_data!!.postContents.size > 1) {
//                        layoutBinding.imgMulti.visibility = View.VISIBLE
//                    } else {
//                        layoutBinding.imgMulti.visibility = View.INVISIBLE
//                    }

                    layoutBinding.txtTime.text = all_post_data.createdAt
                }
            }
        }

        private fun setMentionOrNormal(allpostdata: All_Post_Data) {
            try {
                layoutBinding.titleTxt.text = allpostdata.title

                allpostdata.mention?.let {
                    val strSpannable = layoutBinding.titleTxt.text.toSpannable() as SpannableString

                    var jsonArray = JSONArray(allpostdata.mention)
                    if (jsonArray.length() > 0) {
                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            var id = item.getString("id")
                            var name = item.getString("name")
//                            var accountId = item.getString("accountId")

                            strSpannable.withClickableSpan(name, onClickListener = ({
                                //On click Open user profile
//                                context.showToast("$id,$name")

                                openProfileActivity(context, id.toInt())
                            }))
                        }

                        layoutBinding.titleTxt.text = strSpannable
                        layoutBinding.titleTxt.movementMethod = LinkMovementMethod.getInstance();
                    }
                }
            } catch (e: JSONException) {
            }
        }

        private fun setMentionLastCommentOrNormal(allpostdata: ResponsePostComment.Data) {
            try {
                layoutBinding.txtComment.text = allpostdata.commentText

                allpostdata.mention?.let {
                    val strSpannable = layoutBinding.txtComment.text.toSpannable() as SpannableString

                    var jsonArray = JSONArray(allpostdata.mention)
                    if (jsonArray.length() > 0) {
                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            var id = item.getString("id")
                            var name = item.getString("name")
//                            var accountId = item.getString("accountId")

                            strSpannable.withClickableSpan(name, onClickListener = ({
                                //On click Open user profile
//                                context.showToast("$id,$name")

                                openProfileActivity(context, id.toInt())
                            }))
                        }

                        layoutBinding.txtComment.text = strSpannable
                        layoutBinding.txtComment.movementMethod = LinkMovementMethod.getInstance();
                    }
                }
            } catch (e: JSONException) {
            }
        }

        private fun setThought(thoughtType: String, all_post_data: All_Post_Data.PostContent) {

            val backgroundType: String? = all_post_data.backgroundType   //pattern
            val color: String? = all_post_data.color
            val fontColor: String? = all_post_data.fontColor
            val patternId: String = all_post_data.patternId
            val alignment: String? = all_post_data.alignment
            val fontStyle: String? = all_post_data.fontStyle
            val isBold: Int? = all_post_data.isBold
            val isItalic: Int? = all_post_data.isItalic
            val isUnderline: Int? = all_post_data.isUnderline
            val content: String? = all_post_data.content

            if (thoughtType == "text") {
                //normal text thought
                layoutBinding.txtMore.visibility = View.GONE
                layoutBinding.rlThought.gravity = Gravity.CENTER
                if (backgroundType == "pattern") {
                    layoutBinding.postImg.setImageBitmap(null)
                    layoutBinding.postImg.setBackgroundColor(0)

                    Glide.with(context).load(patternId).into(layoutBinding.postImg)

//                Glide.with(context).asBitmap()
//                    .load(patternId)
//                    .centerCrop()
//                    .apply(RequestOptions.skipMemoryCacheOf(!isCaching))
//                    .apply(RequestOptions.diskCacheStrategyOf(if (isCaching) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
//                    .error(R.drawable.gallery_post)
//                    .into(object : CustomTarget<Bitmap?>() {
//                        override fun onLoadCleared(placeholder: Drawable?) {
//                            layoutBinding.imgView.visibility = View.VISIBLE
//                        }
//
//                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap?>?) {
//                            try {
//                                layoutBinding.imgDefault.visibility = View.GONE
//                                layoutBinding.postImg.setImageBitmap(bitmap)
//
//                                val pxl = bitmap.getPixel(500, 500)
//                                layoutBinding.imgShadow.setColorFilter(pxl)
//                            } catch (ignore: Exception) {
//                            }
//                        }
//                    })
                } else {
                    layoutBinding.postImg.setBackgroundColor(0)
                    layoutBinding.postImg.setImageBitmap(null)

                    if (color!!.startsWith("#"))
                        layoutBinding.postImg.setBackgroundColor(Color.parseColor(color))
                    else
                        layoutBinding.postImg.setBackgroundColor(context.resources.getColor(R.color.white))
                }

                if (fontColor?.startsWith("#") == true) {
                    layoutBinding.txtThought.setTextColor(Color.parseColor(fontColor))
                }

                Log.e("setThought: ", fontStyle.toString())
                fontStyle?.let {
                    Log.e("setThought:After Let ", fontStyle.toString())
                    layoutBinding.txtThought.typeface = Typeface.createFromAsset(context.assets, fontArrayThoughts[fontStyle.toInt()])
                }

                alignment?.let {
                    when (it) {
                        "center" -> layoutBinding.txtThought.gravity = Gravity.CENTER
                        "left" -> layoutBinding.txtThought.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                        "right" -> layoutBinding.txtThought.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
                    }
                }

                isBold?.let {
                    if (it == 0) {
                        if (fontStyle != null) {
                            layoutBinding.txtThought.setTypeface(Typeface.createFromAsset(context.assets, fontArrayThoughts[fontStyle.toInt()]), if (isItalic == 1) Typeface.ITALIC else Typeface.NORMAL)
                        }
                    } else {
                        if (fontStyle != null) {
                            layoutBinding.txtThought.setTypeface(Typeface.createFromAsset(context.assets, fontArrayThoughts[fontStyle.toInt()]), if (isItalic == 1) Typeface.BOLD_ITALIC else Typeface.BOLD)
                        }
                    }
                }

                isUnderline?.let {
                    if (it == 0) {
                        val htmlString = content.toString()
                        layoutBinding.txtThought.setText(Html.fromHtml(htmlString))
                    } else {
                        val htmlString = "<u>" + content.toString() + "</u>"
                        layoutBinding.txtThought.setText(Html.fromHtml(htmlString))
                    }
                }

                layoutBinding.txtThought.textSize = 24f
            } else {
                //--------------article ------Multiline text thought

                setEditTextMaxLength(300)

                if (content.toString().length > 300) layoutBinding.txtMore.visibility = View.VISIBLE
                else layoutBinding.txtMore.visibility = View.GONE

                layoutBinding.txtThought.textSize = 16f
                layoutBinding.postImg.visibility = View.GONE

                content?.let {
                    layoutBinding.txtThought.setText(content.toString())
                }

                var params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                layoutBinding.rlThought.layoutParams = params

                layoutBinding.txtMore.setOnClickListener {
                    //Expand Layout
                    setEditTextMaxLength(content.toString().length)

                    content?.let {
                        layoutBinding.txtThought.setText(content.toString())
                    }

                    var params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutBinding.rlThought.layoutParams = params

                    layoutBinding.txtMore.visibility = View.GONE
                }
            }
        }

        init {
            layoutBinding.imgLike.setOnClickListener(this)
            layoutBinding.txtPost.setOnClickListener(this)
            layoutBinding.imgSave.setOnClickListener(this)
            layoutBinding.llComment.setOnClickListener(this)
            layoutBinding.moreIv.setOnClickListener(this)
            layoutBinding.rlMain.setOnClickListener(this)
            layoutBinding.imgView.setOnClickListener(this)
            layoutBinding.txtTagUsername.setOnClickListener(this)
//            layoutBinding.txtUsername.setOnClickListener(this)

            //----------------------------init Mention-----------------------------
            allfriendList = ArrayList()
            edtComment = layoutBinding.edtComment
            layoutComment = layoutBinding.layoutComment

            //------------------------------Add New----------------------------
            //edtCommentListener()

        }

        fun edtCommentListener() {
            layoutBinding.edtComment.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    Log.e("afterTextChanged: ", s.toString())
                    if (s.toString().trim().isEmpty()) {
                        layoutBinding.txtPost.setTextColor(context.resources.getColor(R.color.gray3))
//                                enableOrDisableImageViewTint(context, s.toString().trim().isNotEmpty(), layoutBinding.txtPost)
                    } else {

                        if (s.toString().length == 1) {
                            initMention(activity = context, edtComment = layoutBinding.edtComment)
                            initPopupWindow(layoutBinding.layoutComment)
                        }

                        layoutBinding.txtPost.setTextColor(context.resources.getColor(R.color.gray4))
//                                enableOrDisableImageViewTint(context, true, layoutBinding.txtPost)

                    }
                }
            })
        }

        private fun setEditTextMaxLength(length: Int) {
            val filterArray = arrayOfNulls<InputFilter>(1)
            filterArray[0] = InputFilter.LengthFilter(length)
            layoutBinding.txtThought.filters = filterArray
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.imgLike,
                R.id.imgSave,
                R.id.llComment,
                R.id.more_iv,
                R.id.rlMain,
                R.id.img_view,
                R.id.txt_tag_username
                    /*R.id.txt_username*/ -> {
                    if (onItemClick != null) {
                        onItemClick?.invoke(v, bindingAdapterPosition, "", "")
                    }
                }
                R.id.txtPost -> {
                    if (onItemClick != null) {
                        mentions!!.insertedMentions?.let {
                            (mentions!!.insertedMentions as ArrayList).clear()
                        }
                        onItemClick?.invoke(v, bindingAdapterPosition, layoutBinding.edtComment.text.toString().trim(), Gson().toJson(jsonArrayMention))
                    }
                }
            }
        }
    }

//    inner class ThoughtHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var rlThought: RelativeLayout = itemView.findViewById(R.id.rlThought)
//        var txtThought: AppCompatEditText = itemView.findViewById(R.id.txtThought)
//        var imgBg: ImageView = itemView.findViewById(R.id.imgBg)
//    }

    init {
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        displayMetrics = DisplayMetrics()
        (context as AppCompatActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        StrictMode.setThreadPolicy(policy)
        getScreenSize(context)
        fontArrayThoughts = context.resources.getStringArray(R.array.fontArrayThoughts)
        //postList = ArrayList<String>(this.all_post.size)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    //-----------------------------------DiffUtil Class-----------------------------------------
    class PostDiffCallback(oldPostList: List<All_Post_Data>, newPostList: List<All_Post_Data>) : DiffUtil.Callback() {
        private val mOldPostList: List<All_Post_Data> = oldPostList
        private val mNewPostList: List<All_Post_Data> = newPostList
        override fun getOldListSize(): Int {
            return mOldPostList.size
        }

        override fun getNewListSize(): Int {
            return mNewPostList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldPostList[oldItemPosition].id == mNewPostList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldPostData: All_Post_Data = mOldPostList[oldItemPosition]
            val newPostData: All_Post_Data = mNewPostList[newItemPosition]

//            return oldPostData.id == newPostData.id
            return oldPostData == newPostData

//            return oldPostData.id == newPostData.id &&
//                    oldPostData.commentsCount == newPostData.commentsCount &&
//                    oldPostData.isLike == newPostData.isLike &&
//                    oldPostData.isSave == newPostData.isSave
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            // Implement method if you're going to use ItemAnimator
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

    }

//    private fun initData(adapter: FeedAdapter) {
//        for (i in 0..9) {
//            adapter.add(
//                Feed(
//                    Video(
//                        "https://instagram.fhan3-1.fna.fbcdn.net/vp/fdac380ecfa349f38f4f1701abcc29b5/5A681B7A/t51.2885-15/e15/26226589_171205453644367_3851432199505051648_n.jpg",
//                        "https://instagram.fhan3-1.fna.fbcdn.net/vp/cc9f81b0cd5c3100895184072dac16d5/5A68A1A0/t50.2886-16/19231638_185054565422809_5378340150369583104_n.mp4",
//                        0
//                    ),
//                    Feed.Model.M1
//                )
//            )
//            adapter.add(
//                Feed(
//                    Video(
//                        "http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70,so_0/v1481795681/2_rp0zyy.jpg",
//                        "https://firebasestorage.googleapis.com/v0/b/flickering-heat-5334.appspot.com/o/demo1.mp4?alt=media&token=f6d82bb0-f61f-45bc-ab13-16970c7432c4",
//                        0
//                    ), Feed.Model.M2
//                )
//            )
//            adapter.add(
//                Feed(
//                    Photo("https://instagram.fhan3-1.fna.fbcdn.net/vp/eda3c07414af6af5b63242572b6691d0/5AF3DAAF/t51.2885-15/s640x640/sh0.08/e35/26296225_2013411645646716_7530350444791463936_n.jpg"),
//                    Feed.Model.M1
//                )
//            )
//            adapter.add(
//                Feed(
//                    Video(
//                        "http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70,so_0/v1481795675/3_yqeudi.jpg",
//                        "http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70/v1481795675/3_yqeudi.mp4",
//                        0
//                    ), Feed.Model.M2
//                )
//            )
//            adapter.add(
//                Feed(
//                    Video(
//                        "https://instagram.fhan3-1.fna.fbcdn.net/vp/d0acc072a7333fe1dd27effe686fbfba/5A687F55/t51.2885-15/e15/26409479_1986649528267692_3453338561376419840_n.jpg",
//                        "https://instagram.fhan3-1.fna.fbcdn.net/vp/b1728a20fdf1e3a07060565b8e16c7b5/5A683054/t50.2886-16/26691065_2029557753956918_1909061054097260544_n.mp4",
//                        0
//                    ), Feed.Model.M1
//                )
//            )
//            adapter.add(
//                Feed(
//                    Photo("https://instagram.fhan3-1.fna.fbcdn.net/vp/44464d1c954f7a32c1ce974c5def7a15/5AEA1568/t51.2885-15/s640x640/sh0.08/e35/26863501_553136018374196_6715920436678361088_n.jpg"),
//                    Feed.Model.M1
//                )
//            )
//            adapter.add(
//                Feed(
//                    Video(
//                        "http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70,so_0/v1491561340/hello_cuwgcb.jpg",
//                        "http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70/v1491561340/hello_cuwgcb.mp4",
//                        0
//                    ), Feed.Model.M2
//                )
//            )
//            adapter.add(
//                Feed(
//                    Video(
//                        "https://instagram.fhan3-1.fna.fbcdn.net/vp/edc932372188896d32b39ce29b85baa6/5A68F1F7/t51.2885-15/e35/26071164_412111042577897_3016654672157999104_n.jpg",
//                        "https://instagram.fhan3-1.fna.fbcdn.net/vp/d45509c5c691981913139b3319618564/5A681FE9/t50.2886-16/26821622_554234641576625_2150062079877406200_n.mp4",
//                        0
//                    ), Feed.Model.M2
//                )
//            )
//            adapter.add(
//                Feed(
//                    Photo("https://instagram.fhan3-1.fna.fbcdn.net/vp/f72190a2129f384f5655277a6912aeda/5AF845DC/t51.2885-15/sh0.08/e35/p640x640/26153004_719204978284531_943265240148082688_n.jpg"),
//                    Feed.Model.M1
//                )
//            )
//        }
//    }

//    fun showAppUserDialog(activity: Activity, data: List<Tagged>) {
//        val dialog = Dialog(activity)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.dialog_app_user)
//        val rvAppUser: RecyclerView = dialog.findViewById(R.id.rv_app_user)!!
//
//        var mLayoutManager = LinearLayoutManager(activity)
//        rvAppUser.layoutManager = mLayoutManager
//
//        val adapter = MoreUserAdapter(activity, data)
//        rvAppUser.adapter = adapter
//
//        dialog.show()
//    }

    //--------------------------------------------------Mention----------------------------------------------------------
    /**
     * Initialize views and utility objects.
     */
    private fun initMention(activity: Activity, edtComment: EditText) {
        mentions = Mentions.Builder(activity, edtComment)
            .suggestionsListener(this)
            .queryListener(this)
            .build()
    }

    override fun displaySuggestions(display: Boolean) {
        Log.e("displaySuggestions: ", display.toString())
        if (display) {
            if (this::mentions_list_layout.isInitialized) {
                mentions_list_layout.visibility = View.VISIBLE
            }
        } else {
            if (this::mentions_list_layout.isInitialized) {
                mentions_list_layout.visibility = View.GONE
            }
        }
    }

    override fun onQueryReceived(query: String?) {
        Log.e("onQueryReceived: ", query!!)

        getFriendList(0, query!!) { users ->
            if (users.isNotEmpty()) {
                if (users.isNotEmpty()) {
                    usersAdapter!!.clear()
                    usersAdapter!!.setCurrentQuery(query!!)
                    usersAdapter!!.addAll(users)
                    showMentionsList(true)
                } else {
                    showMentionsList(false)
                }
            }
        }
    }

    private fun getFriendList(currentSize: Int, searchText: String, callback: ((ArrayList<ResponseFriendsList.Data>) -> Unit)? = null) {
        try {
            try {
                //openProgressDialog(activity)

                val hashMap: HashMap<String, Any> = hashMapOf(
                    Constants.flag to "mention",
                    Constants.term to searchText
//                        Constants.limit to requireActivity().getString(R.string.limit_20),
//                        Constants.offset to currentSize.toString()
                )

                mCompositeDisposable.add(
                    ApiClient.create()
                        .search_contacts(hashMap)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableObserver<ResponseFriendsList>() {
                            override fun onNext(responseFriendsList: ResponseFriendsList) {
                                Log.v("onNext: ", responseFriendsList.toString())
                                if (responseFriendsList != null && responseFriendsList.success == 1) {

                                    if (responseFriendsList.data.isNotEmpty()) {

                                        allfriendList?.let { it.clear() }
                                        allfriendList.addAll(responseFriendsList.data as ArrayList<ResponseFriendsList.Data>)
                                    }
                                }
                            }

                            override fun onError(e: Throwable) {
                                hideProgressDialog()
                                Log.v("onError: ", e.toString())
                                callback?.invoke(allfriendList)
                            }

                            override fun onComplete() {
                                hideProgressDialog()
                                callback?.invoke(allfriendList)
                            }
                        })
                )

            } catch (e: Exception) {
                e.printStackTrace()
                hideProgressDialog()
                callback?.invoke(allfriendList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            hideProgressDialog()
        }
    }

    private fun showMentionsList(display: Boolean) {
        Log.e("showMentionsList: ", display.toString())

        mentions_list_layout.visibility = View.VISIBLE
        if (display) {
            showPopupWindow(layoutComment)
            mentionsEmptyView.visibility = View.GONE
        } else {
            popupWindow?.let {
                mentionsEmptyView.visibility = View.VISIBLE
                it.dismiss()
            }
        }
    }

    private fun initPopupWindow(anchor: View) {
        popupWindow = PopupWindow(anchor.context).apply {
            isOutsideTouchable = true
            val inflater = LayoutInflater.from(context)
            contentView = inflater.inflate(R.layout.mention_popup_layout, null)/*.apply {
                measure(
                    View.MeasureSpec.makeMeasureSpec(WindowManager.LayoutParams.MATCH_PARENT, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
            }*/

            width = WindowManager.LayoutParams.MATCH_PARENT
            height = 800

            mentionsEmptyView = contentView.findViewById(R.id.mentions_empty_view)
            mentions_list_layout = contentView.findViewById(R.id.mentions_list_layout)
            mentions_list = contentView.findViewById(R.id.mentions_list)
            mentions_list.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rect_rounded_bg_white))

//            mentions_list_layout.foreground.alpha = 0

            setupMentionsListForPopup(mentions_list)

        }.also { popupWindow ->
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Absolute location of the anchor view
//            val location = IntArray(2).apply {
//                anchor.getLocationOnScreen(this)
//            }
//            val size = Size(
//                popupWindow.contentView.measuredWidth,
//                200/*popupWindow.contentView.measuredHeight*/
//            )
//            popupWindow.showAtLocation(
//                anchor,
//                Gravity.TOP or Gravity.START,
//                0,/*location[0] - (size.width - anchor.width) / 2,*/
//                anchor.bottom-50/*location[1] - size.height*/
//            )
        }
    }

    private fun setupMentionsListForPopup(mentionsList: RecyclerView) {
        mentionsList.layoutManager = LinearLayoutManager(context)
        usersAdapter = UsersAdapter(context)
        mentionsList.adapter = usersAdapter

        mentionsList.addOnItemTouchListener(RecyclerItemClickListener(context, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val user = usersAdapter!!.getItem(position)

                user?.let {
                    val mention = Mention()
                    mention.mentionName = user.firstName + " " + user.lastName
                    mention.userId = user.id.toString()  //Add New
                    mention.mentionAccountId = user.accountId //Add New
                    mentions!!.insertMention(mention)
//                    Log.e("onItemClick: ", mentions.toString())

                    //----------------------------Add New----------------------------
                    highlightMentions(commentTextView = edtComment, mentions = mentions!!.insertedMentions)

                    //------------------Create JSONArray-----------------
                    jsonArrayMention = JsonArray()
                    mentions!!.insertedMentions.forEach { mentionable ->

                        val jsonObject = JsonObject()
                        jsonObject.addProperty("id", mentionable.userId)
                        jsonObject.addProperty("accountId", mentionable.mentionAccountId)
                        jsonObject.addProperty("name", mentionable.mentionName)
                        jsonArrayMention.add(jsonObject)
                    }
                    Log.e("onItemClick: jsonArray", Gson().toJson(jsonArrayMention))
                }
            }
        }))
    }

    private fun highlightMentions(commentTextView: EditText?, mentions: List<Mentionable>?) {
        if (commentTextView != null && mentions != null && mentions.isNotEmpty()) {
//            val spannable: Spannable = SpannableString(commentTextView.text)
            for (mention in mentions) {
                if (mention != null) {
                    val start = mention.mentionOffset
                    val end = start + mention.mentionLength
                    if (commentTextView.length() >= end) {
//                        spannable.setSpan(ForegroundColorSpan(orange), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//                        commentTextView.setText(spannable, TextView.BufferType.SPANNABLE)

                        //-----------------------Add New-------------------------
                        val strSpannable = commentTextView.text.toSpannable() as SpannableString
                        strSpannable.withClickableSpan(mention.mentionName!!, onClickListener = ({
                            Toast.makeText(context, mention.mentionName, Toast.LENGTH_SHORT).show()

                            openProfileActivity(context, mention.userId!!.toInt())
                        }))
                        commentTextView.movementMethod = LinkMovementMethod.getInstance()
//                        commentTextView.text = strSpannable
                        commentTextView.setText(strSpannable)

                    } else {
                        //Something went wrong.  The expected text that we're trying to highlight does not
                        // match the actual text at that position.
                        Log.w("Mentions Sample", "Mention lost. [$mention]")
                    }
                }
            }
        }
    }

    private fun showPopupWindow(anchor: View) {
        popupWindow?.let {
//            popupWindow.showAsDropDown(anchor)/*, 10, -(location[1] - anchor.height), Gravity.TOP)*/
//            popupWindow.showAsDropDown(anchor)

            popupWindow.showAtLocation(anchor, Gravity.TOP, 0, 0);

//            popupWindow.showAsDropDown(anchor, 0, -(anchor.height + popupWindow.height))
        }
    }

}
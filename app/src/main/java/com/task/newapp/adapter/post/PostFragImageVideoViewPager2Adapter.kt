package com.task.newapp.adapter.post

import android.app.Activity
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import com.aghajari.zoomhelper.ZoomHelper
import com.allattentionhere.autoplayvideos.AAH_CustomViewHolder
import com.allattentionhere.autoplayvideos.AAH_VideosAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.jsibbold.zoomage.ZoomageView
import com.luck.picture.lib.tools.ScreenUtils
import com.squareup.picasso.Picasso
import com.task.newapp.R
import com.task.newapp.models.post.ResponseGetAllPost.All_Post_Data
import com.task.newapp.utils.showLog
import java.io.File
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

import com.google.android.exoplayer2.DefaultLoadControl

import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ui.PlayerView


class PostFragImageVideoViewPager2Adapter(var context: Activity, all_post: List<All_Post_Data.PostContent>, p: Picasso) : AAH_VideosAdapter(),
    ZoomHelper.OnZoomStateChangedListener {
    private val VIEW_TYPE_IMAGE = 0
    private val VIEW_TYPE_VIDEO = 1

    var all_post: ArrayList<All_Post_Data.PostContent> = all_post as ArrayList<All_Post_Data.PostContent>
    var onItemClick: ((View, Int, String) -> Unit)? = null
    val isCaching = true
    var picasso: Picasso = p
    var width: Int = 0
    var height: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AAH_CustomViewHolder {
        return if (viewType == VIEW_TYPE_IMAGE) {
            val itemView1: View = LayoutInflater.from(parent.context).inflate(R.layout.item_post_photo_pager, parent, false)
            PhotoViewHolder(itemView1)
        } else {
            val itemView2: View = LayoutInflater.from(parent.context).inflate(R.layout.item_post_video_pager, parent, false)
            VideoViewHolder(itemView2)
        }
    }


    inner class PhotoViewHolder(itemView: View) : AAH_CustomViewHolder(itemView) {
//        val ivInfo: ZoomageView = itemView.findViewById<ZoomageView>(R.id.ivInfo)
        val ivInfo: ImageView = itemView.findViewById<ImageView>(R.id.ivInfo)
        val rlMainPhoto: RelativeLayout = itemView.findViewById<RelativeLayout>(R.id.rlMainPhoto)
        val imgShadow: ImageView = itemView.findViewById<ImageView>(R.id.imgShadow)
    }

    inner class VideoViewHolder(itemView: View) : AAH_CustomViewHolder(itemView) {
        var isMuted = false

        val flMainVideo: RelativeLayout = itemView.findViewById<RelativeLayout>(R.id.flMainVideo)

        //        val imgShadow: ImageView = itemView.findViewById<ImageView>(R.id.imgShadow)
        val img_playback: ImageView = itemView.findViewById<ImageView>(R.id.img_playback)
        val img_vol: ImageView = itemView.findViewById<ImageView>(R.id.img_vol)

        val ivInfo: ImageView = itemView.findViewById<ImageView>(R.id.ivInfo)
//        val ivInfo: ZoomageView = itemView.findViewById<ZoomageView>(R.id.ivInfo)

//        override fun videoStarted() {
//            super.videoStarted()
//            img_playback.setImageResource(R.drawable.pause)
//            if (isMuted) {
//                muteVideo()
//                img_vol.setImageResource(R.drawable.ic_mute)
//            } else {
//                unmuteVideo()
//                img_vol.setImageResource(R.drawable.ic_unmute)
//            }
//        }
//
//        override fun pauseVideo() {
//            super.pauseVideo()
//            img_playback.setImageResource(R.drawable.play)
//        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (all_post!![position].type) {
            "image" -> VIEW_TYPE_IMAGE
            else -> VIEW_TYPE_VIDEO
        }
    }

    override fun onBindViewHolder(holder: AAH_CustomViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val postContent = all_post[position]

//        if (all_post[position].type == "image") {
        if (holder.itemViewType == VIEW_TYPE_IMAGE) {

            var holderPhoto = PhotoViewHolder(holder.itemView)

            //-------------------------Add New-----------------------
            ZoomHelper.addZoomableView(holderPhoto.ivInfo, postContent)


            if (position == 0) {
                width = postContent!!.width
                height = postContent!!.height
            }

            //---------------Add New-------------------
            showLog("rlMainPhoto", width.toString() + " : " + ScreenUtils.getScreenWidth(context))
//            if (width < ScreenUtils.getScreenWidth(context)) {
            if (width > 0) {
                holderPhoto.rlMainPhoto.layoutParams.height = (ScreenUtils.getScreenWidth(context) * height) / width
            }
            holderPhoto.rlMainPhoto.layoutParams.width = ScreenUtils.getScreenWidth(context)
//            } else {
//                holderPhoto.rlMainPhoto.layoutParams.height =  (ScreenUtils.getScreenWidth(context) * height) / width
//                holderPhoto.rlMainPhoto.layoutParams.width = ScreenUtils.getScreenWidth(context)
//            }

            val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)

            Glide.with(context)
                .load(postContent!!.content)
                //.skipMemoryCache(true)
                .thumbnail(0.01f)
//                .thumbnail(Glide.with(context).load(postContent.thumb))
//                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(holderPhoto.ivInfo)

            //Set Size of all cell as per first View
//            Glide.with(context).asBitmap()
//                .load(postContent!!.content)
//
//                .thumbnail(0.01f)
//                .transition(DrawableTransitionOptions.withCrossFade())
//
//
//                //                .thumbnail(0.25f)
////                .transform(GlideBlurTransformation(context))
////                .centerCrop()
////                .apply(RequestOptions.skipMemoryCacheOf(!isCaching))
////                .apply(RequestOptions.diskCacheStrategyOf(if (isCaching) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
////                .error(R.drawable.gallery_post)
//                .into(object : CustomTarget<Bitmap?>() {
//                    override fun onLoadCleared(placeholder: Drawable?) {
////                        layoutBinding.imgView.visibility = View.VISIBLE
//                    }
//
//                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap?>?) {
//                        try {
////                            holderPhoto.img.visibility = View.GONE
//                            holderPhoto.ivInfo.setImageBitmap(bitmap)
//
//                            val pxl = bitmap.getPixel(500, 500)
//                            holderPhoto.imgShadow.setColorFilter(pxl)
//
//                        } catch (ignore: Exception) {
//                        }
//                    }
//                })

        } else {
            //video
            var holderVideo = VideoViewHolder(holder.itemView)

            if (position == 0) {
                width = postContent!!.width
                height = postContent!!.height
            }

            //---------------Add New-------------------
            showLog("rlMainPhoto", width.toString() + " : " + ScreenUtils.getScreenWidth(context))
//            if (width < ScreenUtils.getScreenWidth(context)) {
            if (width > 0) {
                holderVideo.flMainVideo.layoutParams.height = (ScreenUtils.getScreenWidth(context) * height) / width
            }
            holderVideo.flMainVideo.layoutParams.width = ScreenUtils.getScreenWidth(context)
//            } else {
//                holderVideo.flMainVideo.layoutParams.height = height
//                holderVideo.flMainVideo.layoutParams.width = width
//            }

            showLog("width/height", holderVideo.flMainVideo.layoutParams.width.toString() + " : " + holderVideo.flMainVideo.layoutParams.height)
            //todo
//            holder.imageUrl = postContent.thumb
//            holder.videoUrl = postContent.content

            //load image into imageview
            if (postContent.thumb.isNotEmpty()) {
//                picasso.load(holder.imageUrl).config(Bitmap.Config.RGB_565).into(holder.aaH_ImageView)
//                Glide.with(context).load(holder.imageUrl).into(holder.aaH_ImageView)

                //Glide.with(context).load(postContent.thumb).into(holderVideo.ivInfo)


                val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)

                Glide.with(context)
                    .load(postContent!!.content)
                    //.skipMemoryCache(true)
                    .thumbnail(0.01f)
//                .thumbnail(Glide.with(context).load(postContent.thumb))
//                .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(requestOptions)
                    .into(holderVideo.ivInfo)


//                Glide.with(context).asBitmap()
//                    .load(postContent.thumb)
//                    //                .thumbnail(0.25f)
////                    .centerCrop()
//                    .apply(RequestOptions.skipMemoryCacheOf(!isCaching))
//                    .apply(RequestOptions.diskCacheStrategyOf(if (isCaching) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
//                    .error(R.drawable.gallery_post)
//                    .into(object : CustomTarget<Bitmap?>() {
//                        override fun onLoadCleared(placeholder: Drawable?) {
////                        layoutBinding.imgView.visibility = View.VISIBLE
//                        }
//
//                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap?>?) {
//                            try {
////                                holderVideo.aaH_ImageView
//
////                                holder.imgDefault.visibility = View.GONE
//                                holderVideo.ivInfo.setImageBitmap(bitmap)
//
////                                val pxl = bitmap.getPixel(500, 500)
////                                holderVideo.imgShadow.setColorFilter(pxl)
//                            } catch (ignore: Exception) {
//                            }
//                        }
//                    })
            }

            holder.isLooping = false //optional - true by default
//
//            //to play pause videos manually (optional)
//            holderVideo.img_playback.setOnClickListener(View.OnClickListener {
//                if (holder.isPlaying) {
//                    holder.pauseVideo()
//                    holder.isPaused = true
//                } else {
//                    holder.playVideo()
//                    holder.isPaused = false
//                }
//            })

//            to mute/un-mute video (optional)
//            holderVideo.img_vol.setOnClickListener(View.OnClickListener {
//                if (holderVideo.isMuted) {
//                    holder.unmuteVideo()
//                    holderVideo.img_vol.setImageResource(R.drawable.ic_unmute)
//                } else {
//                    holder.muteVideo()
//                    holderVideo.img_vol.setImageResource(R.drawable.ic_mute)
//                }
//                holderVideo.isMuted = !holderVideo.isMuted
//            })

//            if (postContent.content == null) {
//                holderVideo.img_vol.visibility = View.GONE
//                holderVideo.img_playback.visibility = View.GONE
//            } else {
//                holderVideo.img_vol.visibility = View.VISIBLE
//                holderVideo.img_playback.visibility = View.VISIBLE
//            }

            holderVideo.flMainVideo.setOnClickListener {
                //Open Video Dialog
//                showDialog(context, postContent.content)


//                val filePath: String = Environment.getExternalStorageDirectory().toString().plus(File.separator.toString()) +
//                          "registration.mp4"
//                Log.e("filepath", filePath)
//                showDialog(context, filePath)

                initializePlayer(postContent.content)
            }
        }
    }

    private fun initializePlayer(videoUrl: String) {
        try {
            if (dialog != null) {
                dialog!!.dismiss()
                dialog = null
            }

            dialog = Dialog(context)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.setContentView(R.layout.dialog_video_new)
            dialog!!.setCanceledOnTouchOutside(true)

            var playerView: PlayerView = dialog!!.findViewById(R.id.video_view)
            var imgFullScreen: ImageView = dialog!!.findViewById(R.id.imgFullScreen)

            val player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                context,
                DefaultRenderersFactory(context),
                DefaultTrackSelector(AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())),
                DefaultLoadControl()
            )

//            val filePath = Environment.getExternalStorageDirectory().toString() + File.separator +
////                    "video" + File.separator +
//                    "registration.mp4"
//            Log.e("filepath", filePath)

//            val uri = Uri.parse(filePath)
            val uri = Uri.parse(videoUrl)

            val audioSource = ExtractorMediaSource(
                uri,
                DefaultDataSourceFactory(context, "MyExoplayer"),
                DefaultExtractorsFactory(),
                null,
                null
            )
            player.prepare(audioSource)
            playerView.player = player
            player.playWhenReady = true

            imgFullScreen.setOnClickListener {
                var orientation = context.resources.configuration.orientation
                when (orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    Configuration.ORIENTATION_LANDSCAPE -> context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }

            dialog!!.setOnDismissListener {
                if (player != null)
                    player!!.release()

                //stop player
                var orientation = context.resources.configuration.orientation
                when (orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }

            if (dialog != null && !dialog!!.isShowing) {
                dialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var dialog: Dialog? = null

    private fun showDialog(context: Activity, videoUrl: String) {
        try {
            if (dialog != null) {
                dialog!!.dismiss()
                dialog = null
            }

            dialog = Dialog(context)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.setContentView(R.layout.dialog_video)
            dialog!!.setCanceledOnTouchOutside(true)

            var simpleExoPlayerView: SimpleExoPlayerView = dialog!!.findViewById(R.id.simpleExoPlayerView)
            var imgFullScreen: ImageView = dialog!!.findViewById(R.id.imgFullScreen)

            setUpPlayer(simpleExoPlayerView, videoUrl)

            imgFullScreen.setOnClickListener {
                var orientation = context.resources.configuration.orientation
                when (orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    Configuration.ORIENTATION_LANDSCAPE -> context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }

            dialog!!.setOnDismissListener {
                if (simpleExoPlayerView.player != null)
                    simpleExoPlayerView.player!!.release()

                //stop player
                var orientation = context.resources.configuration.orientation
                when (orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }

            if (dialog != null && !dialog!!.isShowing) {
                dialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpPlayer(simpleExoPlayerView: SimpleExoPlayerView, videoUrl: String) {
        try {
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()

            // track selector is used to navigate between
            // video using a default seekbar.
            val trackSelector: TrackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))

            // we are adding our track selector to exoplayer.
            var exoPlayer: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

            val videouri: Uri = Uri.parse(videoUrl)

            // we are creating a variable for datasource factory
            // and setting its user agent as 'exoplayer_view'
            val dataSourceFactory = DefaultHttpDataSourceFactory("exoplayer_video")

            // we are creating a variable for extractor factory
            // and setting it to default extractor factory.
            val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()

            // we are creating a media source with above variables
            // and passing our event handler as null,
            val mediaSource: MediaSource = ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null)

            simpleExoPlayerView.player = exoPlayer

            exoPlayer.prepare(mediaSource)
            exoPlayer.playWhenReady = true

        } catch (e: java.lang.Exception) {
            Log.e("TAG", "Error : $e")
        }
    }

    fun getdata(): ArrayList<All_Post_Data.PostContent> {
        return all_post
    }

    fun clear() {
        this.all_post.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = all_post!!.size

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
            return mOldPostList[oldItemPosition].id === mNewPostList[newItemPosition].id
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

    override fun onZoomStateChanged(zoomHelper: ZoomHelper, zoomableView: View, isZooming: Boolean) {
        if (isZooming) {
            val model = ZoomHelper.getZoomableViewTag(zoomableView) as All_Post_Data.PostContent
            //Toast.makeText(zoomableView.context, model.content + "'s post started zooming!", Toast.LENGTH_SHORT).show()
        }
    }
}
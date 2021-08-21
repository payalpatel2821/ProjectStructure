package com.task.newapp.adapter.post

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.jsibbold.zoomage.ZoomageView
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.task.newapp.R
import com.task.newapp.models.post.Post_Uri_Model
import com.task.newapp.utils.isImageFile
import java.io.File
import java.util.*

class PostPagerAdapter(context: Activity, images: ArrayList<LocalMedia>) : PagerAdapter() {

    var mContext: Activity
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
        val media: LocalMedia = arrayList!![position]

//        if (media == null || TextUtils.isEmpty(media.path)) {
//            return
//        }

        val itemView: View = mLayoutInflater!!.inflate(R.layout.item_post_pager, container, false)
        val imageView: ZoomageView = itemView.findViewById<ZoomageView>(R.id.imageViewMain) as ZoomageView
        val imgPlay: ImageView = itemView.findViewById<View>(R.id.imgPlay) as ImageView

//        val path: String = if (media.isCut && !media.isCompressed) {
//            // Cropped
//            media.cutPath
//        } else if (media.isCompressed || media.isCut && media.isCompressed) {
//            // Compressed, or cropped and compressed at the same time, the final compressed image shall prevail
//            media.compressPath
//        } else if (PictureMimeType.isHasVideo(media.mimeType) && !TextUtils.isEmpty(media.coverPath)) {
//            media.coverPath
//        } else {
//            // Original image
//            media.path
//        }

        val path = media.path

        Glide.with(mContext!!).load(
//            if (PictureMimeType.isContent(path) && !media.isCut && !media.isCompressed) Uri.parse(path) else path
            path
        ).into(imageView)

        if (isImageFile(path)) imgPlay.visibility = View.GONE
        else imgPlay.visibility = View.VISIBLE

        Objects.requireNonNull(container).addView(itemView)

        imgPlay.setOnClickListener {
            //Open Video Dialog
//            var videoUrl = Uri.fromFile(File(path)).toString()
//            showDialog(mContext, videoUrl)
            initializePlayer(path)
        }

        return itemView
    }

    private fun initializePlayer(videoUrl: String) {
        try {
            if (dialog != null) {
                dialog!!.dismiss()
                dialog = null
            }

            dialog = Dialog(mContext)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.setContentView(R.layout.dialog_video_new)
            dialog!!.setCanceledOnTouchOutside(true)

            var playerView: PlayerView = dialog!!.findViewById(R.id.video_view)
            var imgFullScreen: ImageView = dialog!!.findViewById(R.id.imgFullScreen)

            val player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                mContext,
                DefaultRenderersFactory(mContext),
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
                DefaultDataSourceFactory(mContext, "MyExoplayer"),
                DefaultExtractorsFactory(),
                null,
                null
            )
            player.prepare(audioSource)
            playerView.player = player
            player.playWhenReady = true

            imgFullScreen.setOnClickListener {
                var orientation = mContext.resources.configuration.orientation
                when (orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> mContext.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    Configuration.ORIENTATION_LANDSCAPE -> mContext.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }

            dialog!!.setOnDismissListener {
                if (player != null)
                    player!!.release()

                //stop player
                var orientation = mContext.resources.configuration.orientation
                when (orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> mContext.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }

            if (dialog != null && !dialog!!.isShowing) {
                dialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
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

    fun setUpPlayer(simpleExoPlayerView: SimpleExoPlayerView, videoUrl: String) {
        try {
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()

            // track selector is used to navigate between
            // video using a default seekbar.
            val trackSelector: TrackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))

            // we are adding our track selector to exoplayer.
            var exoPlayer: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector)

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

}
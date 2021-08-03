package com.task.newapp.ui.activities.post

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityShowPostBinding
import com.task.newapp.databinding.FragmentPostBinding
import com.task.newapp.models.post.PostSocket
import com.task.newapp.models.post.ResponseGetAllPost
import com.task.newapp.utils.Constants
import com.task.newapp.utils.exoplayer.MediaRecyclerAdapter
import com.task.newapp.utils.hideProgressDialog
import com.task.newapp.utils.openProgressDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class ShowPostActivity : AppCompatActivity() {

    companion object {
        lateinit var activity: ShowPostActivity
    }

    private lateinit var binding: ActivityShowPostBinding
    private lateinit var mAdapter: MediaRecyclerAdapter
    var postId = 0
    var position = 0
    var postByMe = 0
    private val mCompositeDisposable = CompositeDisposable()
    private var arrayListContent: ArrayList<ResponseGetAllPost.All_Post_Data.PostContent> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_post)
        activity = this

        if (intent != null) {
            postId = intent.getIntExtra("postId", 0)
            position = intent.getIntExtra("position", -1)
            postByMe = intent.getIntExtra("postByMe", 0)

            val type: Type = object : TypeToken<ArrayList<ResponseGetAllPost.All_Post_Data.PostContent>>() {}.type
            arrayListContent = Gson().fromJson(intent.getStringExtra("content"), type)
            Log.e("ShowPostActivity: ", arrayListContent.size.toString())

            initView()
        }
    }

    private fun initView() {
        binding.exoPlayerRecyclerView.layoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.exoPlayerRecyclerView)
        binding.exoPlayerRecyclerView.setOnTouchListener(View.OnTouchListener { v, event ->
//            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
            false
        })

        binding.exoPlayerRecyclerView.setMediaObjects(arrayListContent)
        mAdapter = MediaRecyclerAdapter(activity, arrayListContent, initGlide()!!)
        binding.exoPlayerRecyclerView.adapter = mAdapter
    }

    private fun initGlide(): RequestManager? {
        val options = RequestOptions()
        return Glide.with(applicationContext)
            .setDefaultRequestOptions(options)
    }

    //    protected void onResume() {
    //        super.onResume();
    //        ZoomHelper instance = ZoomHelper.Companion.getInstance();
    //        instance.addOnZoomStateChangedListener(mAdapter);
    //    }

    override fun onPause() {
        super.onPause()
        if (binding.exoPlayerRecyclerView != null) {
            binding.exoPlayerRecyclerView.onPausePlayer()
        }
//        shimmerFrameLayout.stopShimmerAnimation()

//        ZoomHelper var10000 = ZoomHelper.Companion.getInstance();
//        var10000.removeOnZoomStateChangedListener(mAdapter);
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding.exoPlayerRecyclerView != null) {
            binding.exoPlayerRecyclerView.releasePlayer()
        }
    }

    fun playVideo(playVideo: Boolean) {
        binding.exoPlayerRecyclerView.playVideo(playVideo)
    }
}
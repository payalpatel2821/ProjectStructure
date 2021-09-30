package com.task.newapp.ui.activities.profile

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.paginate.Paginate
import com.task.newapp.R
import com.task.newapp.adapter.profile.PostListAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityPostListBinding
import com.task.newapp.models.ResponsePostList
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class PostListActivity : AppCompatActivity(), Paginate.Callbacks {

    private val allpost: ArrayList<ResponsePostList.Data> = ArrayList<ResponsePostList.Data>()
    private var userId: Int = 0
    lateinit var binding: ActivityPostListBinding
    private var paginate: Paginate? = null
    private lateinit var postListAdapter: PostListAdapter
    var isloading = false
    var hasLoadedAllItems = false
    lateinit var type: String
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_list)
        initView()
    }

    private fun initView() {
        setData()
        setAdapter()
    }

    private fun setData() {
        binding.toolbarLayout.txtTitle.text = resources.getString(intent.getIntExtra(Constants.title, 0))
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        userId = intent.getIntExtra(Constants.user_id, 0)
        type = intent.getStringExtra(Constants.type)!!
    }

    private fun setAdapter() {
        // If RecyclerView was recently bound, unbind
        if (paginate != null) {
            paginate!!.unbind()
        }

        postListAdapter = PostListAdapter(applicationContext, ArrayList())
        val layoutManager = GridLayoutManager(applicationContext, 3)
        binding.rvPostlist.layoutManager = layoutManager
        binding.rvPostlist.adapter = postListAdapter

        initPaginate()

        postListAdapter.onItemClick = { dataset, position ->
            showToast("Item Click")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initPaginate() {
        paginate = Paginate.with(binding.rvPostlist, this)
            .setLoadingTriggerThreshold(17)
            .addLoadingListItem(false)
            .setLoadingListItemCreator(null)
            .build()
    }

    override fun onLoadMore() {
        isloading = true
        val scrollPosition: Int = postListAdapter.itemCount
        callAPIGetPostList(type, resources.getString(R.string.limit_20).toInt(), scrollPosition)
    }

    override fun isLoading(): Boolean {
        return isloading
    }

    override fun hasLoadedAllItems(): Boolean {
        return hasLoadedAllItems
    }

    private fun callAPIGetPostList(type: String, limit: Int, offset: Int) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.type to type,
                Constants.id to userId,
                Constants.limit to limit,
                Constants.offset to offset,
            )

            openProgressDialog(this)
            mCompositeDisposable.add(
                ApiClient.create()
                    .setProfileGroupPageFriendPost(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponsePostList>() {
                        override fun onNext(responsePostList: ResponsePostList) {
                            if (responsePostList.success == 1) {
                                allpost.addAll(responsePostList.data)
                                postListAdapter.setData(responsePostList.data as ArrayList<ResponsePostList.Data>)
                                isloading = false
                                hasLoadedAllItems = false
                            } else {
                                hasLoadedAllItems = true
                                if (allpost.isEmpty()) {
                                    binding.llEmptyPost.visibility = VISIBLE
                                } else {
                                    binding.llEmptyPost.visibility = GONE
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            hideProgressDialog()
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }
}
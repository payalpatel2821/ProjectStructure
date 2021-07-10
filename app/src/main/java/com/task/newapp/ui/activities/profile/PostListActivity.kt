package com.task.newapp.ui.activities.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.paginate.Paginate
import com.task.newapp.R
import com.task.newapp.adapter.PostListAdapter
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
        binding.txtTitle.text = resources.getString(intent.getIntExtra(Constants.title, 0))
        userId = intent.getIntExtra(Constants.user_id, 0)
        type = intent.getStringExtra(Constants.type)!!
    }

    private fun setAdapter() {
        // If RecyclerView was recently bound, unbind
        if (paginate != null) {
            paginate!!.unbind()
        }

        postListAdapter = PostListAdapter(applicationContext, ArrayList(), this@PostListActivity)
        val layoutManager = GridLayoutManager(applicationContext, 3)
        binding.rvPostlist.layoutManager = layoutManager
        binding.rvPostlist.adapter = postListAdapter

        initPaginate()

        postListAdapter.onItemClick = { dataset, position ->
            showToast("Item Click")
        }
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
        callAPIGetPostList(type, 20, scrollPosition)
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
                                postListAdapter.setData(responsePostList.data as ArrayList<ResponsePostList.Data>)
                                isloading = false
                                hasLoadedAllItems = false
                            } else {
                                hasLoadedAllItems = true
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

}
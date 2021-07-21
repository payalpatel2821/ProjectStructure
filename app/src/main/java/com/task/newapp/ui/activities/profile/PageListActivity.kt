package com.task.newapp.ui.activities.profile

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.paginate.Paginate
import com.task.newapp.R
import com.task.newapp.adapter.profile.PageListAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityPageListBinding
import com.task.newapp.models.ResponsePageList
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class PageListActivity : AppCompatActivity(), Paginate.Callbacks {

    private lateinit var pageListAdapter: PageListAdapter
    lateinit var binding: ActivityPageListBinding
    var isloading = false
    var hasLoadedAllItems = false
    private var paginate: Paginate? = null
    lateinit var flag: String
    var limit: Int = 20
    private var userID: Int = 0
    private val mCompositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_page_list)
        setData()

    }

    private fun setData() {
        flag = intent?.getStringExtra(Constants.type).toString()

        if (flag == resources.getString(R.string.pages)) {
            binding.segmentText.visibility = GONE
            binding.txtTitle.visibility = VISIBLE
            userID = intent.getIntExtra(Constants.user_id, 0)
        } else {
            binding.segmentText.visibility = VISIBLE
            binding.txtTitle.visibility = GONE
        }

        binding.segmentText.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.rb_followed_pages -> {
                    flag = resources.getString(R.string.followed_page)
                    isloading = false
                    hasLoadedAllItems = false
                    setAdapter()
                }
                R.id.rb_my_pages -> {
                    flag = resources.getString(R.string.my_pages)
                    isloading = false
                    hasLoadedAllItems = false
                    setAdapter()
                }
            }
        }

    }

    fun onClick(view: View) {
        when (view!!.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
        }
    }

    override fun onResume() {
        setAdapter()
        super.onResume()
    }

    private fun callAPIPages(hashMap: HashMap<String, Any>) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            mCompositeDisposable.add(
                ApiClient.create()
                    .getPageList(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponsePageList>() {
                        override fun onNext(loginResponse: ResponsePageList) {
                            if (loginResponse.success == 1) {
                                pageListAdapter.setData(loginResponse.data as ArrayList<ResponsePageList.Data>)
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

    private fun setAdapter() {
        // If RecyclerView was recently bound, unbind
        if (paginate != null) {
            paginate!!.unbind()
        }

        pageListAdapter = PageListAdapter(applicationContext, ArrayList())
        val layoutManager = GridLayoutManager(applicationContext, 3)
        binding.rvPagelist.layoutManager = layoutManager
        binding.rvPagelist.adapter = pageListAdapter

        initPaginate()

    }

    private fun initPaginate() {
        paginate = Paginate.with(binding.rvPagelist, this)
            .setLoadingTriggerThreshold(17)
            .addLoadingListItem(false)
            .setLoadingListItemCreator(null)
            .build()
    }

    override fun onLoadMore() {
        isloading = true
        val scrollPosition: Int = pageListAdapter.itemCount
        showLog("loadmore", scrollPosition.toString())
        when (flag) {
            resources.getString(R.string.pages) -> {
                val hashMap: HashMap<String, Any> = hashMapOf(
                    Constants.limit to limit,
                    Constants.offset to scrollPosition,
                    Constants.type to Constants.page,
                    Constants.id to userID
                )
                callAPIPages(hashMap)
            }
            resources.getString(R.string.followed_page) -> {
                val hashMap: HashMap<String, Any> = hashMapOf(
                    Constants.limit to limit,
                    Constants.offset to scrollPosition,
                    Constants.type to Constants.page,
                    Constants.follow to 1,
                    Constants.id to userID
                )
                callAPIPages(hashMap)
            }
            resources.getString(R.string.my_pages) -> {
                val hashMap: HashMap<String, Any> = hashMapOf(
                    Constants.limit to limit,
                    Constants.offset to scrollPosition,
                    Constants.type to Constants.page,
                    Constants.follow to 0,
                    Constants.id to userID
                )
                callAPIPages(hashMap)
            }
        }
    }

    override fun isLoading(): Boolean {
        return isloading
    }

    override fun hasLoadedAllItems(): Boolean {
        return hasLoadedAllItems
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }
}
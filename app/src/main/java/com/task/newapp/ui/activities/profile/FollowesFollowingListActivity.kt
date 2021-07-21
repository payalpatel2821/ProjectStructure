package com.task.newapp.ui.activities.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.paginate.Paginate
import com.task.newapp.R
import com.task.newapp.adapter.FollowerFollowingAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityFollowesFollowingListBinding
import com.task.newapp.models.ResponseFollowUnfollow
import com.task.newapp.models.ResponsePostList
import com.task.newapp.models.ResponseUserFollowingFollower
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class FollowesFollowingListActivity : AppCompatActivity(), Paginate.Callbacks {

    private lateinit var by: String
    private var titleName: Int = 0
    private lateinit var from: String
    private var User_ID: Int = 0
    private lateinit var followerfollowingadapter: FollowerFollowingAdapter
    lateinit var binding: ActivityFollowesFollowingListBinding
    private val mCompositeDisposable = CompositeDisposable()
    var isloading = false
    var hasLoadedAllItems = false
    private val isAPICallRunning = false
    private var paginate: Paginate? = null
    var change1: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_followes_following_list)
        initView()
    }

    private fun initView() {
        setData()
        openProgressDialog(this)
    }

    private fun setData() {
        from = intent.getStringExtra("From")!!
        by = intent.getStringExtra("By")!!
        titleName = intent.getIntExtra("Title", 0)
        binding.txtTitle.text = resources.getString(titleName)
        if (by == "Other") {
            User_ID = intent.getIntExtra(Constants.user_id, 0)
        }
    }

    private fun setAdapter() {
        // If RecyclerView was recently bound, unbind
        if (paginate != null) {
            paginate!!.unbind()
        }

        followerfollowingadapter = FollowerFollowingAdapter(applicationContext, ArrayList(), from, by, this@FollowesFollowingListActivity)
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.rvFollowlist.layoutManager = layoutManager
        binding.rvFollowlist.adapter = followerfollowingadapter

        initPaginate()

        followerfollowingadapter.onFollowItemClick = { f_uf, dataSet1, viewHolder, position, from ->
            callAPIFollowUnfollow(f_uf, dataSet1, viewHolder, position, from)
        }

        followerfollowingadapter.onItemClick = { myId, dataset, position ->
            if (myId == dataset[position].id) {
                applicationContext.launchActivity<MyProfileActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
//                val intent = Intent(this, MyProfileActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                resultLauncher.launch(intent)
            } else {
                applicationContext.launchActivity<OtherUserProfileActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.user_id, dataset[position].id)
                }
//                val intent = Intent(this, OtherUserProfileActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                intent.putExtra(Constants.user_id, dataset[position].id)
//                resultLauncher.launch(intent)
            }
        }
    }

    private fun initPaginate() {
        paginate = Paginate.with(binding.rvFollowlist, this)
            .setLoadingTriggerThreshold(17)
            .addLoadingListItem(false)
            .setLoadingListItemCreator(null)
            .build()
    }

    override fun onLoadMore() {
        isloading = true
        val scrollPosition: Int = followerfollowingadapter.itemCount
        showLog("loadmore", scrollPosition.toString())
        when (from) {
            "profile_view" -> {
                callAPIGetProfileViewer(20, scrollPosition)
            }
            "friend" -> {
                callAPIGetFriendList(from, 20, scrollPosition)
            }
            else -> {
                callAPIGetFollower(from, 20, scrollPosition)
            }
        }
    }

    override fun isLoading(): Boolean {
        return isloading
    }

    override fun hasLoadedAllItems(): Boolean {
        return hasLoadedAllItems
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
        }
    }

    private fun callAPIGetProfileViewer(limit: Int, offset: Int) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.limit to limit,
                Constants.offset to offset,
            )

//            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .getUserProfileViwer(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseUserFollowingFollower>() {
                        override fun onNext(loginResponse: ResponseUserFollowingFollower) {
                            if (loginResponse.success == 1) {
                                followerfollowingadapter.setData(loginResponse.data as ArrayList<ResponseUserFollowingFollower.Data>)
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

    private fun callAPIGetFollower(type: String, limit: Int, offset: Int) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            val hashMap: HashMap<String, Any>
            if (by == "Other") {
                hashMap = hashMapOf(
                    Constants.type to type,
                    Constants.limit to limit,
                    Constants.offset to offset,
                    Constants.user_id to User_ID,
                )
            } else {
                hashMap = hashMapOf(
                    Constants.type to type,
                    Constants.limit to limit,
                    Constants.offset to offset,
                )
            }

            mCompositeDisposable.add(
                ApiClient.create()
                    .getUserFollowerFollowing(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseUserFollowingFollower>() {
                        override fun onNext(loginResponse: ResponseUserFollowingFollower) {
                            if (loginResponse.success == 1) {
                                followerfollowingadapter.setData(loginResponse.data as ArrayList<ResponseUserFollowingFollower.Data>)
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

    private fun callAPIFollowUnfollow(f_uf: String, dataSet1: ResponseUserFollowingFollower.Data, viewHolder: FollowerFollowingAdapter.ViewHolder, position: Int, from: String) {
        if (!applicationContext.isNetworkConnected()) {
            applicationContext.showToast(applicationContext.getString(R.string.no_internet))
            return
        }
        try {

            openProgressDialog(this)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.type to f_uf,
                Constants.follow_id to dataSet1.id,
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .setUserFollowUnfollow(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseFollowUnfollow>() {
                        override fun onNext(loginResponse: ResponseFollowUnfollow) {
                            if (loginResponse.success != 1) {
                                applicationContext.showToast(loginResponse.message)
                            } else if (loginResponse.success == 1) {
                                change1 = 1
                                if (f_uf == "follow") {
                                    followerfollowingadapter.setUnFollowText(viewHolder, dataSet1)
                                } else {
                                    if (from == "my") {
                                        followerfollowingadapter.updateData(position)
                                    } else {
                                        followerfollowingadapter.setFollowText(viewHolder)
                                        dataSet1.isFollow = 0
                                    }
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

    private fun callAPIGetFriendList(type: String, limit: Int, offset: Int) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.type to type,
                Constants.id to User_ID,
                Constants.limit to limit,
                Constants.offset to offset,
            )

            openProgressDialog(this)
            mCompositeDisposable.add(
                ApiClient.create()
                    .getFriendList(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseUserFollowingFollower>() {
                        override fun onNext(loginResponse: ResponseUserFollowingFollower) {
                            if (loginResponse.success == 1) {
                                followerfollowingadapter.setData(loginResponse.data as ArrayList<ResponseUserFollowingFollower.Data>)
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


    override fun onBackPressed() {
        var intent = Intent().putExtra("change", change1)
        setResult(RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }

    override fun onResume() {
        isloading = false
        hasLoadedAllItems = false
        setAdapter()
        super.onResume()
    }
}
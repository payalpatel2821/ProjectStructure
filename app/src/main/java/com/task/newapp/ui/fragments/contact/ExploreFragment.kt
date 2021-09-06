package com.task.newapp.ui.fragments.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.paginate.Paginate
import com.task.newapp.R
import com.task.newapp.adapter.contact.ExploreAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentExploreBinding
import com.task.newapp.models.ResponseIsAppUser
import com.task.newapp.utils.Constants
import com.task.newapp.utils.hideProgressDialog
import com.task.newapp.utils.isNetworkConnected
import com.task.newapp.utils.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class ExploreFragment : Fragment(), Paginate.Callbacks {

    private var _binding: FragmentExploreBinding? = null
    lateinit var linearLayoutManager: LinearLayoutManager
    private val binding get() = _binding!!
    private lateinit var adapter: ExploreAdapter
    private val mCompositeDisposable = CompositeDisposable()
    private val allUser: ArrayList<ResponseIsAppUser.Data> = ArrayList<ResponseIsAppUser.Data>()
    var isloading = false
    var hasLoadedAllItems = false
    private var paginate: Paginate? = null
    var query: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        linearLayoutManager = LinearLayoutManager(requireActivity())
        _binding!!.rvList.layoutManager = linearLayoutManager
        adapter = ExploreAdapter(requireActivity())
        _binding!!.rvList.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun filter(query: String) {
        initPaginate()
        this.query = query
        callAPISearchUser(query, 0)
    }

    private fun callAPISearchUser(query: String, offset: Int) {
        if (!requireActivity().isNetworkConnected()) {
            requireActivity().showToast(getString(R.string.no_internet))
            return
        }
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.term to query,
                Constants.limit to R.string.limit_20,
                Constants.offset to offset,
            )
            mCompositeDisposable.add(
                ApiClient.create()
                    .searchAppUser(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseIsAppUser>() {
                        override fun onNext(responseIsAppUser: ResponseIsAppUser) {
                            if (responseIsAppUser.success == 1) {
                                allUser.addAll(responseIsAppUser.data)
                                adapter.setData(responseIsAppUser.data as ArrayList<ResponseIsAppUser.Data>)
                                isloading = false
                                hasLoadedAllItems = false
                            } else {
                                hasLoadedAllItems = true
//                                if (allpost.isEmpty()) {
//                                    binding.llEmptyPost.visibility = View.VISIBLE
//                                } else {
//                                    binding.llEmptyPost.visibility = View.GONE
//                                }
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

    private fun initPaginate() {
        paginate = Paginate.with(_binding!!.rvList, this)
            .setLoadingTriggerThreshold(17)
            .addLoadingListItem(false)
            .setLoadingListItemCreator(null)
            .build()
    }

    override fun onLoadMore() {
        isloading = true
        val scrollPosition: Int = adapter.itemCount
        callAPISearchUser(query, scrollPosition)
    }

    override fun isLoading(): Boolean {
        return isloading
    }

    override fun hasLoadedAllItems(): Boolean {
        return hasLoadedAllItems
    }
}
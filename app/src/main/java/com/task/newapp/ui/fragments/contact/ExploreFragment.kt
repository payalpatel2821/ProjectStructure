package com.task.newapp.ui.fragments.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.task.newapp.R
import com.task.newapp.adapter.contact.ExploreAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentExploreBinding
import com.task.newapp.models.ResponseIsAppUser
import com.task.newapp.realmDB.deleteContactHistory
import com.task.newapp.realmDB.deleteOneContactHistory
import com.task.newapp.realmDB.getAllContactHistory
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class ExploreFragment : Fragment(), View.OnClickListener {

    private lateinit var disposable: DisposableObserver<ResponseIsAppUser>
    private var _binding: FragmentExploreBinding? = null
    lateinit var linearLayoutManager: LinearLayoutManager
    private val binding get() = _binding!!
    private lateinit var adapter: ExploreAdapter
    private val mCompositeDisposable = CompositeDisposable()
    private var allUser: ArrayList<ResponseIsAppUser.Data> = ArrayList<ResponseIsAppUser.Data>()
    var query: String = ""

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
        adapter = ExploreAdapter(requireActivity(), object : ExploreAdapter.OnExploreContactListener {
            override fun onRemoveItemClick(contactId: Int, position: Int) {
                deleteOneContactHistory(contactId)
                allUser.removeAt(position)
                adapter.setData(allUser, true)
                adapter.notifyDataSetChanged()
                setClearBtnVisibility()
            }
        })
        _binding!!.rvList.adapter = adapter
        getAllContactHistoryFromDB()
        binding.txtClearAll.setOnClickListener(this)
    }

    private fun getAllContactHistoryFromDB() {
        allUser.clear()
        getAllContactHistory().forEach {
            val contactHistory: ResponseIsAppUser.Data = ResponseIsAppUser.Data()
            contactHistory.id = it.id
            contactHistory.firstName = it.firstName
            contactHistory.lastName = it.lastName
            contactHistory.accountId = it.accountId
            contactHistory.profileImage = it.profileImage
            contactHistory.profileColor = it.profileColor
            contactHistory.isVisible = it.isVisible
            contactHistory.email = it.email
            contactHistory.mobile = it.mobile
            contactHistory.about = it.about
            allUser.add(contactHistory)
        }
        adapter.setData(allUser, true)

        setClearBtnVisibility()
    }

    fun setClearBtnVisibility() {
        binding.txtEmptyContact.text = resources.getString(R.string.no_recent_searches)
        if (allUser.isNullOrEmpty()) {
            binding.txtClearAll.visibility = GONE
            binding.txtEmptyContact.visibility = VISIBLE
        } else {
            binding.txtClearAll.visibility = VISIBLE
            binding.txtEmptyContact.visibility = GONE
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.txt_clear_all -> {
                deleteContactHistory()
                allUser.clear()
                adapter.setData(allUser, true)
                setClearBtnVisibility()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun filter(query: String) {
        try {
            allUser.clear()
            if (query == "") {
                binding.txtClearAll.visibility = VISIBLE
                getAllContactHistoryFromDB()
            } else {
                binding.txtClearAll.visibility = GONE
                this.query = query
                showLog("filter", "call")
                callAPISearchUser(query, 0)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun callAPISearchUser(query: String, offset: Int) {
        if (!requireActivity().isNetworkConnected()) {
            requireActivity().showToast(getString(R.string.no_internet))
            return
        }
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.term to query,
            )
            mCompositeDisposable.add(
                ApiClient.create()
                    .searchAppUser(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseIsAppUser>() {
                        override fun onNext(responseIsAppUser: ResponseIsAppUser) {
                            allUser.addAll(responseIsAppUser.data)
                            adapter.setData(allUser, false)
                            binding.txtEmptyContact.text = resources.getString(R.string.contact_not_found)
                            if (allUser.isNullOrEmpty()) {
                                binding.txtEmptyContact.visibility = VISIBLE
                            } else {
                                binding.txtEmptyContact.visibility = GONE
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

}
package com.task.newapp.ui.fragments.post

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paginate.Paginate
import com.task.newapp.R
import com.task.newapp.adapter.post.PostTagListAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentPostTagListBinding
import com.task.newapp.models.post.ResponseFriendsList
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList


class PostTagFriendListFragment : BottomSheetDialogFragment(), View.OnClickListener, Paginate.Callbacks,
    SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentPostTagListBinding
    private lateinit var fragmentActivity: FragmentActivity
    private val mCompositeDisposable = CompositeDisposable()
    private var paginate: Paginate? = null
    var isloading = false
    var hasLoadedAllItems = false
    private var isAPICallRunning = false
    private lateinit var postTagListAdapter: PostTagListAdapter
    var onPostTagDoneClickListener: OnPostTagDoneClickListener? = null
    private lateinit var allfriendList: ArrayList<ResponseFriendsList.Data>
    private lateinit var commaSeperatedIds: String
    private lateinit var commaSeperatedNames: String
    private var searchtxt: String = ""

    fun newInstance(commaSeperatedIds: String): PostTagFriendListFragment {
        val f = PostTagFriendListFragment()
        val args = Bundle()
        args.putString("commaSeperatedIds", commaSeperatedIds)
        f.arguments = args
        Log.e("commaSeperatedIds: ", commaSeperatedIds)
        return f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commaSeperatedIds = requireArguments().getString("commaSeperatedIds").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_tag_list, container, false)
        this.fragmentActivity = requireActivity()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initPaging() {
        if (paginate != null) {
            paginate!!.unbind()
        }

        paginate = Paginate.with(binding.rvTagList, this)
            .setLoadingTriggerThreshold(17)
            .addLoadingListItem(false)
            .setLoadingListItemCreator(null)
            .build()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        handleUserExit()
    }

    private fun initView() {
        checkAndEnable(false)
        clickListerner()
        initSearchView()

        allfriendList = ArrayList()
        postTagListAdapter = PostTagListAdapter(fragmentActivity, allfriendList)
        binding.rvTagList.layoutManager = LinearLayoutManager(activity)
        binding.rvTagList.adapter = postTagListAdapter

        initPaging()

        openProgressDialog(activity)
        getFriendList(0, "")

        postTagListAdapter.onItemClick = { commaSeperatedNames, commaSeperatedIds ->
            binding.txtFriendList.text = commaSeperatedNames
            checkAndEnable(!commaSeperatedIds.isEmpty())

            if (commaSeperatedIds.isEmpty()) {
                this.commaSeperatedIds = ""

                checkAndEnable(false)
                binding.txtFriendList.visibility = View.GONE
            } else {
                this.commaSeperatedIds = commaSeperatedIds

                checkAndEnable(true)
                binding.txtFriendList.visibility = View.VISIBLE
            }
        }
    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextListener(this)
        binding.searchView.onActionViewExpanded()
        binding.searchView.clearFocus()

        binding.searchView.setOnCloseListener(SearchView.OnCloseListener {
//            issearch = false
            searchtxt = ""
            Log.e("search", "search text change")

            allfriendList.clear()
            if (!isAPICallRunning) getFriendList(0, "")
            false
        })
        binding.searchView.findViewById<View>(R.id.search_close_btn)
            .setOnClickListener(View.OnClickListener {
                Log.d("called", "this is called.")
                binding.searchView.isActivated = true
                binding.searchView.setQuery("", false)
                binding.searchView.isIconified = true
                binding.searchView.onActionViewExpanded()
                binding.searchView.clearFocus()
                binding.searchView.findViewById<View>(R.id.search_button).performClick()
            })
    }

    private fun clickListerner() {
        binding.imgBack.setOnClickListener(this)
        binding.btnDone.setOnClickListener(this)
    }

    private fun handleUserExit() {
        Toast.makeText(requireContext(), "TODO - SAVE data or similar", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgBack -> dismiss()
            R.id.btnDone -> {
                dismiss()
                onPostTagDoneClickListener!!.onPostTagDoneClick(commaSeperatedIds)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

    private fun getFriendList(currentSize: Int, searchText: String) {
        try {
            isAPICallRunning = true
            if (activity != null) {
                try {
                    //openProgressDialog(activity)

                    val hashMap: HashMap<String, Any> = hashMapOf(
                        Constants.flag to "tag_post",
                        Constants.term to searchText,
                        Constants.limit to requireActivity().getString(R.string.get_all_post),
                        Constants.offset to currentSize.toString()
                    )

                    mCompositeDisposable.add(
                        ApiClient.create()
                            .search_contacts(hashMap)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableObserver<ResponseFriendsList>() {
                                override fun onNext(responseFriendsList: ResponseFriendsList) {
                                    Log.v("onNext: ", responseFriendsList.toString())
                                    if (responseFriendsList != null) {
                                        if (responseFriendsList.success == 1) {

                                            if (responseFriendsList.data.isNotEmpty()) {

                                                //allfriendList = ArrayList()
                                                allfriendList.addAll(responseFriendsList.data as ArrayList<ResponseFriendsList.Data>)

                                                //Set checked items
                                                if (commaSeperatedIds.isNotEmpty()) {

                                                    val commaArray = commaSeperatedIds.split(",")

                                                    allfriendList.forEachIndexed { index, data ->
                                                        data.isSelected = commaArray.contains(data.id.toString())
                                                    }

                                                    //Set name list in textview
                                                    commaSeperatedNames = allfriendList.filter(ResponseFriendsList.Data::isSelected)
                                                        .joinToString(separator = ", ") { if (it.isSelected) (it.firstName ?: "") + " " + (it.lastName ?: "") else "" }
                                                    binding.txtFriendList.text = commaSeperatedNames

                                                    binding.txtFriendList.visibility = View.VISIBLE
                                                    checkAndEnable(true)
                                                } else {
                                                    //
                                                }

//                                                postTagListAdapter.setData(allfriendList, false)
                                                postTagListAdapter.notifyDataSetChanged()

                                                isloading = false
                                                hasLoadedAllItems = false

                                            } else {
                                                isloading = true
                                                hasLoadedAllItems = true
                                            }

                                        } else {
                                            hasLoadedAllItems = true
                                        }
                                    }
                                    isAPICallRunning = false
                                }

                                override fun onError(e: Throwable) {
                                    hideProgressDialog()
                                    Log.v("onError: ", e.toString())
                                    isAPICallRunning = false
                                }

                                override fun onComplete() {
                                    hideProgressDialog()
                                }
                            })
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    isAPICallRunning = false
                    hideProgressDialog()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            hideProgressDialog()
        }
    }

    override fun onLoadMore() {
        isloading = true

        if (!isAPICallRunning) {
            val scrollPosition: Int = postTagListAdapter.getData()
            if (scrollPosition > 0) {
                showLog("loadmore_tag", scrollPosition.toString())

                getFriendList(scrollPosition, searchtxt)
            }
        }
    }

    override fun isLoading(): Boolean {
        return isloading
    }

    override fun hasLoadedAllItems(): Boolean {
        return hasLoadedAllItems
    }

    /**
     * interface for post done click
     *
     */
    interface OnPostTagDoneClickListener {
        fun onPostTagDoneClick(commaSeperatedIds: String)
    }

    fun setListener(listener: OnPostTagDoneClickListener) {
        onPostTagDoneClickListener = listener
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
//        issearch = true
        searchtxt = query.toString()
        Log.e("search", "search text change")

        binding.searchView.clearFocus()

        allfriendList.clear()
        if (!isAPICallRunning) getFriendList(0, searchtxt)
//        search = false
//        initScrollListener()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    private fun checkAndEnable(enable: Boolean) {
        //enableOrDisableButtonBgColor(requireContext(), enable, binding.btnDone)
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                (this@PostTagFriendListFragment.dialog as BottomSheetDialog).behavior.setState(
                    BottomSheetBehavior.STATE_EXPANDED
                )
            }
        }
    }
}
 
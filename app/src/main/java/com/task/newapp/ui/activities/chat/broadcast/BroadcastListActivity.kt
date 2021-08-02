package com.task.newapp.ui.activities.chat.broadcast

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.task.newapp.R
import com.task.newapp.adapter.chat.broadcast.BroadcastChatListAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityBroadcastListBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.realmDB.deleteBroadcast
import com.task.newapp.realmDB.getAllBroadcastChat
import com.task.newapp.realmDB.models.BroadcastTable
import com.task.newapp.ui.activities.chat.SelectFriendsActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.SelectFriendsNavigation
import com.task.newapp.utils.DialogUtils.DialogCallbacks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class BroadcastListActivity : AppCompatActivity(), View.OnClickListener, BroadcastChatListAdapter.OnChatItemClickListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    lateinit var binding: ActivityBroadcastListBinding
    private var broadcastChatList = ArrayList<BroadcastTable>()
    private lateinit var chatsAdapter: BroadcastChatListAdapter
    private val mCompositeDisposable = CompositeDisposable()
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setAdapter()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_broadcast_list)
        initView()
    }

    private fun initView() {
        binding.toolbarLayout.txtTitle.text = getString(R.string.title_broadcast_list)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        setAdapter()
        initSearchView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_create_broadcast -> {
                resultLauncher.launch(Intent(this, SelectFriendsActivity::class.java).putExtra(Constants.bundle_navigate_from, SelectFriendsNavigation.FROM_CREATE_BROADCAST))
                /*launchActivity<SelectFriendsActivity> {
                    putExtra(Constants.bundle_navigate_from, SelectFriendsNavigation.FROM_CREATE_BROADCAST)
                }*/
            }
        }
    }

    private fun initSearchView() {
        binding.searchLayout.searchView.setOnQueryTextListener(this)
        binding.searchLayout.searchView.onActionViewExpanded()
        binding.searchLayout.searchView.clearFocus()

        binding.searchLayout.searchView.setOnCloseListener {
            false
        }
        binding.searchLayout.searchView.findViewById<View>(R.id.search_close_btn)
            .setOnClickListener(View.OnClickListener {
                Log.d("called", "this is called.")
                binding.searchLayout.searchView.isActivated = true
                binding.searchLayout.searchView.setQuery("", false)
                binding.searchLayout.searchView.isIconified = true
                binding.searchLayout.searchView.onActionViewExpanded()
                binding.searchLayout.searchView.clearFocus()
                binding.searchLayout.searchView.findViewById<View>(R.id.search_button).performClick()
            })
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        chatsAdapter.filter.filter(query);
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        chatsAdapter.filter.filter(newText);
        return false
    }

    private fun setAdapter() {
        prepareChatListAdapterModel(getAllBroadcastChat())
        if (broadcastChatList.isNotEmpty()) {
            if (!this::chatsAdapter.isInitialized) {
                chatsAdapter = BroadcastChatListAdapter(this, this)
                chatsAdapter.setOnItemClickListener(this)
                binding.rvBroadcastChat.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvBroadcastChat.adapter = chatsAdapter
            }
            chatsAdapter.doRefresh(broadcastChatList)
        }

        showHideEmptyView()
    }

    private fun prepareChatListAdapterModel(broadcastList: List<BroadcastTable>) {
        if (broadcastChatList.isNotEmpty())
            broadcastChatList.clear()
        broadcastChatList.addAll(broadcastList)

    }

    private fun showHideEmptyView() {
        if (broadcastChatList.isEmpty()) {
            binding.llNoData.visibility = View.VISIBLE
            binding.rvBroadcastChat.visibility = View.GONE
            //binding.divider.visibility = View.GONE
            binding.searchLayout.llSearch.visibility = View.GONE
        } else {
            binding.llNoData.visibility = View.GONE
            binding.rvBroadcastChat.visibility = View.VISIBLE
            //binding.divider.visibility = View.VISIBLE
            binding.searchLayout.llSearch.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        launchActivity<CreateBroadcastActivity> {
            putExtra(Constants.bundle_selected_friends, broadcastChatList[position].broad_other_userid)
        }
    }

    override fun onClearChatClick(position: Int, broadcastChat: BroadcastTable) {
        DialogUtils().showConfirmationYesNoDialog(this, "Clear Broadcast Chat", "Are you sure you want to clear broadcast messages?", object : DialogCallbacks {
            override fun onPositiveButtonClick() {
                callAPIClearBroadcastChat(broadcastChat)
            }

            override fun onNegativeButtonClick() {
            }

            override fun onDefaultButtonClick(actionName: String) {
            }
        })
    }

    override fun onDeleteBroadcastChatClick(position: Int, broadcastChat: BroadcastTable) {
        DialogUtils().showConfirmationYesNoDialog(this, "Delete Broadcast", "Are you sure you want to delete this broadcast ? ", object : DialogCallbacks {
            override fun onPositiveButtonClick() {
                callAPIDeleteBroadcast(broadcastChat)
            }

            override fun onNegativeButtonClick() {
            }

            override fun onDefaultButtonClick(actionName: String) {
            }
        })
    }

    private fun callAPIDeleteBroadcast(broadcastTable: BroadcastTable) {
        try {
            if (!isNetworkConnected()) {
                showToast(getString(R.string.no_internet))
                return
            }

            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .deleteBroadcast(broadcastTable.broadcast_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {
                            if (commonResponse.success == 1) {
                                deleteBroadcast(broadcastTable.broadcast_id)
                                setAdapter()
                            }

                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
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

    private fun callAPIClearBroadcastChat(broadcastTable: BroadcastTable) {

        try {
            if (isNetworkConnected()) {
                showToast(getString(R.string.no_internet))
                return
            }

            openProgressDialog(this)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.broadcast_id to broadcastTable.broadcast_id
            )
            mCompositeDisposable.add(
                ApiClient.create()
                    .deleteChat(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {
                            if (commonResponse.success == 1) {
                                showToast("Broadcast messages deleted successfully.")
                            }

                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
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
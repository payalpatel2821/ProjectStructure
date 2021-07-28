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
import com.task.newapp.databinding.ActivityBroadcastListBinding
import com.task.newapp.realmDB.getAllBroadcastChat
import com.task.newapp.realmDB.models.BroadcastTable
import com.task.newapp.utils.Constants
import com.task.newapp.utils.Constants.Companion.SelectFriendsNavigation
import com.task.newapp.utils.DialogUtils
import com.task.newapp.utils.DialogUtils.DialogCallbacks
import com.task.newapp.utils.showToast
import io.reactivex.disposables.CompositeDisposable
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
        initSearchView()
        setAdapter()
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
//        issearch = true
//        searchtxt = query
        Log.e("search", "search text change")
//        if (!isAPICallRunning) initData(searchtxt, 0, "main")
//        search = false
//        initScrollListener()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    private fun setAdapter() {
        prepareChatListAdapterModel(getAllBroadcastChat())
        if (broadcastChatList.isNotEmpty()) {
            //if (chatsAdapter == null) {
            chatsAdapter = BroadcastChatListAdapter(this, this)
            chatsAdapter.setOnItemClickListener(this)
            binding.rvBroadcastChat.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.rvBroadcastChat.adapter = chatsAdapter
            //  }
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
    }

    override fun onClearChatClick(position: Int, chats: BroadcastTable) {
        showToast("onClearChatClick $position")
    }

    override fun onDeleteBroadcastChatClick(position: Int, chats: BroadcastTable) {
        DialogUtils().showConfirmationYesNoDialog(this,"Delete Broadcast","Are you sure you want to delete this broadcast ? ",object :DialogCallbacks{
            override fun onPositiveButtonClick() {
                callAPIDeleteBroadcast(chats)
            }

            override fun onNegativeButtonClick() {
                TODO("Not yet implemented")
            }

            override fun onDefaultButtonClick(actionName: String) {
                TODO("Not yet implemented")
            }
        })
        showToast("onDeleteBroadcastChatClick $position")
    }

    private fun callAPIDeleteBroadcast(broadcastTable: BroadcastTable) {

    }

}
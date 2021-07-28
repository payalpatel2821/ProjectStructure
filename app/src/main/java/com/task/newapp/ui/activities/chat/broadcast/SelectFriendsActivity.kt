package com.task.newapp.ui.activities.chat.broadcast

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView.OnItemClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.task.newapp.R
import com.task.newapp.adapter.post.SelectFriendsListAdapter
import com.task.newapp.adapter.post.SelectedFriendsListAdapter
import com.task.newapp.databinding.ActivitySelectFriendsBinding
import com.task.newapp.models.chat.SelectFriendWrapperModel
import com.task.newapp.realmDB.getAllFriends
import com.task.newapp.realmDB.getSingleUserDetails
import com.task.newapp.realmDB.models.FriendRequest
import com.task.newapp.realmDB.models.Users
import com.task.newapp.ui.activities.BaseAppCompatActivity
import com.task.newapp.ui.activities.chat.group.CreateGroupActivity
import com.task.newapp.utils.Constants
import com.task.newapp.utils.Constants.Companion.FriendRequestStatus
import com.task.newapp.utils.Constants.Companion.SelectFriendsNavigation
import com.task.newapp.utils.Constants.Companion.SelectFriendsNavigation.*
import com.task.newapp.utils.enableOrDisableButtonBgColor
import com.task.newapp.utils.getCurrentUserId
import io.reactivex.disposables.CompositeDisposable

class SelectFriendsActivity : BaseAppCompatActivity(), SearchView.OnQueryTextListener, OnClickListener {

    lateinit var binding: ActivitySelectFriendsBinding
    private val mCompositeDisposable = CompositeDisposable()
    private var allFriendsList: ArrayList<SelectFriendWrapperModel> = ArrayList()
    private var selectedFriendsList: ArrayList<SelectFriendWrapperModel> = ArrayList()
    private lateinit var friendsListAdapter: SelectFriendsListAdapter
    private lateinit var selectedFriendsListAdapter: SelectedFriendsListAdapter
    private lateinit var navigatedFrom: SelectFriendsNavigation

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()

            }
        }


    /**
     * item click listener for friends list. based on check and uncheck of item,
     * selected friends list will be updated
     */
    private val friendsItemClickListener = OnItemClickListener { parent, view, position, id ->
        val obj = friendsListAdapter.getItem(position)
        obj.isChecked = !obj.isChecked
        friendsListAdapter.updateCheckUncheck(obj.isChecked, position)

        if (obj.isChecked) {
            selectedFriendsList.add(obj)
            if (!this::selectedFriendsListAdapter.isInitialized) { //initialize adapter if not else add object to adapter list
                setSelectedFriendsAdapter()
            } else
                selectedFriendsListAdapter.addSelected(obj)
        } else {
            selectedFriendsList.removeAt(selectedFriendsList.indexOf(obj))
            selectedFriendsListAdapter.removeSelected(obj)
        }
        checkAndEnable(selectedFriendsList.isNotEmpty())
    }

    /**
     * item click listener for selected friends list. on click of close icon item will be removed from the list and also,
     * friends list will be unchecked the item selected
     */
    private val selectedFriendsItemClickListener = OnItemClickListener { parent, view, position, id ->
        val obj = selectedFriendsListAdapter.getItem(position) //get current click object from adapter using position
        val index = friendsListAdapter.getItemPosition(obj) //get index position for friends list recyclerview to check/uncheck item
        selectedFriendsList.remove(obj)
        selectedFriendsListAdapter.removeSelected(obj)
        if (index != -1)
            friendsListAdapter.updateCheckUncheck(obj.isChecked, index)
        checkAndEnable(selectedFriendsList.isNotEmpty())

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_friends)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_friends)
        initView()
    }

    private fun initView() {
        navigatedFrom = intent.getSerializableExtra(Constants.bundle_navigate_from) as SelectFriendsNavigation

        binding.toolbarLayout.txtTitle.text = getString(R.string.title_select_friends)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        checkAndEnable(false)
        setSelectFriendsAdapter()
        initSearchView()
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
                binding.searchLayout.searchView.isActivated = true
                binding.searchLayout.searchView.setQuery("", false)
                binding.searchLayout.searchView.isIconified = true
                binding.searchLayout.searchView.onActionViewExpanded()
                binding.searchLayout.searchView.clearFocus()
                binding.searchLayout.searchView.findViewById<View>(R.id.search_button).performClick()
            })
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        friendsListAdapter.filter.filter(query);
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        friendsListAdapter.filter.filter(newText);
        return false
    }

    private fun checkAndEnable(enable: Boolean) {
        enableOrDisableButtonBgColor(this, enable, binding.btnNext)
    }


    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

    /**
     * set recyclerview of friends list to be selected for the broadcast
     *
     */
    private fun setSelectFriendsAdapter() {
        prepareAllFriendListAdapterModel(getAllFriends(FriendRequestStatus.ACCEPT.status))
        if (allFriendsList.isNotEmpty()) {
            if (!this::friendsListAdapter.isInitialized) {
                friendsListAdapter = SelectFriendsListAdapter(this)
                friendsListAdapter.setOnItemClickListener(friendsItemClickListener)
                binding.rvFriends.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvFriends.adapter = friendsListAdapter
            }
            friendsListAdapter.doRefresh(allFriendsList)
        }

        showHideEmptyView()

    }

    private fun setSelectedFriendsAdapter() {
        if (selectedFriendsList.isNotEmpty()) {
            if (!this::selectedFriendsListAdapter.isInitialized) {
                selectedFriendsListAdapter = SelectedFriendsListAdapter(this)
                selectedFriendsListAdapter.setOnItemClickListener(selectedFriendsItemClickListener)
                binding.rvSelectedFriends.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.rvSelectedFriends.adapter = selectedFriendsListAdapter
            }
            selectedFriendsListAdapter.doRefresh(selectedFriendsList)
        }

    }

    private fun prepareAllFriendListAdapterModel(friendRequest: List<FriendRequest>) {
        if (allFriendsList.isNotEmpty())
            allFriendsList.clear()
        friendRequest.forEach {
            getSingleUserDetails(if (it.user_id == getCurrentUserId()) it.friend_id else it.user_id)?.let { users: Users ->
                val searchUser = SelectFriendWrapperModel(
                    users.receiver_id,
                    users.first_name ?: "",
                    users.last_name ?: "",
                    users.user_name ?: "",
                    users.profile_image ?: "",
                    users.profile_color ?: "",
                    isChecked = false,
                    isEdit = true
                )
                allFriendsList.add(searchUser)
            }
            if (allFriendsList.isNotEmpty()) {
                allFriendsList.sortBy { obj: SelectFriendWrapperModel ->
                    obj.firstName.lowercase()
                }
            }
        }

    }


    private fun showHideEmptyView() {
        if (allFriendsList.isEmpty()) {
            binding.llNoData.visibility = View.VISIBLE
            binding.rvFriends.visibility = View.GONE
            binding.searchLayout.llSearch.visibility = View.GONE
        } else {
            binding.llNoData.visibility = View.GONE
            binding.rvFriends.visibility = View.VISIBLE
            binding.searchLayout.llSearch.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btn_next -> {
                when (navigatedFrom) {
                    FROM_CREATE_GROUP -> {
                        resultLauncher.launch(Intent(this, CreateGroupActivity::class.java).putExtra(Constants.bundle_selected_friends, selectedFriendsList))
                        //launchActivity<CreateGroupActivity> { putExtra(Constants.bundle_selected_friends, selectedFriendsList) }
                    }
                    FROM_CREATE_BROADCAST -> {
                        resultLauncher.launch(Intent(this, CreateBroadcastActivity::class.java).putExtra(Constants.bundle_selected_friends, selectedFriendsList))
                        // launchActivity<CreateBroadcastActivity> { putExtra(Constants.bundle_selected_friends, selectedFriendsList) }
                    }
                    FROM_EDIT_GROUP -> TODO()
                    FROM_EDIT_BROADCAST -> TODO()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
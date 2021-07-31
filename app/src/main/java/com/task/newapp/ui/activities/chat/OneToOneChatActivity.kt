package com.task.newapp.ui.activities.chat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.databinding.ActivityOneToOneChatBinding
import com.task.newapp.realmDB.getSingleUserChat
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.ui.activities.profile.OtherUserProfileActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.DialogUtils.ChatAttachmentActionsName
import com.task.newapp.utils.DialogUtils.ChatAttachmentActionsName.*
import com.task.newapp.utils.DialogUtils.DialogCallbacks
import io.reactivex.disposables.CompositeDisposable

class OneToOneChatActivity : AppCompatActivity(), OnClickListener {

    lateinit var binding: ActivityOneToOneChatBinding
    private val mCompositeDisposable = CompositeDisposable()
    private var opponentId: Int = 0
    private var chatObj: Chats? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_one_to_one_chat)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initView()

    }

    private fun initView() {
        opponentId = intent.getIntExtra(Constants.bundle_opponent_id, 0)
        chatObj = getSingleUserChat(opponentId)
        chatObj?.let {
            binding.toolbarLayout.txtTitle.text = it.name
            binding.toolbarLayout.imgProfile.load(it.user_data?.profile_image ?: "", true, it.name, it.user_data?.profile_color)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_attachment -> {
                DialogUtils().showChatAttachmentIOSDialog(this, listener = object : DialogCallbacks {
                    override fun onPositiveButtonClick() {

                    }

                    override fun onNegativeButtonClick() {
                    }

                    override fun onDefaultButtonClick(actionName: String) {
                        when (ChatAttachmentActionsName.getObjectFromName(actionName)) {
                            CAMERA -> showToast(actionName)
                            PHOTOS -> showToast(actionName)
                            DOCUMENTS -> showToast(actionName)
                            CONTACTS -> showToast(actionName)
                            LOCATION -> showToast(actionName)
                            null -> showToast(actionName)
                        }


                    }
                })
            }
            R.id.ll_profile_layout -> {
                launchActivity<OtherUserProfileActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.user_id, opponentId)
                }
            }
        }
    }


}
package com.task.newapp.ui.activities.setting

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityNotificationsBinding
import com.task.newapp.models.ResponseGetSetting
import com.task.newapp.models.ResponseUserSetting
import com.task.newapp.utils.Constants
import com.task.newapp.utils.isNetworkConnected
import com.task.newapp.utils.launchActivity
import com.task.newapp.utils.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class NotificationsActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityNotificationsBinding
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications)
        initView()
    }

    private fun initView() {
        binding.toolbarLayout.txtTitle.text = resources.getString(R.string.notifications)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        callAPISetNotification()
        callAPIGetSetting()
    }

    private fun callAPISetNotification() {
        binding.switchPauseNotification.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (!isNetworkConnected()) {
                    showToast(getString(R.string.no_internet))
                    return
                }
                try {
                    val hashMap: HashMap<String, Any> = hashMapOf(
                        if (isChecked) {
                            Constants.isPauseNotification to 1
                        } else {
                            Constants.isPauseNotification to 0
                        }
                    )

                    mCompositeDisposable.add(
                        ApiClient.create()
                            .setUserSetting(hashMap)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableObserver<ResponseUserSetting>() {
                                override fun onNext(loginResponse: ResponseUserSetting) {
                                    if (loginResponse.success == 0) {
                                        binding.switchPauseNotification.isSelected = isChecked
                                        showToast(loginResponse.message)
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    Log.v("onError: ", e.toString())
                                }

                                override fun onComplete() {
                                }
                            })
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.lay_profile -> {
                launchActivity<ProfileActivity> { }
            }
            R.id.lay_chat -> {
                launchActivity<ChatSettingActivity> { }
            }
            R.id.lay_timeline -> {
                launchActivity<TimelineActivity> { }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun callAPIGetSetting() {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.type to "user"
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .getSettings(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseGetSetting>() {
                        override fun onNext(responseGetSetting: ResponseGetSetting) {
                            if (responseGetSetting.success == 1) {
                                binding.switchPauseNotification.isSelected = responseGetSetting.userSetting.isPauseNotification != 0
                            } else {
                                showToast(responseGetSetting.message)
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                        }

                        override fun onComplete() {
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
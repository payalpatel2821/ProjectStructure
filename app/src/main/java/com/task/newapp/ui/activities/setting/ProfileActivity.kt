package com.task.newapp.ui.activities.setting

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityProfileBinding
import com.task.newapp.models.ResponseUserSetting
import com.task.newapp.utils.Constants
import com.task.newapp.utils.isNetworkConnected
import com.task.newapp.utils.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        initView()
    }

    private fun initView() {
        binding.toolbarLayout.txtTitle.text = resources.getString(R.string.profile)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        callAPIProfileNotificationSetting(binding.switchProfileViews,Constants.isProfileViewNotify)
        callAPIProfileNotificationSetting(binding.switchFollowRequest,Constants.isFollowRequestNotify)
        callAPIProfileNotificationSetting(binding.switchAcceptedFollowRequest,Constants.isAcceptFollowRequestNotify)
    }

    private fun callAPIProfileNotificationSetting(switch : Switch, key: String) {
        switch.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (!isNetworkConnected()) {
                    showToast(getString(R.string.no_internet))
                    return
                }
                try {
                    val hashMap: HashMap<String, Any> = hashMapOf(
                        if (isChecked) {
                            key to 1
                        } else {
                            key to 0
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
                                        switch.isSelected = isChecked
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}
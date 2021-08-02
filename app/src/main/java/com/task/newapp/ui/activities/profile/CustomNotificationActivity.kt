package com.task.newapp.ui.activities.profile

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityCustomNotificationBinding
import com.task.newapp.models.ResponseFriendSetting
import com.task.newapp.models.ResponseNotification
import com.task.newapp.realmDB.insertNotificationToneData
import com.task.newapp.realmDB.prepareNotificationToneData
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class CustomNotificationActivity : AppCompatActivity() {
    private lateinit var mp: MediaPlayer
    private var User_ID: Int = 0
    private lateinit var binding: ActivityCustomNotificationBinding
    var isCustomNotification: Int = 0
    var notificationTune: Int = 0
    lateinit var vibrateStatus: String
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_notification)
        initView()
    }

    private fun initView() {
        setData()
    }

    private fun setData() {
        mp = MediaPlayer()
        isCustomNotification = intent.getIntExtra(Constants.bundle_custom_notification, 0)
        notificationTune = intent.getIntExtra(Constants.bundle_notification_tone, 0)
        vibrateStatus = intent.getStringExtra(Constants.bundle_vibration)!!
        User_ID = intent.getIntExtra(Constants.user_id, 0)
        binding.switchCustomNotification.isChecked = isCustomNotification == 1
        if (isCustomNotification == 1) {
            binding.llCustomSetting.visibility = VISIBLE
        } else {
            binding.llCustomSetting.visibility = GONE
        }
        binding.txtVibration.text = vibrateStatus.firstCap()
        binding.switchCustomNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                callAPICustomNotification(1)

            } else {
                callAPICustomNotification(0)
            }
        }
        callAPIGetAllNotification()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.ll_vibration -> {
                showVibrateDialog()
            }
            R.id.ll_notification -> {
                DialogUtils().showNotificationDialog(this, notificationTune, object : DialogUtils.ListDialogItemClickCallback {
                    override fun onItemClick(position: Int, notificationUrl: String) {
                        playSong(notificationUrl)
                    }
                }, object : DialogUtils.DialogCallbacks {
                    override fun onPositiveButtonClick() {

                    }

                    override fun onNegativeButtonClick() {
                    }

                    override fun onDefaultButtonClick(actionName: String) {
                        callAPISetNotificationID(actionName.toInt() + 1)
                    }
                })
            }
        }
    }

    fun playSong(songName: String) {
        mp.reset() // stops any current playing song
        mp = MediaPlayer.create(applicationContext, Uri.parse(songName)) // create's
        mp.start() // starting mediaplayer
    }

    private fun callAPICustomNotification(isCustomNotificationEnable: Int) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.is_custom_notification_enable to isCustomNotificationEnable,
                Constants.friend_id to User_ID,
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .setFriendSetting(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseFriendSetting>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(loginResponse: ResponseFriendSetting) {
                            Log.v("onNext: ", loginResponse.toString())
                            showToast(loginResponse.message)

                            if (loginResponse.success == 1) {
                                isCustomNotification = loginResponse.data.isCustomNotificationEnable
                                notificationTune = loginResponse.data.notificationToneId
                                vibrateStatus = loginResponse.data.vibrateStatus
                                if (isCustomNotification == 1) {
                                    binding.llCustomSetting.visibility = VISIBLE
                                } else {
                                    binding.llCustomSetting.visibility = GONE
                                }
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

    private fun callAPISetNotificationID(notificationToneId: Int) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.is_custom_notification_enable to 1,
                Constants.notification_tone_id to notificationToneId,
                Constants.friend_id to User_ID,
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .setFriendSetting(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseFriendSetting>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(loginResponse: ResponseFriendSetting) {
                            Log.v("onNext: ", loginResponse.toString())
                            showToast(loginResponse.message)

                            if (loginResponse.success == 1) {
                                isCustomNotification = loginResponse.data.isCustomNotificationEnable
                                notificationTune = loginResponse.data.notificationToneId
                                vibrateStatus = loginResponse.data.vibrateStatus
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

    private fun callAPISetVibrateStatus(notificationVibrateStatus: String) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.is_custom_notification_enable to 1,
                Constants.vibrate_status to notificationVibrateStatus,
                Constants.friend_id to User_ID
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .setFriendSetting(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseFriendSetting>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(loginResponse: ResponseFriendSetting) {
                            Log.v("onNext: ", loginResponse.toString())
                            showToast(loginResponse.message)

                            if (loginResponse.success == 1) {
                                isCustomNotification = loginResponse.data.isCustomNotificationEnable
                                notificationTune = loginResponse.data.notificationToneId
                                vibrateStatus = loginResponse.data.vibrateStatus
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

    private fun callAPIGetAllNotification() {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)

            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.type to "my_notification"
            )

            mCompositeDisposable.add(
                ApiClient.create()
                    .getNotificationTune(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseNotification>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(responseNotification: ResponseNotification) {
                            Log.v("onNext: ", responseNotification.toString())
                            showToast(responseNotification.message)

                            if (responseNotification.success == 1) {
                                insertNotificationToneData(prepareNotificationToneData(responseNotification.data))
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

    private fun showVibrateDialog() {
        DialogUtils().showVibrationStatusDialog(this as AppCompatActivity, binding.txtVibration.text.toString(), "", "", object : DialogUtils.DialogCallbacks {
            override fun onPositiveButtonClick() {

            }

            override fun onNegativeButtonClick() {

            }

            override fun onDefaultButtonClick(actionName: String) {
                when (actionName) {
                    DialogUtils.VibrationDialogActionName.OFF.value -> {
                        binding.txtVibration.text = DialogUtils.VibrationDialogActionName.OFF.value
                        callAPISetVibrateStatus(DialogUtils.VibrationDialogActionName.OFF.value.decapitalize())
                    }
                    DialogUtils.VibrationDialogActionName.DEFAULT.value -> {
                        binding.txtVibration.text = DialogUtils.VibrationDialogActionName.DEFAULT.value
                        callAPISetVibrateStatus(DialogUtils.VibrationDialogActionName.DEFAULT.value.decapitalize())
                    }
                    DialogUtils.VibrationDialogActionName.SHORT.value -> {
                        binding.txtVibration.text = DialogUtils.VibrationDialogActionName.SHORT.value
                        callAPISetVibrateStatus(DialogUtils.VibrationDialogActionName.SHORT.value.decapitalize())
                    }
                    DialogUtils.VibrationDialogActionName.LONG.value -> {
                        binding.txtVibration.text = DialogUtils.VibrationDialogActionName.LONG.value
                        callAPISetVibrateStatus(DialogUtils.VibrationDialogActionName.LONG.value.decapitalize())
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(Constants.bundle_custom_notification, isCustomNotification)
        intent.putExtra(Constants.bundle_notification_tone, notificationTune)
        intent.putExtra(Constants.bundle_vibration, vibrateStatus)
        setResult(RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
        mp.release();
    }
}
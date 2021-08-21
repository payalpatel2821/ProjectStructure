package com.task.newapp.ui.activities.profile

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.MenuItem
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
import com.task.newapp.models.ResponseGroupSetting
import com.task.newapp.models.ResponseNotification
import com.task.newapp.realmDB.*
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class CustomNotificationForGroupActivity : AppCompatActivity() {
    private lateinit var mp: MediaPlayer
    private var GroupID: Int = 0
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
        binding.toolbarLayout.txtTitle.text = getString(R.string.custom_notification)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        mp = MediaPlayer()
        isCustomNotification = intent.getIntExtra(Constants.bundle_custom_notification, 0)
        notificationTune = intent.getIntExtra(Constants.bundle_notification_tone, 0)
        vibrateStatus = intent.getStringExtra(Constants.bundle_vibration)!!
        GroupID = intent.getIntExtra(Constants.group_id, 0)
        binding.switchCustomNotification.isChecked = isCustomNotification == 1
        binding.txtNotification.text = getSelectedNotificationTuneNameBYID(notificationTune)
        if (isCustomNotification == 1) {
            binding.llCustomSetting.visibility = VISIBLE
        } else {
            binding.llCustomSetting.visibility = GONE
        }
        binding.txtVibration.text = vibrateStatus.firstCap()
        binding.switchCustomNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.is_custom_notification_enable to if (isChecked) 1 else 0,
                Constants.group_id to GroupID,
            )
            callAPICustomNotification(hashMap)

        }
        if (getAllNotificationTune().isEmpty()) {
            callAPIGetAllNotification(mCompositeDisposable)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.ll_vibration -> {
                showVibrateDialog()
            }
            R.id.ll_notification -> {
                DialogUtils().showNotificationDialog(
                    this,
                    notificationTune,
                    binding.txtNotification,
                    object : DialogUtils.ListDialogItemClickCallback {
                        override fun onItemClick(position: Int, notificationUrl: String) {
                            playSong(notificationUrl)
                        }
                    },
                    object : DialogUtils.DialogCallbacks {
                        override fun onPositiveButtonClick() {

                        }

                        override fun onNegativeButtonClick() {
                        }

                        override fun onDefaultButtonClick(actionName: String) {
                            val hashMap: HashMap<String, Any> = hashMapOf(
                                Constants.is_custom_notification_enable to 1,
                                Constants.notification_tone_id to actionName.toInt() + 1,
                                Constants.group_id to GroupID,
                            )
                            callAPICustomNotification(hashMap)
                        }
                    })
            }
            R.id.iv_back -> {
                onBackPressed()
            }
        }
    }

    fun playSong(songName: String) {
        val cdnPathUri = Uri.parse(songName)
        val r = RingtoneManager.getRingtone(this, cdnPathUri)
        r.play()
    }

    private fun callAPICustomNotification(hashMap: HashMap<String, Any>) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        try {
            openProgressDialog(this)
            mCompositeDisposable.add(
                ApiClient.create()
                    .setGroupSetting(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseGroupSetting>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(responseGroupSetting: ResponseGroupSetting) {
                            if (responseGroupSetting.success == 1) {
                                isCustomNotification =
                                    responseGroupSetting.data.isCustomNotificationEnable
                                notificationTune = responseGroupSetting.data.notificationToneId
                                vibrateStatus = responseGroupSetting.data.vibrateStatus
                                prepareSingleGroupUsersData(responseGroupSetting.data)?.let {
                                    updateGroupUserSettings(
                                        it.user_id,
                                        GroupID,
                                        it
                                    )
                                }
                                if (isCustomNotification == 1) {
                                    binding.llCustomSetting.visibility = VISIBLE
                                } else {
                                    binding.llCustomSetting.visibility = GONE
//                                    binding.txtNotification.text = ""
                                }
                            } else {
//                                binding.txtNotification.text = ""
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                            binding.txtNotification.text = ""
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
        DialogUtils().showVibrationStatusDialog(
            this as AppCompatActivity,
            binding.txtVibration.text.toString(),
            "",
            "",
            object : DialogUtils.DialogCallbacks {
                override fun onPositiveButtonClick() {

                }

                override fun onNegativeButtonClick() {

                }

                override fun onDefaultButtonClick(actionName: String) {
                    val action = DialogUtils.VibrationDialogActionName.getObjectFromName(actionName)
                    val vi = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    val hashMap: HashMap<String, Any> = hashMapOf(
                        Constants.is_custom_notification_enable to 1,
                        Constants.vibrate_status to action.value.decapitalize(),
                        Constants.group_id to GroupID
                    )
                    callAPICustomNotification(hashMap)
                    binding.txtVibration.text = action.value
                    when (action) {
                        DialogUtils.VibrationDialogActionName.OFF -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vi.vibrate(VibrationEffect.createOneShot(1, VibrationEffect.DEFAULT_AMPLITUDE))
                            } else {
                                //deprecated in API 26
                                vi.vibrate(longArrayOf(1), VibrationEffect.DEFAULT_AMPLITUDE)
                            }
                        }
                        DialogUtils.VibrationDialogActionName.DEFAULT -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vi.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 1000), VibrationEffect.DEFAULT_AMPLITUDE))
                            } else {
                                vi.vibrate(longArrayOf(0, 500, 1000), -1)
                            }
                        }
                        DialogUtils.VibrationDialogActionName.SHORT -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vi.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 1000), VibrationEffect.DEFAULT_AMPLITUDE))
                            } else {
                                //deprecated in API 26
                                vi.vibrate(longArrayOf(0, 100, 1000), -1)
                            }
                        }
                        DialogUtils.VibrationDialogActionName.LONG -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vi.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 1000, 500, 1000, 500), VibrationEffect.DEFAULT_AMPLITUDE))
                            } else {
                                //deprecated in API 26
                                vi.vibrate(longArrayOf(0, 1000, 500, 1000, 500), -1)
                            }
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
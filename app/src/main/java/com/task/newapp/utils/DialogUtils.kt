package com.task.newapp.utils

import android.app.Dialog
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.irozon.alertview.AlertView
import com.irozon.alertview.enums.AlertActionStyle
import com.irozon.alertview.enums.AlertStyle
import com.irozon.alertview.objects.AlertAction
import com.task.newapp.R
import com.task.newapp.adapter.profile.NotificationListAdapter
import com.task.newapp.models.NotificationToneWrapper
import com.task.newapp.realmDB.getAllNotificationTune


class DialogUtils {
    fun showConfirmationDialog(activity: AppCompatActivity, title: String, message: String, listener: DialogCallbacks) {
        val alert1 = AlertView(title, message, AlertStyle.BOTTOM_SHEET)
        alert1.show(activity)
        alert1.addAction(AlertAction(UnfollowDialogActionName.UNFOLLOW.value, AlertActionStyle.POSITIVE) {
            listener.onPositiveButtonClick()
        })
        alert1.addAction(AlertAction(UnfollowDialogActionName.CANCEL.value, AlertActionStyle.NEGATIVE) {
            listener.onNegativeButtonClick()
        })
    }

    fun showConfirmationYesNoDialog(activity: AppCompatActivity, title: String, message: String, listener: DialogCallbacks) {
        val alert1 = AlertView(title, message, AlertStyle.BOTTOM_SHEET)
        alert1.show(activity)
        alert1.addAction(AlertAction(YesNoDialogActionName.YES.value, AlertActionStyle.POSITIVE) {
            listener.onPositiveButtonClick()
        })
        alert1.addAction(AlertAction(YesNoDialogActionName.NO.value, AlertActionStyle.NEGATIVE) {
            listener.onNegativeButtonClick()
        })
    }

    fun showNotificationDialog(activity: AppCompatActivity, setPosition: Int, listener: ListDialogItemClickCallback, doneClick: DialogCallbacks) {
        val notificationTunes: ArrayList<NotificationToneWrapper> = ArrayList()
        getAllNotificationTune().forEach {
            notificationTunes.add(NotificationToneWrapper(it))
        }
        val dialog = BottomSheetDialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_notification)
        val txtDone: TextView = dialog.findViewById(R.id.txt_done)!!
        val rvNotification: RecyclerView = dialog.findViewById(R.id.rv_notification)!!
        val notificationAdp = NotificationListAdapter(activity, notificationTunes)
        notificationAdp.selectSingleItem(setPosition - 1)
        notificationAdp.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            notificationAdp.selectSingleItem(position)
            listener.onItemClick(position,notificationTunes.get(position).notificationTone.notification_url)
        })
        txtDone.setOnClickListener(View.OnClickListener {
            doneClick.onDefaultButtonClick(notificationAdp.getCheckedItem().toString())
            dialog.dismiss()
        })

        rvNotification.layoutManager = LinearLayoutManager(activity)
        rvNotification.adapter = notificationAdp
        dialog.show()
    }

    fun showConfirmationIOSDialog(activity: AppCompatActivity, title: String, message: String, listener: DialogCallbacks) {
        val alert = AlertView(title, message, AlertStyle.IOS)
        alert.show(activity)

        alert.addAction(AlertAction(PostDialogActionName.REPORT.value, AlertActionStyle.NEGATIVE) {
            listener.onNegativeButtonClick()
        })
        alert.addAction(AlertAction(PostDialogActionName.COPYLINK.value, AlertActionStyle.DEFAULT) {
            listener.onDefaultButtonClick(PostDialogActionName.COPYLINK.value)
        })
        alert.addAction(AlertAction(PostDialogActionName.TURNONPOST.value, AlertActionStyle.DEFAULT) {
            listener.onDefaultButtonClick(PostDialogActionName.TURNONPOST.value)
        })
        alert.addAction(AlertAction(PostDialogActionName.UNFOLLOW.value, AlertActionStyle.DEFAULT) {
            listener.onDefaultButtonClick(PostDialogActionName.UNFOLLOW.value)
        })
    }

    fun showVibrationStatusDialog(activity: AppCompatActivity, checkedOption: String, title: String, message: String, listener: DialogCallbacks) {
        val alert = AlertView(title, message, AlertStyle.IOS_RADIO)
        alert.show(activity)

        for (value in VibrationDialogActionName.values()) {
            alert.addAction(AlertAction(value.value, value.value == checkedOption, AlertActionStyle.DEFAULT) {
                listener.onDefaultButtonClick(value.value)
            })
        }
    }

    fun showReportDialog(activity: AppCompatActivity){
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_report_user)
        val txtDone: TextView = dialog.findViewById(R.id.txt_done)!!
        val rvNotification: RecyclerView = dialog.findViewById(R.id.rv_notification)!!

        txtDone.setOnClickListener(View.OnClickListener {
//            doneClick.onDefaultButtonClick(notificationAdp.getCheckedItem().toString())
            dialog.dismiss()
        })

        dialog.show()
    }

    interface DialogCallbacks {
        fun onPositiveButtonClick()
        fun onNegativeButtonClick()
        fun onDefaultButtonClick(actionName: String)
    }

    interface ListDialogItemClickCallback {
        fun onItemClick(position: Int, notificationUrl: String)
    }

    enum class UnfollowDialogActionName(val value: String) {
        CANCEL("Cancel"),
        UNFOLLOW("Unfollow");
    }

    enum class YesNoDialogActionName(val value: String) {
        YES("Yes"),
        NO("No");
    }

    enum class PostDialogActionName(val value: String) {
        REPORT("Report"),
        COPYLINK("Copy Link"),
        TURNONPOST("Turn on Post Notification"),
        MUTE("Mute"),
        UNFOLLOW("Unfollow")
    }

    enum class VibrationDialogActionName(val value: String) {
        OFF("Off"),
        DEFAULT("Default"),
        SHORT("Short"),
        LONG("Long")
    }

}
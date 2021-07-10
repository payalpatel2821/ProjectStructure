package com.task.newapp.utils

import androidx.appcompat.app.AppCompatActivity
import com.irozon.alertview.AlertView
import com.irozon.alertview.enums.AlertActionStyle
import com.irozon.alertview.enums.AlertStyle
import com.irozon.alertview.objects.AlertAction
import com.task.newapp.R


class DialogUtils {
    fun showConfirmationDialog(activity: AppCompatActivity, title: String, message: String, listener: DialogCallbacks) {
        val alert1 = AlertView(title, message, AlertStyle.BOTTOM_SHEET)
        alert1.show(activity)
        alert1.addAction(AlertAction(UnfollowDialogActionName.UNFOLLOW.value, AlertActionStyle.POSITIVE) {
            listener.onPositiveButtonClick()
        })
        alert1.addAction(AlertAction(UnfollowDialogActionName.CANCEL.value, AlertActionStyle.NEGATIVE) {
            listener.onPositiveButtonClick()
        })
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


    interface DialogCallbacks {
        fun onPositiveButtonClick()
        fun onNegativeButtonClick()
        fun onDefaultButtonClick(actionName: String)
    }

    enum class UnfollowDialogActionName(val value: String) {
        CANCEL("Cancel"),
        UNFOLLOW("Unfollow");
    }
    enum class PostDialogActionName(val value: String) {
        REPORT("Report"),
        COPYLINK("Copy Link"),
        TURNONPOST("Turn on Post Notification"),
        MUTE("Mute"),
        UNFOLLOW("Unfollow")
    }


}
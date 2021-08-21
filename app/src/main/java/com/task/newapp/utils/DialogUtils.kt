package com.task.newapp.utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.irozon.alertview.AlertView
import com.irozon.alertview.enums.AlertActionStyle
import com.irozon.alertview.enums.AlertStyle
import com.irozon.alertview.objects.AlertAction
import com.task.newapp.R
import com.task.newapp.adapter.profile.NotificationListAdapter
import com.task.newapp.models.NotificationToneWrapper
import com.task.newapp.realmDB.getAllNotificationTune
import java.util.*
import kotlin.collections.ArrayList


class DialogUtils {

    fun showConfirmationDialog(activity: AppCompatActivity, title: String, message: String, listener: DialogCallbacks) {
        val alert1 = AlertView(title, message, AlertStyle.BOTTOM_SHEET)
        alert1.show(activity)
        alert1.addAction(AlertAction(UnfollowDialogActionName.UNFOLLOW.value, AlertActionStyle.DEFAULT) {
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

    fun showNotificationDialog(activity: AppCompatActivity, setPosition: Int, textView: AppCompatTextView, listener: ListDialogItemClickCallback, doneClick: DialogCallbacks) {
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
            listener.onItemClick(position, notificationTunes.get(position).notificationTone.notification_url)
        })
        txtDone.setOnClickListener(View.OnClickListener {
            textView.text = notificationTunes.get(notificationAdp.getCheckedItem()).notificationTone.display_name
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
        alert.setCancelButtonText(activity.getString(R.string.cancel),activity.resources.getColor(com.irozon.alertview.R.color.black))
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

    fun showChatAttachmentIOSDialog(activity: AppCompatActivity, title: String = "", message: String = "", listener: DialogCallbacks) {
        val alert = AlertView(title, message, AlertStyle.IOS_ICON)
        alert.show(activity)

        ChatAttachmentActionsName.values().forEach { actionNames: ChatAttachmentActionsName ->
            alert.addAction(AlertAction(actionNames.value, actionNames.resouceId, AlertActionStyle.DEFAULT) {
                listener.onDefaultButtonClick(actionNames.value)
            })
        }

    }

    fun showVibrationStatusDialog(activity: AppCompatActivity, checkedOption: String, title: String, message: String, listener: DialogCallbacks) {
        val alert = AlertView(title, message, AlertStyle.IOS_RADIO)
        alert.setCancelButtonText(activity.getString(R.string.cancel),activity.resources.getColor(com.irozon.alertview.R.color.red))
        for (value in VibrationDialogActionName.values()) {
            alert.addAction(AlertAction(value.value, value.value == checkedOption, AlertActionStyle.DEFAULT) {
                listener.onDefaultButtonClick(value.value)
            })
        }
        alert.show(activity)
    }

    fun showReportDialog(activity: AppCompatActivity, message: String, listener: DialogCallbacks) {
        val dialog = Dialog(activity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_report_user)
        val edtReason: EditText = dialog.findViewById(R.id.edt_reason)!!
        val txtYes: TextView = dialog.findViewById(R.id.txt_yes)!!
        val txtNo: TextView = dialog.findViewById(R.id.txt_no)!!
        val txtTitle: TextView = dialog.findViewById(R.id.txt_title)!!
        txtTitle.text = message

        txtYes.setOnClickListener {
            if (edtReason.text.trim().isEmpty()) {
                edtReason.error = activity.resources.getString(R.string.enter_reason)
                return@setOnClickListener
            }
            listener.onDefaultButtonClick(edtReason.text.toString())
            dialog.dismiss()
        }

        txtNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showChangeEmailDialog(activity: AppCompatActivity, listener: DialogCallbacks) {
        val dialog = Dialog(activity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_change_email)
        val edtEmail: EditText = dialog.findViewById(R.id.edt_email)!!
        val inputEmail: TextInputLayout = dialog.findViewById(R.id.input_layout_email)!!
        val txtConfirm: TextView = dialog.findViewById(R.id.txt_confirm)!!

        txtConfirm.setOnClickListener {
            if (edtEmail.text.toString().trim().isNullOrBlank()) {
                inputEmail.error = activity.resources.getString(R.string.enter_email_address)
                return@setOnClickListener
            } else if (!isValidEmail(edtEmail.text.toString().trim())) {
                inputEmail.error = activity.resources.getString(R.string.enter_valid_address)
                return@setOnClickListener
            }
            listener.onDefaultButtonClick(edtEmail.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showChangePasswordDialog(activity: AppCompatActivity, listener: ConfirmationDialogCallbacks) {
        val dialog = Dialog(activity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_change_password)
        val edtOldPassword: EditText = dialog.findViewById(R.id.edt_old_password)!!
        val edtNewPassword: EditText = dialog.findViewById(R.id.edt_new_password)!!
        val edtConfirmPassword: EditText = dialog.findViewById(R.id.edt_confirm_password)!!
        val inputOldPassword: TextInputLayout = dialog.findViewById(R.id.input_old_password)!!
        val inputNewPassword: TextInputLayout = dialog.findViewById(R.id.input_new_password)!!
        val inputConfirmPassword: TextInputLayout = dialog.findViewById(R.id.input_confirm_password)!!
        val txtConfirm: TextView = dialog.findViewById(R.id.txt_confirm)!!

        edtOldPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputOldPassword.isErrorEnabled = false
                inputOldPassword.endIconDrawable!!.setTint(activity.resources.getColor(R.color.black))
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        edtNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputNewPassword.isErrorEnabled = false
                inputNewPassword.endIconDrawable!!.setTint(activity.resources.getColor(R.color.black))
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputConfirmPassword.isErrorEnabled = false
                inputConfirmPassword.endIconDrawable!!.setTint(activity.resources.getColor(R.color.black))
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        txtConfirm.setOnClickListener {
            if (edtOldPassword.text.toString().trim().isNullOrBlank()) {
                inputOldPassword.error = activity.resources.getString(R.string.enter_password)
                inputOldPassword.endIconDrawable!!.setTint(activity.resources.getColor(R.color.red))
                return@setOnClickListener
            }
            if (edtNewPassword.text.toString().trim().isNullOrBlank()) {
                inputNewPassword.error = activity.resources.getString(R.string.enter_new_password)
                inputNewPassword.endIconDrawable!!.setTint(activity.resources.getColor(R.color.red))
                return@setOnClickListener
            }
            if (edtConfirmPassword.text.toString().trim().isNullOrBlank()) {
                inputConfirmPassword.error = activity.resources.getString(R.string.enter_confirm_password)
                inputConfirmPassword.endIconDrawable!!.setTint(activity.resources.getColor(R.color.red))
                return@setOnClickListener
            }
            if (edtNewPassword.text.toString().trim().length < 6) {
                inputNewPassword.error = activity.resources.getString(R.string.please_enter_minimum_password)
                inputNewPassword.endIconDrawable!!.setTint(activity.resources.getColor(R.color.red))
                return@setOnClickListener
            }
            if (edtConfirmPassword.text.toString().trim().length < 6) {
                inputConfirmPassword.error = activity.resources.getString(R.string.please_enter_minimum_password)
                inputConfirmPassword.endIconDrawable!!.setTint(activity.resources.getColor(R.color.red))
                return@setOnClickListener
            }
            if (edtConfirmPassword.text.toString().trim() != edtNewPassword.text.toString().trim()) {
                inputConfirmPassword.error = activity.resources.getString(R.string.password_and_confirm_password_not_match)
                inputConfirmPassword.endIconDrawable!!.setTint(activity.resources.getColor(R.color.red))
                return@setOnClickListener
            }
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.current to edtOldPassword.text.toString().trim(),
                Constants.password to edtNewPassword.text.toString().trim()
            )
            listener.onConfirmButtonClick(hashMap)
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showMemberClickIOSDialogIfAdmin(activity: AppCompatActivity, title: String, message: String, is_admin: Int, listener: DialogCallbacks) {
        val alert = AlertView(title, message, AlertStyle.IOS)
        alert.setCancelButtonText(activity.getString(R.string.cancel),activity.resources.getColor(com.irozon.alertview.R.color.black))

        alert.addAction(AlertAction(MemberClickIfAdminDialogActionName.VIEW_PROFILE.value, AlertActionStyle.DEFAULT) {
            listener.onDefaultButtonClick(MemberClickIfAdminDialogActionName.VIEW_PROFILE.value)
        })
        alert.addAction(AlertAction(MemberClickIfAdminDialogActionName.SEND_MESSAGE.value, AlertActionStyle.DEFAULT) {
            listener.onDefaultButtonClick(MemberClickIfAdminDialogActionName.SEND_MESSAGE.value)
        })
        if (is_admin == 0) {
            alert.addAction(AlertAction(MemberClickIfAdminDialogActionName.MAKE_GROUP_CAPTAIN.value, AlertActionStyle.DEFAULT) {
                listener.onDefaultButtonClick(MemberClickIfAdminDialogActionName.MAKE_GROUP_CAPTAIN.value)
            })
        } else {
            alert.addAction(AlertAction(MemberClickIfAdminDialogActionName.DISMISS_AS_CAPTAIN.value, AlertActionStyle.DEFAULT) {
                listener.onDefaultButtonClick(MemberClickIfAdminDialogActionName.DISMISS_AS_CAPTAIN.value)
            })
        }
        alert.addAction(AlertAction(MemberClickIfAdminDialogActionName.REMOVE_FROM_GROUP.value, AlertActionStyle.NEGATIVE) {
            listener.onDefaultButtonClick(MemberClickIfAdminDialogActionName.REMOVE_FROM_GROUP.value)
        })
        alert.show(activity)
    }

    fun showMemberClickIOSDialog(activity: AppCompatActivity, title: String, message: String, listener: DialogCallbacks) {
        val alert = AlertView(title, message, AlertStyle.IOS)
        alert.setCancelButtonText(activity.getString(R.string.cancel),activity.resources.getColor(com.irozon.alertview.R.color.red))

        alert.addAction(AlertAction(MemberClickIfAdminDialogActionName.VIEW_PROFILE.value, AlertActionStyle.DEFAULT) {
            listener.onDefaultButtonClick(MemberClickIfAdminDialogActionName.VIEW_PROFILE.value)
        })
        alert.addAction(AlertAction(MemberClickIfAdminDialogActionName.SEND_MESSAGE.value, AlertActionStyle.DEFAULT) {
            listener.onDefaultButtonClick(MemberClickIfAdminDialogActionName.SEND_MESSAGE.value)
        })
        alert.show(activity)

    }

    interface DialogCallbacks {
        fun onPositiveButtonClick()
        fun onNegativeButtonClick()
        fun onDefaultButtonClick(actionName: String)
    }

    interface ConfirmationDialogCallbacks {
        fun onConfirmButtonClick(requestBody: HashMap<String, Any>)
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

    enum class MemberClickIfAdminDialogActionName(val value: String) {
        VIEW_PROFILE("View Profile"),
        SEND_MESSAGE("Send Message"),
        MAKE_GROUP_CAPTAIN("Make group captain"),
        DISMISS_AS_CAPTAIN("Dismiss group captain"),
        REMOVE_FROM_GROUP("Remove from group")
    }

    enum class VibrationDialogActionName(val value: String) {
        OFF("Off"),
        DEFAULT("Default"),
        SHORT("Short"),
        LONG("Long");

        companion object {
            fun getObjectFromName(name: String): VibrationDialogActionName {
                VibrationDialogActionName.values().forEach {
                    if (it.value == name) {
                        return it

                    }
                }
                return DEFAULT
            }
        }
    }

    enum class ChatAttachmentActionsName(val value: String, val resouceId: Int) {
        CAMERA("Camera", R.drawable.ic_camera),
        PHOTOS("Photos", R.drawable.ic_photo),
        DOCUMENTS("Documents", R.drawable.ic_document),
        CONTACTS("Contacts", R.drawable.ic_contact),
        LOCATION("Location", R.drawable.ic_location_chat);

        companion object {
            fun getObjectFromName(name: String): ChatAttachmentActionsName? {
                ChatAttachmentActionsName.values().forEach {
                    if (it.value == name) {
                        return it

                    }
                }
                return null
            }
        }
    }


}
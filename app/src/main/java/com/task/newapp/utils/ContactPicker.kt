package com.task.newapp.utils

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

data class PickedContact(val number: String, val name: String?, val email: ArrayList<String>?)

class ContactPicker constructor(private val requestCode: Int = 23) : Fragment() {

    private lateinit var onContactPicked: (PickedContact) -> Unit
    private lateinit var onFailure: (Throwable) -> Unit

    companion object {

        private const val TAG = "ContactPicker"

        fun create(
            activity: AppCompatActivity,
            onContactPicked: (PickedContact) -> Unit,
            onFailure: (Throwable) -> Unit
        ): ContactPicker? {

            return try {
                val picker = ContactPicker()
                picker.onContactPicked = onContactPicked
                picker.onFailure = onFailure
                activity.supportFragmentManager.beginTransaction()
                    .add(picker, TAG)
                    .commitNowAllowingStateLoss()

                picker
            } catch (e: Exception) {
                onFailure(e)
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return null
    }

    fun pick() {
        try {
            Intent().apply {
                data = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                action = Intent.ACTION_PICK
                startActivityForResult(this, requestCode)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                this.requestCode -> {
                    var cursor: Cursor? = null
                    try {
                        cursor = data?.data.let { uri ->
                            uri as Uri
                            activity?.contentResolver?.query(uri, null, null, null, null)
                        }
                        cursor?.let {
                            it.moveToFirst()
                            val phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                            var email = ArrayList<String>()
                            cursor = activity?.contentResolver?.query(
                                ContactsContract.Data.CONTENT_URI, arrayOf(
                                    ContactsContract.Data.DISPLAY_NAME,
                                    ContactsContract.Contacts.Data.DATA1,
                                    ContactsContract.Contacts.Data.MIMETYPE
                                ), ContactsContract.Data.DISPLAY_NAME + " = ?", arrayOf<String>(name), null
                            )
                            cursor?.let { cursor1 ->
                                if (cursor1.moveToFirst()) {
                                    // Get the indexes of the MIME type and data
                                    // Match the data to the MIME type, store in variables
                                    do {
                                        val mime = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.Data.MIMETYPE))
                                        if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(mime, ignoreCase = true)) {
                                            email.add(cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.Data.DATA1)))
                                        }
                                        /*  if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mime, ignoreCase = true)) {
                                              var phone = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.Data.DATA1))
                                              phone = PhoneNumberUtils.formatNumber(phone)
                                              showLog("NUMBER:::: ", phone)
                                          }*/
                                    } while (cursor1.moveToNext())
                                }

                                showLog("EMAIL:::: ", email.toString())
                            }
                            it.close()
                            onContactPicked(PickedContact(phoneNumber, name, email))
                        }

                    } catch (e: Exception) {
                        onFailure(e)
                        cursor?.close()
                    }
/*
                    var cursor: Cursor? = null// Cursor object
                    var mime: String? // MIME type
                    val dataIdx: Int // Index of DATA1 column
                    val mimeIdx: Int // Index of MIMETYPE column
                    val nameIdx: Int // Index of DISPLAY_NAME column


                    // Get the name
                    cursor = data?.data.let { uri ->
                        uri as Uri
                        activity?.contentResolver?.query(uri, arrayOf(ContactsContract.Contacts.DISPLAY_NAME), null, null, null)
                    }
                    cursor?.let {

                        if (it.moveToFirst()) {
                            nameIdx = it.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME
                            )
                            val name = it.getString(nameIdx)
                            showLog("NAME :::", name)
                            // Set up the projection
                            val projection = arrayOf(
                                ContactsContract.Data.DISPLAY_NAME,
                                ContactsContract.Contacts.Data.DATA1,
                                ContactsContract.Contacts.Data.MIMETYPE
                            )

                            // Query ContactsContract.Data
                            cursor = activity?.contentResolver?.query(
                                ContactsContract.Data.CONTENT_URI, projection,
                                ContactsContract.Data.DISPLAY_NAME + " = ?", arrayOf<String>(name),
                                null
                            )
                            cursor?.let { cursor1 ->
                                if (cursor1.moveToFirst()) {
                                    // Get the indexes of the MIME type and data
                                    mimeIdx = cursor1.getColumnIndex(
                                        ContactsContract.Contacts.Data.MIMETYPE
                                    )
                                    dataIdx = cursor1.getColumnIndex(
                                        ContactsContract.Contacts.Data.DATA1
                                    )

                                    // Match the data to the MIME type, store in variables
                                    do {
                                        mime = cursor1.getString(mimeIdx)
                                        if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(mime, ignoreCase = true)) {
                                            val email = cursor1.getString(dataIdx)
                                            showLog("EMAIL:::: ", email)
                                        }
                                        if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mime, ignoreCase = true)) {
                                            var phone = cursor1.getString(dataIdx)
                                            phone = PhoneNumberUtils.formatNumber(phone)
                                            showLog("NUMBER:::: ", phone)
                                        }
                                    } while (cursor1.moveToNext())
                                }

                            }

                        }
                    }
                }*/
                }
            }
        }

    }
}
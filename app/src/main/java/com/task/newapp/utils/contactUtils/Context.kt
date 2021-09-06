package com.task.newapp.utils.contactUtils

import android.content.Context
import com.task.newapp.utils.contactUtils.extensions.hasPermission

import com.task.newapp.R
import com.task.newapp.models.contact.ContactSource
import com.task.newapp.utils.ContactsConstants
val Context.config: Config get() = Config.newInstance(applicationContext)


fun Context.hasContactPermissions() =
    hasPermission(ContactsConstants.PERMISSION_READ_CONTACTS) && hasPermission(ContactsConstants.PERMISSION_WRITE_CONTACTS)


fun Context.getVisibleContactSources(): ArrayList<String> {
    val sources = getAllContactSources()
    val ignoredContactSources = config.ignoredContactSources
    return ArrayList(sources).filter { !ignoredContactSources.contains(it.getFullIdentifier()) }
        .map { it.name }.toMutableList() as ArrayList<String>
}

fun Context.getAllContactSources(): ArrayList<ContactSource> {
    val sources = ContactsHelper(this).getDeviceContactSources()
    sources.add(getPrivateContactSource())
    return sources.toMutableList() as ArrayList<ContactSource>
}

fun Context.getPrivateContactSource() =
    ContactSource(ContactsConstants.SMT_PRIVATE,ContactsConstants. SMT_PRIVATE, getString(R.string.phone_storage_hidden))

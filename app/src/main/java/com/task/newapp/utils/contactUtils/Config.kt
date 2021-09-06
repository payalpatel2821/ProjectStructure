package com.task.newapp.utils.contactUtils

import android.content.Context
import com.task.newapp.utils.ContactsConstants.Companion.IGNORED_CONTACT_SOURCES
import com.task.newapp.utils.ContactsConstants.Companion.SHOW_ONLY_CONTACTS_WITH_NUMBERS
import com.task.newapp.utils.ContactsConstants.Companion.WAS_LOCAL_ACCOUNT_INITIALIZED
import com.task.newapp.utils.contactUtils.helpers.BaseConfig

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    var ignoredContactSources: HashSet<String>
        get() = prefs.getStringSet(IGNORED_CONTACT_SOURCES, hashSetOf(".")) as HashSet
        set(ignoreContactSources) = prefs.edit().remove(IGNORED_CONTACT_SOURCES).putStringSet(IGNORED_CONTACT_SOURCES, ignoreContactSources).apply()


    var showOnlyContactsWithNumbers: Boolean
        get() = prefs.getBoolean(SHOW_ONLY_CONTACTS_WITH_NUMBERS, false)
        set(showOnlyContactsWithNumbers) = prefs.edit().putBoolean(SHOW_ONLY_CONTACTS_WITH_NUMBERS, showOnlyContactsWithNumbers).apply()


    var wasLocalAccountInitialized: Boolean
        get() = prefs.getBoolean(WAS_LOCAL_ACCOUNT_INITIALIZED, false)
        set(wasLocalAccountInitialized) = prefs.edit().putBoolean(WAS_LOCAL_ACCOUNT_INITIALIZED, wasLocalAccountInitialized).apply()

}

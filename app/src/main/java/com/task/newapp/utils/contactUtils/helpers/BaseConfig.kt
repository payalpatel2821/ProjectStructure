package com.task.newapp.utils.contactUtils.helpers

import android.content.Context
import com.task.newapp.utils.ContactsConstants
import com.task.newapp.utils.contactUtils.extensions.getSDCardPath
import com.task.newapp.utils.contactUtils.extensions.getSharedPrefs

open class BaseConfig(val context: Context) {
    protected val prefs = context.getSharedPrefs()

    companion object {
        fun newInstance(context: Context) = BaseConfig(context)
    }


    var OTGTreeUri: String
        get() = prefs.getString(ContactsConstants.OTG_TREE_URI, "")!!
        set(OTGTreeUri) = prefs.edit().putString(ContactsConstants.OTG_TREE_URI, OTGTreeUri).apply()

    var OTGPartition: String
        get() = prefs.getString(ContactsConstants.OTG_PARTITION, "")!!
        set(OTGPartition) = prefs.edit().putString(ContactsConstants.OTG_PARTITION, OTGPartition).apply()

    var OTGPath: String
        get() = prefs.getString(ContactsConstants.OTG_REAL_PATH, "")!!
        set(OTGPath) = prefs.edit().putString(ContactsConstants.OTG_REAL_PATH, OTGPath).apply()

    var sdCardPath: String
        get() = prefs.getString(ContactsConstants.SD_CARD_PATH, getDefaultSDCardPath())!!
        set(sdCardPath) = prefs.edit().putString(ContactsConstants.SD_CARD_PATH, sdCardPath).apply()

    private fun getDefaultSDCardPath() = if (prefs.contains(ContactsConstants.SD_CARD_PATH)) "" else context.getSDCardPath()


    var sorting: Int
        get() = prefs.getInt(ContactsConstants.SORT_ORDER, 1)
        set(sorting) = prefs.edit().putInt(ContactsConstants.SORT_ORDER, sorting).apply()


    var startNameWithSurname: Boolean
        get() = prefs.getBoolean(ContactsConstants.START_NAME_WITH_SURNAME, false)
        set(startNameWithSurname) = prefs.edit().putBoolean(ContactsConstants.START_NAME_WITH_SURNAME, startNameWithSurname).apply()


}

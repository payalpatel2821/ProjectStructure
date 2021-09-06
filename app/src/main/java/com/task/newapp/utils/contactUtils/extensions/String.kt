package com.task.newapp.utils.contactUtils.extensions

import android.telephony.PhoneNumberUtils
import com.task.newapp.utils.ContactsConstants
import java.text.Normalizer

// remove diacritics, for example č -> c
fun String.normalizeString() = Normalizer.normalize(this, Normalizer.Form.NFD).replace(ContactsConstants.normalizeRegex, "")

// if we are comparing phone numbers, compare just the last 9 digits

fun String.normalizePhoneNumber() = PhoneNumberUtils.normalizeNumber(this)

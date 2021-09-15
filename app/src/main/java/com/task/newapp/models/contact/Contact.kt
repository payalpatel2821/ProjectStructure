package com.task.newapp.models.contact

import android.graphics.Bitmap
import com.task.newapp.utils.ContactsConstants
import com.task.newapp.utils.contactUtils.extensions.normalizeString
import com.task.newapp.utils.contactUtils.IM
import com.task.newapp.utils.contactUtils.Organization
import com.task.newapp.utils.contactUtils.PhoneNumber
import com.task.newapp.utils.contactUtils.helpers.*

data class Contact(
    var id: Int,
    var prefix: String,
    var firstName: String,
    var middleName: String,
    var surname: String,
    var suffix: String,
    var nickname: String,
    var photoUri: String,
    var phoneNumbers: ArrayList<String>,
    var emails: ArrayList<String>,
    var fullName:String,
//    var addresses: ArrayList<Address>,
 //   var events: ArrayList<Event>,
    var source: String,
//    var starred: Int,
//    var contactId: Int,
 //   var thumbnailUri: String,
 //   var photo: Bitmap?,
//    var notes: String,
//    var groups: ArrayList<Group>,
//    var organization: Organization,
//    var websites: ArrayList<String>,
 //   var IMs: ArrayList<IM>,
//    var mimetype: String,
//    var ringtone: String?
) :
    Comparable<Contact> {
    companion object {
        var sorting = 0
        var startWithSurname = false
        fun getNameToDisplay(firstName: String,middleName: String,surname: String,prefix: String,suffix: String,startWithSurname:Boolean,emails: ArrayList<String>): String {
            val firstMiddle = "$firstName $middleName".trim()
            val firstPart = if (startWithSurname) surname else firstMiddle
            val lastPart = if (startWithSurname) firstMiddle else surname
            val suffixComma = if (suffix.isEmpty()) "" else ", $suffix"
            val fullName = "$prefix $firstPart $lastPart$suffixComma".trim()
            return if (fullName.isEmpty()) {
//            if (organization.isNotEmpty()) {
//                getFullCompany()
//            } else {
                emails.firstOrNull()?.trim() ?: ""
//            }
            } else {
                fullName
            }
        }
    }

    override fun compareTo(other: Contact): Int {
        var result = when {
            sorting and ContactsConstants.SORT_BY_FIRST_NAME != 0 -> {
                val firstString = firstName.normalizeString()
                val secondString = other.firstName.normalizeString()
                compareUsingStrings(firstString, secondString, other)
            }
            sorting and ContactsConstants.SORT_BY_MIDDLE_NAME != 0 -> {
                val firstString = middleName.normalizeString()
                val secondString = other.middleName.normalizeString()
                compareUsingStrings(firstString, secondString, other)
            }
            sorting and ContactsConstants.SORT_BY_SURNAME != 0 -> {
                val firstString = surname.normalizeString()
                val secondString = other.surname.normalizeString()
                compareUsingStrings(firstString, secondString, other)
            }
            sorting and ContactsConstants.SORT_BY_FULL_NAME != 0 -> {
                val firstString = getNameToDisplay(firstName,middleName,surname,prefix,suffix, startWithSurname,emails).normalizeString()
                val secondString = getNameToDisplay(firstName,middleName,surname,prefix,suffix, startWithSurname,emails).normalizeString()
                compareUsingStrings(firstString, secondString, other)
            }
            else -> compareUsingIds(other)
        }

        if (sorting and ContactsConstants.SORT_DESCENDING != 0) {
            result *= -1
        }

        return result
    }

    private fun compareUsingStrings(
        firstString: String,
        secondString: String,
        other: Contact
    ): Int {
        var firstValue = firstString
        var secondValue = secondString

        if (firstValue.isEmpty() && firstName.isEmpty() && middleName.isEmpty() && surname.isEmpty()) {
          /*  val fullCompany = getFullCompany()
            if (fullCompany.isNotEmpty()) {
                firstValue = fullCompany.normalizeString()
            } else*/
                if (emails.isNotEmpty()) {
                firstValue = emails.first()
            }
        }

        if (secondValue.isEmpty() && other.firstName.isEmpty() && other.middleName.isEmpty() && other.surname.isEmpty()) {
           /* val otherFullCompany = other.getFullCompany()
            if (otherFullCompany.isNotEmpty()) {
                secondValue = otherFullCompany.normalizeString()
            } else*/
                if (other.emails.isNotEmpty()) {
                secondValue = other.emails.first()
            }
        }

        return if (firstValue.firstOrNull()?.isLetter() == true && secondValue.firstOrNull()
                ?.isLetter() == false
        ) {
            -1
        } else if (firstValue.firstOrNull()?.isLetter() == false && secondValue.firstOrNull()
                ?.isLetter() == true
        ) {
            1
        } else {
            if (firstValue.isEmpty() && secondValue.isNotEmpty()) {
                1
            } else if (firstValue.isNotEmpty() && secondValue.isEmpty()) {
                -1
            } else {
                if (firstValue.equals(secondValue, ignoreCase = true)) {
                    getNameToDisplay(firstName,middleName,surname,prefix,suffix, startWithSurname,emails).compareTo(getNameToDisplay(firstName,middleName,surname,prefix,suffix, startWithSurname,emails), true)
                } else {
                    firstValue.compareTo(secondValue, true)
                }
            }
        }
    }

    private fun compareUsingIds(other: Contact): Int {
        val firstId = id
        val secondId = other.id

        return firstId.compareTo(secondId)
    }



    fun getStringToCompare(): String {
        return copy(
            id = 0,
            prefix = "",
            firstName = getNameToDisplay(firstName,middleName,surname,prefix,suffix, startWithSurname,emails).toLowerCase(),
            middleName = "",
            surname = "",
            suffix = "",
            nickname = "",
            photoUri = "",
            phoneNumbers = ArrayList(),
            emails = ArrayList(),
//            events = ArrayList(),
            source = "",
//            addresses = ArrayList(),
//            starred = 0,
//            contactId = 0,
//            thumbnailUri = "",
//            notes = "",
//            groups = ArrayList(),
//            websites = ArrayList(),
//            organization = Organization("", ""),
//            IMs = ArrayList(),
//            ringtone = ""
        ).toString()
    }


  /*  private fun getFullCompany(): String {
        var fullOrganization =
            if (organization.company.isEmpty()) "" else "${organization.company}, "
        fullOrganization += organization.jobPosition
        return fullOrganization.trim().trimEnd(',')
    }*/

}

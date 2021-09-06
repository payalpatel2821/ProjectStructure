package com.task.newapp.utils.contactUtils

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.*
import android.provider.ContactsContract.CommonDataKinds.*
import android.text.TextUtils
import android.util.SparseArray
import com.simplemobiletools.commons.overloads.times
import com.task.newapp.R
import com.task.newapp.models.contact.*
import com.task.newapp.models.contact.Email
import com.task.newapp.models.contact.Event
import com.task.newapp.utils.ContactsConstants
import com.task.newapp.utils.ContactsConstants.Companion.SORT_BY_FIRST_NAME
import com.task.newapp.utils.ContactsConstants.Companion.SORT_BY_FULL_NAME
import com.task.newapp.utils.ContactsConstants.Companion.SORT_BY_MIDDLE_NAME
import com.task.newapp.utils.ContactsConstants.Companion.SORT_BY_SURNAME
import com.task.newapp.utils.ContactsConstants.Companion.ensureBackgroundThread
import com.task.newapp.utils.contactUtils.extensions.getIntValue
import com.task.newapp.utils.contactUtils.extensions.getStringValue
import com.task.newapp.utils.contactUtils.extensions.normalizePhoneNumber
import com.task.newapp.utils.contactUtils.extensions.queryCursor
import com.task.newapp.utils.contactUtils.helpers.*
import java.util.*
import kotlin.collections.ArrayList

class ContactsHelper(val context: Context) {
    private var displayContactSources = ArrayList<String>()

    fun getContacts(
        getAll: Boolean = false,
        gettingDuplicates: Boolean = false,
        ignoredContactSources: HashSet<String> = HashSet(),
        callback: (ArrayList<Contact>) -> Unit
    ) {
        ensureBackgroundThread {
            val contacts = SparseArray<Contact>()
            displayContactSources = context.getVisibleContactSources()

            if (getAll) {
                displayContactSources = if (ignoredContactSources.isEmpty()) {
                    context.getAllContactSources().map { it.name }.toMutableList() as ArrayList
                } else {
                    context.getAllContactSources().filter {
                        it.getFullIdentifier()
                            .isNotEmpty() && !ignoredContactSources.contains(it.getFullIdentifier())
                    }.map { it.name }.toMutableList() as ArrayList
                }
            }

            getDeviceContacts(contacts, ignoredContactSources, gettingDuplicates)

            val contactsSize = contacts.size()
            val showOnlyContactsWithNumbers = context.config.showOnlyContactsWithNumbers
            val tempContacts = ArrayList<Contact>(contactsSize)
            val resultContacts = ArrayList<Contact>(contactsSize)

            (0 until contactsSize).filter {
                if (ignoredContactSources.isEmpty() && showOnlyContactsWithNumbers) {
                    contacts.valueAt(it).phoneNumbers.isNotEmpty()
                } else {
                    true
                }
            }.mapTo(tempContacts) {
                contacts.valueAt(it)
            }

            if (ignoredContactSources.isEmpty() && !getAll) {
                tempContacts.filter { displayContactSources.contains(it.source) }
                    .groupBy { it.getNameToDisplay().toLowerCase() }.values.forEach { it ->
                        if (it.size == 1) {
                            resultContacts.add(it.first())
                        } else {
                            val sorted = it.sortedByDescending { it.getStringToCompare().length }
                            resultContacts.add(sorted.first())
                        }
                    }
            } else {
                resultContacts.addAll(tempContacts)
            }

            Contact.sorting = context.config.sorting
            Contact.startWithSurname = context.config.startNameWithSurname
            resultContacts.sort()

            Handler(Looper.getMainLooper()).post {
                callback(resultContacts)
            }
        }
    }

    private fun getContentResolverAccounts(): HashSet<ContactSource> {
        val sources = HashSet<ContactSource>()
        arrayOf(Groups.CONTENT_URI, Settings.CONTENT_URI, RawContacts.CONTENT_URI).forEach {
            fillSourcesFromUri(it, sources)
        }

        return sources
    }

    private fun fillSourcesFromUri(uri: Uri, sources: HashSet<ContactSource>) {
        val projection = arrayOf(
            RawContacts.ACCOUNT_NAME,
            RawContacts.ACCOUNT_TYPE
        )

        context.queryCursor(uri, projection) { cursor ->
            val name = cursor.getStringValue(RawContacts.ACCOUNT_NAME) ?: ""
            val type = cursor.getStringValue(RawContacts.ACCOUNT_TYPE) ?: ""
            var publicName = name
            if (type == ContactsConstants.TELEGRAM_PACKAGE) {
                publicName = context.getString(R.string.telegram)
            }

            val source = ContactSource(name, type, publicName)
            sources.add(source)
        }
    }

    private fun getDeviceContacts(
        contacts: SparseArray<Contact>,
        ignoredContactSources: HashSet<String>?,
        gettingDuplicates: Boolean
    ) {
        if (!context.hasContactPermissions()) {
            return
        }

        val ignoredSources = ignoredContactSources ?: context.config.ignoredContactSources
        val uri = Data.CONTENT_URI
        val projection = getContactProjection()

        arrayOf(
            CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
            StructuredName.CONTENT_ITEM_TYPE
        ).forEach { mimetype ->
            val selection = "${Data.MIMETYPE} = ?"
            val selectionArgs = arrayOf(mimetype)
            val sortOrder = getSortString()

            context.queryCursor(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder,
                true
            ) { cursor ->
                val accountName = cursor.getStringValue(RawContacts.ACCOUNT_NAME) ?: ""
                val accountType = cursor.getStringValue(RawContacts.ACCOUNT_TYPE) ?: ""

                if (ignoredSources.contains("$accountName:$accountType")) {
                    return@queryCursor
                }

                val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
                var prefix = ""
                var firstName = ""
                var middleName = ""
                var surname = ""
                var suffix = ""

                // ignore names at Organization type contacts
                if (mimetype == StructuredName.CONTENT_ITEM_TYPE) {
                    prefix = cursor.getStringValue(StructuredName.PREFIX) ?: ""
                    firstName = cursor.getStringValue(StructuredName.GIVEN_NAME) ?: ""
                    middleName = cursor.getStringValue(StructuredName.MIDDLE_NAME) ?: ""
                    surname = cursor.getStringValue(StructuredName.FAMILY_NAME) ?: ""
                    suffix = cursor.getStringValue(StructuredName.SUFFIX) ?: ""
                }

                var photoUri = ""
                var starred = 0
                var contactId = 0
                var thumbnailUri = ""
                var ringtone: String? = null

                if (!gettingDuplicates) {
                    photoUri = cursor.getStringValue(StructuredName.PHOTO_URI) ?: ""
                    starred = cursor.getIntValue(StructuredName.STARRED)
                    contactId = cursor.getIntValue(Data.CONTACT_ID)
                    thumbnailUri = cursor.getStringValue(StructuredName.PHOTO_THUMBNAIL_URI) ?: ""
                    ringtone = cursor.getStringValue(StructuredName.CUSTOM_RINGTONE)
                }

                val nickname = ""
                val numbers = ArrayList<PhoneNumber>()          // proper value is obtained below
                val emails = ArrayList<Email>()
                val addresses = ArrayList<Address>()
                val events = ArrayList<Event>()
                val notes = ""
                val organization = Organization("", "")
                val websites = ArrayList<String>()
                val ims = ArrayList<IM>()
                val contact = Contact(
                    id,
                    prefix,
                    firstName,
                    middleName,
                    surname,
                    suffix,
                    nickname,
                    photoUri,
                    numbers,
                    emails,
                    addresses,
                    events,
                    accountName,
                    starred,
                    contactId,
                    thumbnailUri,
                    null,
                    notes,
                    organization,
                    websites,
                    ims,
                    mimetype,
                    ringtone
                )

                contacts.put(id, contact)
            }
        }

        val emails = getEmails()
        var size = emails.size()
        for (i in 0 until size) {
            val key = emails.keyAt(i)
            contacts[key]?.emails = emails.valueAt(i)
        }

        val organizations = getOrganizations()
        size = organizations.size()
        for (i in 0 until size) {
            val key = organizations.keyAt(i)
            contacts[key]?.organization = organizations.valueAt(i)
        }

        // no need to fetch some fields if we are only getting duplicates of the current contact
        if (gettingDuplicates) {
            return
        }

        val phoneNumbers = getPhoneNumbers(null)
        size = phoneNumbers.size()
        for (i in 0 until size) {
            val key = phoneNumbers.keyAt(i)
            if (contacts[key] != null) {
                val numbers = phoneNumbers.valueAt(i)
                contacts[key].phoneNumbers = numbers
            }
        }

        val addresses = getAddresses()
        size = addresses.size()
        for (i in 0 until size) {
            val key = addresses.keyAt(i)
            contacts[key]?.addresses = addresses.valueAt(i)
        }

        val IMs = getIMs()
        size = IMs.size()
        for (i in 0 until size) {
            val key = IMs.keyAt(i)
            contacts[key]?.IMs = IMs.valueAt(i)
        }

        val events = getEvents()
        size = events.size()
        for (i in 0 until size) {
            val key = events.keyAt(i)
            contacts[key]?.events = events.valueAt(i)
        }

        val notes = getNotes()
        size = notes.size()
        for (i in 0 until size) {
            val key = notes.keyAt(i)
            contacts[key]?.notes = notes.valueAt(i)
        }

        val websites = getWebsites()
        size = websites.size()
        for (i in 0 until size) {
            val key = websites.keyAt(i)
            contacts[key]?.websites = websites.valueAt(i)
        }
    }

    private fun getPhoneNumbers(contactId: Int? = null): SparseArray<ArrayList<PhoneNumber>> {
        val phoneNumbers = SparseArray<ArrayList<PhoneNumber>>()
        val uri = Phone.CONTENT_URI
        val projection = arrayOf(
            Data.RAW_CONTACT_ID,
            Phone.NUMBER,
            Phone.NORMALIZED_NUMBER,
            Phone.TYPE,
            Phone.LABEL
        )

        val selection =
            if (contactId == null) getSourcesSelection() else "${Data.RAW_CONTACT_ID} = ?"
        val selectionArgs =
            if (contactId == null) getSourcesSelectionArgs() else arrayOf(contactId.toString())

        context.queryCursor(
            uri,
            projection,
            selection,
            selectionArgs,
            showErrors = true
        ) { cursor ->
            val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
            val number = cursor.getStringValue(Phone.NUMBER) ?: return@queryCursor
            val normalizedNumber =
                cursor.getStringValue(Phone.NORMALIZED_NUMBER) ?: number.normalizePhoneNumber()
            val type = cursor.getIntValue(Phone.TYPE)
            val label = cursor.getStringValue(Phone.LABEL) ?: ""

            if (phoneNumbers[id] == null) {
                phoneNumbers.put(id, ArrayList())
            }

            val phoneNumber = PhoneNumber(number, type, label, normalizedNumber)
            phoneNumbers[id].add(phoneNumber)
        }

        return phoneNumbers
    }

    private fun getEmails(contactId: Int? = null): SparseArray<ArrayList<Email>> {
        val emails = SparseArray<ArrayList<Email>>()
        val uri = CommonDataKinds.Email.CONTENT_URI
        val projection = arrayOf(
            Data.RAW_CONTACT_ID,
            CommonDataKinds.Email.DATA,
            CommonDataKinds.Email.TYPE,
            CommonDataKinds.Email.LABEL
        )

        val selection =
            if (contactId == null) getSourcesSelection() else "${Data.RAW_CONTACT_ID} = ?"
        val selectionArgs =
            if (contactId == null) getSourcesSelectionArgs() else arrayOf(contactId.toString())

        context.queryCursor(
            uri,
            projection,
            selection,
            selectionArgs,
            showErrors = true
        ) { cursor ->
            val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
            val email = cursor.getStringValue(CommonDataKinds.Email.DATA) ?: return@queryCursor
            val type = cursor.getIntValue(CommonDataKinds.Email.TYPE)
            val label = cursor.getStringValue(CommonDataKinds.Email.LABEL) ?: ""

            if (emails[id] == null) {
                emails.put(id, ArrayList())
            }

            emails[id]!!.add(Email(email, type, label))
        }

        return emails
    }

    private fun getAddresses(contactId: Int? = null): SparseArray<ArrayList<Address>> {
        val addresses = SparseArray<ArrayList<Address>>()
        val uri = StructuredPostal.CONTENT_URI
        val projection = arrayOf(
            Data.RAW_CONTACT_ID,
            StructuredPostal.FORMATTED_ADDRESS,
            StructuredPostal.TYPE,
            StructuredPostal.LABEL
        )

        val selection =
            if (contactId == null) getSourcesSelection() else "${Data.RAW_CONTACT_ID} = ?"
        val selectionArgs =
            if (contactId == null) getSourcesSelectionArgs() else arrayOf(contactId.toString())

        context.queryCursor(
            uri,
            projection,
            selection,
            selectionArgs,
            showErrors = true
        ) { cursor ->
            val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
            val address =
                cursor.getStringValue(StructuredPostal.FORMATTED_ADDRESS) ?: return@queryCursor
            val type = cursor.getIntValue(StructuredPostal.TYPE)
            val label = cursor.getStringValue(StructuredPostal.LABEL) ?: ""

            if (addresses[id] == null) {
                addresses.put(id, ArrayList())
            }

            addresses[id]!!.add(Address(address, type, label))
        }

        return addresses
    }

    private fun getIMs(contactId: Int? = null): SparseArray<ArrayList<IM>> {
        val IMs = SparseArray<ArrayList<IM>>()
        val uri = Data.CONTENT_URI
        val projection = arrayOf(
            Data.RAW_CONTACT_ID,
            Im.DATA,
            Im.PROTOCOL,
            Im.CUSTOM_PROTOCOL
        )

        val selection = getSourcesSelection(true, contactId != null)
        val selectionArgs = getSourcesSelectionArgs(Im.CONTENT_ITEM_TYPE, contactId)

        context.queryCursor(
            uri,
            projection,
            selection,
            selectionArgs,
            showErrors = true
        ) { cursor ->
            val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
            val IM = cursor.getStringValue(Im.DATA) ?: return@queryCursor
            val type = cursor.getIntValue(Im.PROTOCOL)
            val label = cursor.getStringValue(Im.CUSTOM_PROTOCOL) ?: ""

            if (IMs[id] == null) {
                IMs.put(id, ArrayList())
            }

            IMs[id]!!.add(IM(IM, type, label))
        }

        return IMs
    }

    private fun getEvents(contactId: Int? = null): SparseArray<ArrayList<Event>> {
        val events = SparseArray<ArrayList<Event>>()
        val uri = Data.CONTENT_URI
        val projection = arrayOf(
            Data.RAW_CONTACT_ID,
            CommonDataKinds.Event.START_DATE,
            CommonDataKinds.Event.TYPE
        )

        val selection = getSourcesSelection(true, contactId != null)
        val selectionArgs =
            getSourcesSelectionArgs(CommonDataKinds.Event.CONTENT_ITEM_TYPE, contactId)

        context.queryCursor(
            uri,
            projection,
            selection,
            selectionArgs,
            showErrors = true
        ) { cursor ->
            val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
            val startDate =
                cursor.getStringValue(CommonDataKinds.Event.START_DATE) ?: return@queryCursor
            val type = cursor.getIntValue(CommonDataKinds.Event.TYPE)

            if (events[id] == null) {
                events.put(id, ArrayList())
            }

            events[id]!!.add(Event(startDate, type))
        }

        return events
    }

    private fun getNotes(contactId: Int? = null): SparseArray<String> {
        val notes = SparseArray<String>()
        val uri = Data.CONTENT_URI
        val projection = arrayOf(
            Data.RAW_CONTACT_ID,
            Note.NOTE
        )

        val selection = getSourcesSelection(true, contactId != null)
        val selectionArgs = getSourcesSelectionArgs(Note.CONTENT_ITEM_TYPE, contactId)

        context.queryCursor(
            uri,
            projection,
            selection,
            selectionArgs,
            showErrors = true
        ) { cursor ->
            val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
            val note = cursor.getStringValue(Note.NOTE) ?: return@queryCursor
            notes.put(id, note)
        }

        return notes
    }

    private fun getOrganizations(contactId: Int? = null): SparseArray<Organization> {
        val organizations = SparseArray<Organization>()
        val uri = Data.CONTENT_URI
        val projection = arrayOf(
            Data.RAW_CONTACT_ID,
            CommonDataKinds.Organization.COMPANY,
            CommonDataKinds.Organization.TITLE
        )

        val selection = getSourcesSelection(true, contactId != null)
        val selectionArgs =
            getSourcesSelectionArgs(CommonDataKinds.Organization.CONTENT_ITEM_TYPE, contactId)

        context.queryCursor(
            uri,
            projection,
            selection,
            selectionArgs,
            showErrors = true
        ) { cursor ->
            val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
            val company = cursor.getStringValue(CommonDataKinds.Organization.COMPANY) ?: ""
            val title = cursor.getStringValue(CommonDataKinds.Organization.TITLE) ?: ""
            if (company.isEmpty() && title.isEmpty()) {
                return@queryCursor
            }

            val organization = Organization(company, title)
            organizations.put(id, organization)
        }

        return organizations
    }

    private fun getWebsites(contactId: Int? = null): SparseArray<ArrayList<String>> {
        val websites = SparseArray<ArrayList<String>>()
        val uri = Data.CONTENT_URI
        val projection = arrayOf(
            Data.RAW_CONTACT_ID,
            Website.URL
        )

        val selection = getSourcesSelection(true, contactId != null)
        val selectionArgs = getSourcesSelectionArgs(Website.CONTENT_ITEM_TYPE, contactId)

        context.queryCursor(
            uri,
            projection,
            selection,
            selectionArgs,
            showErrors = true
        ) { cursor ->
            val id = cursor.getIntValue(Data.RAW_CONTACT_ID)
            val url = cursor.getStringValue(Website.URL) ?: return@queryCursor

            if (websites[id] == null) {
                websites.put(id, ArrayList())
            }

            websites[id]!!.add(url)
        }

        return websites
    }


    private fun getQuestionMarks() =
        ("?," * displayContactSources.filter { it.isNotEmpty() }.size).trimEnd(',')

    private fun getSourcesSelection(
        addMimeType: Boolean = false,
        addContactId: Boolean = false,
        useRawContactId: Boolean = true
    ): String {
        val strings = ArrayList<String>()
        if (addMimeType) {
            strings.add("${Data.MIMETYPE} = ?")
        }

        if (addContactId) {
            strings.add("${if (useRawContactId) Data.RAW_CONTACT_ID else Data.CONTACT_ID} = ?")
        } else {
            // sometimes local device storage has null account_name, handle it properly
            val accountnameString = StringBuilder()
            if (displayContactSources.contains("")) {
                accountnameString.append("(")
            }
            accountnameString.append("${RawContacts.ACCOUNT_NAME} IN (${getQuestionMarks()})")
            if (displayContactSources.contains("")) {
                accountnameString.append(" OR ${RawContacts.ACCOUNT_NAME} IS NULL)")
            }
            strings.add(accountnameString.toString())
        }

        return TextUtils.join(" AND ", strings)
    }

    private fun getSourcesSelectionArgs(
        mimetype: String? = null,
        contactId: Int? = null
    ): Array<String> {
        val args = ArrayList<String>()

        if (mimetype != null) {
            args.add(mimetype)
        }

        if (contactId != null) {
            args.add(contactId.toString())
        } else {
            args.addAll(displayContactSources.filter { it.isNotEmpty() })
        }

        return args.toTypedArray()
    }


    fun getDeviceContactSources(): LinkedHashSet<ContactSource> {
        val sources = LinkedHashSet<ContactSource>()
        if (!context.hasContactPermissions()) {
            return sources
        }

        if (!context.config.wasLocalAccountInitialized) {
            initializeLocalPhoneAccount()
            context.config.wasLocalAccountInitialized = true
        }

        val accounts = AccountManager.get(context).accounts
        accounts.forEach {
            if (ContentResolver.getIsSyncable(it, AUTHORITY) == 1) {
                var publicName = it.name
                if (it.type == ContactsConstants.TELEGRAM_PACKAGE) {
                    publicName = context.getString(R.string.telegram)
                } else if (it.type == ContactsConstants.VIBER_PACKAGE) {
                    publicName = context.getString(R.string.viber)
                }
                val contactSource = ContactSource(it.name, it.type, publicName)
                sources.add(contactSource)
            }
        }

        val contentResolverAccounts = getContentResolverAccounts().filter {
            it.name.isNotEmpty() && it.type.isNotEmpty() && !accounts.contains(
                Account(
                    it.name,
                    it.type
                )
            )
        }
        sources.addAll(contentResolverAccounts)

        return sources
    }

    // make sure the local Phone contact source is initialized and available
    // https://stackoverflow.com/a/6096508/1967672
    private fun initializeLocalPhoneAccount() {
        try {
            val operations = ArrayList<ContentProviderOperation>()
            ContentProviderOperation.newInsert(RawContacts.CONTENT_URI).apply {
                withValue(RawContacts.ACCOUNT_NAME, null)
                withValue(RawContacts.ACCOUNT_TYPE, null)
                operations.add(build())
            }

            val results = context.contentResolver.applyBatch(AUTHORITY, operations)
            val rawContactUri = results.firstOrNull()?.uri ?: return
            context.contentResolver.delete(rawContactUri, null, null)
        } catch (ignored: Exception) {
        }
    }

    private fun getContactProjection() = arrayOf(
        Data.MIMETYPE,
        Data.CONTACT_ID,
        Data.RAW_CONTACT_ID,
        StructuredName.PREFIX,
        StructuredName.GIVEN_NAME,
        StructuredName.MIDDLE_NAME,
        StructuredName.FAMILY_NAME,
        StructuredName.SUFFIX,
        StructuredName.PHOTO_URI,
        StructuredName.PHOTO_THUMBNAIL_URI,
        StructuredName.STARRED,
        StructuredName.CUSTOM_RINGTONE,
        RawContacts.ACCOUNT_NAME,
        RawContacts.ACCOUNT_TYPE
    )

    private fun getSortString(): String {
        val sorting = context.config.sorting
        return when {
            sorting and SORT_BY_FIRST_NAME != 0 -> "${StructuredName.GIVEN_NAME} COLLATE NOCASE"
            sorting and SORT_BY_MIDDLE_NAME != 0 -> "${StructuredName.MIDDLE_NAME} COLLATE NOCASE"
            sorting and SORT_BY_SURNAME != 0 -> "${StructuredName.FAMILY_NAME} COLLATE NOCASE"
            sorting and SORT_BY_FULL_NAME != 0 -> StructuredName.DISPLAY_NAME
            else -> Data.RAW_CONTACT_ID
        }
    }

}

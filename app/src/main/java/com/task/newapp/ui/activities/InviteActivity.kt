package com.task.newapp.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.task.newapp.R
import com.task.newapp.adapter.contact.ContactAdapter
import com.task.newapp.databinding.ActivityInviteBinding
import com.task.newapp.models.ResponseMyContact
import com.task.newapp.models.contact.ContactRecyclerViewModel
import com.task.newapp.realmDB.getAllMyContact
import com.task.newapp.realmDB.models.MyContacts
import com.task.newapp.utils.showLog
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class InviteActivity : AppCompatActivity() {

    private var allContact: ArrayList<ResponseMyContact.Data> = ArrayList()
    private lateinit var adapter: ContactAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var inviteUserContacts: List<MyContacts> = ArrayList()
    lateinit var binding: ActivityInviteBinding
    private lateinit var countriesRecyclerViewModels: ArrayList<ContactRecyclerViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invite)

        binding.toolbarLayout.txtTitle.text = resources.getString(R.string.invite_friends)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        inviteUserContacts = getAllMyContact()!!.filter { it.isAppUser == 0 }
        linearLayoutManager = LinearLayoutManager(this)
        binding.rvList.layoutManager = linearLayoutManager
        adapter = ContactAdapter(this)
        binding.rvList.adapter = adapter
        allContact = prepareMyContactModel(inviteUserContacts, allContact)
        setAdapter(allContact)
        initSearchView()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun setAdapter(contacts: ArrayList<ResponseMyContact.Data>) {
        allContact = contacts
        (allContact as ArrayList<ResponseMyContact.Data>).sortBy { it.fullName }
        adapter.filterList(transformCountriesForRecyclerView(allContact) as ArrayList<ContactRecyclerViewModel>)
        if (allContact.isNullOrEmpty()) {
            binding.txtEmptyContact.visibility = VISIBLE
        } else {
            binding.txtEmptyContact.visibility = GONE
        }
        initSectionPicker()
        binding.rvList.visibility = View.VISIBLE
        binding.sectionpickerCountries.visibility = View.VISIBLE
    }

    private fun prepareMyContactModel(allMyContact: List<MyContacts>?, contacts: ArrayList<ResponseMyContact.Data>): ArrayList<ResponseMyContact.Data> {
        allMyContact!!.forEach { contact ->
            contacts.add(
                ResponseMyContact.Data(
                    contact.appUserId,
                    "",
                    contact.email ?: "",
                    contact.fullName,
                    contact.id,
                    contact.isAppUser,
                    contact.number ?: "",
                    "",
                    contact.profileColor ?: "",
                    contact.profileImage ?: "",
                    contact.registerType ?: "",
                    "", 0
                )
            )
        }
        return contacts
    }

    private fun transformCountriesForRecyclerView(myContacts: List<ResponseMyContact.Data>?): List<ContactRecyclerViewModel?>? {
        countriesRecyclerViewModels = ArrayList()
        if (myContacts != null && myContacts.isNotEmpty()) {
            var letter = ""
            for (contact in myContacts) {

                var contactLetter: String = ""
                contactLetter = if (contact.fullName.isNotEmpty()) {
                    contact.fullName.substring(0, 1)
                } else {
                    "A".substring(0, 1)
                }

                if (TextUtils.isEmpty(letter) || letter != contactLetter) {
                    countriesRecyclerViewModels.add(
                        ContactRecyclerViewModel(
                            null,
                            contactLetter,
                            adapter.TYPE_LETTER
                        )
                    )
                    letter = contactLetter
                }
                countriesRecyclerViewModels.add(
                    ContactRecyclerViewModel(
                        contact,
                        null,
                        adapter.TYPE_CONTACT
                    )
                )
            }
        }
        return countriesRecyclerViewModels
    }

    private fun initSectionPicker() {
        val sectionsAsObject = adapter.sections
        val sections: Array<String> = Arrays.copyOf(
            sectionsAsObject, sectionsAsObject.size,
            Array<String>::class.java
        )
        binding.sectionpickerCountries.textViewIndicator = binding.textviewSection
        binding.sectionpickerCountries.setSections(sections)
        binding.sectionpickerCountries.isNestedScrollingEnabled = true
        binding.sectionpickerCountries.setOnTouchingLetterChangedListener { s ->
            val position = adapter.getPositionForSection(s[0].toInt())
            if (position != -1) {
                linearLayoutManager.scrollToPositionWithOffset(position, 0)
            }
        }
    }

    private fun initSearchView() {
        binding.searchLayout.searchView.onActionViewExpanded()
        binding.searchLayout.searchView.clearFocus()

        binding.searchLayout.searchView.findViewById<View>(R.id.search_close_btn)
            .setOnClickListener(View.OnClickListener {
                closeClick()
            })

        val result = Observable
            .create(ObservableOnSubscribe<String> { subscriber ->
                binding.searchLayout.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(query: String?): Boolean {
                        subscriber.onNext(query!!)
                        return false
                    }

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        subscriber.onNext(query!!)
                        return false
                    }
                })
            })
            .map { text -> text.lowercase().trim() }
            .debounce(300, TimeUnit.MILLISECONDS)
            .subscribe({ query ->
                runOnUiThread {
                    filter(query!!)
                }
            }, { throwable -> showLog("subscribe_throwable", throwable.toString()) })

    }

    fun filter(query: String) {
        if (query != "") {
            try {
                val filteredList1: ArrayList<ResponseMyContact.Data> = ArrayList()
                for (item in prepareMyContactModel(inviteUserContacts!!, allContact)) {
                    if (item.fullName.lowercase().startsWith(query.lowercase())) {
                        filteredList1.add(item)
                    }
                }
                adapter.filterList(transformCountriesForRecyclerView(filteredList1) as ArrayList<ContactRecyclerViewModel>)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            setAdapter(allContact)
        }
    }

    private fun closeClick() {
        binding.searchLayout.searchView.isActivated = true
        binding.searchLayout.searchView.setQuery("", false)
        binding.searchLayout.searchView.isIconified = true
        binding.searchLayout.searchView.onActionViewExpanded()
        binding.searchLayout.searchView.clearFocus()
        binding.searchLayout.searchView.findViewById<View>(R.id.search_button).performClick()
        filter("")

    }
}
package com.task.newapp.ui.fragments.contact

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.task.newapp.App
import com.task.newapp.adapter.contact.ContactAdapter
import com.task.newapp.databinding.FragmentContactBinding
import com.task.newapp.models.ResponseMyContact
import com.task.newapp.models.contact.ContactRecyclerViewModel
import com.task.newapp.realmDB.getAllMyContact
import com.task.newapp.realmDB.models.MyContacts
import com.task.newapp.ui.activities.InviteActivity
import com.task.newapp.ui.activities.profile.OtherUserProfileActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.contactUtils.ContactsHelper
import com.task.newapp.workmanager.WorkManagerScheduler
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import kotlin.collections.ArrayList


/**
 * A placeholder fragment containing a simple view.
 **/
class MyContactFragment : Fragment()/*, Paginate.Callbacks */ {

    private var allContacts: List<MyContacts>? = ArrayList()
    var appUserContacts: List<MyContacts>? = ArrayList()
    private var inviteUserContacts: List<MyContacts>? = ArrayList()
    private var _binding: FragmentContactBinding? = null
    private lateinit var countriesRecyclerViewModels: ArrayList<ContactRecyclerViewModel>
    private lateinit var adapter: ContactAdapter
    private lateinit var adapter1: ContactAdapter
    private lateinit var adapter2: ContactAdapter
    var allContact: ArrayList<ResponseMyContact.Data> = ArrayList()
    var appContact: ArrayList<ResponseMyContact.Data> = ArrayList()
    var inviteContact: ArrayList<ResponseMyContact.Data> = ArrayList()
    lateinit var linearLayoutManager: LinearLayoutManager
    private val mCompositeDisposable = CompositeDisposable()
    private val binding get() = _binding!!
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentContactBinding.inflate(inflater, container, false)
        initView()
        return binding.root

    }

    private fun initView() {
        countriesRecyclerViewModels = ArrayList()

        linearLayoutManager = LinearLayoutManager(requireActivity())
        _binding!!.rvList.layoutManager = linearLayoutManager
        adapter = ContactAdapter(requireActivity())
        _binding!!.rvList.adapter = adapter
        adapter.onItemClick = { appUserId ->
            requireActivity().launchActivity<OtherUserProfileActivity> {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(Constants.user_id, appUserId)
            }
        }

        linearLayoutManager = LinearLayoutManager(requireActivity())
        _binding!!.rvAppUser.layoutManager = linearLayoutManager
        adapter1 = ContactAdapter(requireActivity())
        _binding!!.rvAppUser.adapter = adapter1
        adapter1.onItemClick = { appUserId ->
            requireActivity().launchActivity<OtherUserProfileActivity> {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(Constants.user_id, appUserId)
            }
        }

        linearLayoutManager = LinearLayoutManager(requireActivity())
        _binding!!.rvInviteUser.layoutManager = linearLayoutManager
        adapter2 = ContactAdapter(requireActivity())
        _binding!!.rvInviteUser.adapter = adapter2

        _binding!!.btnGetContact.setOnClickListener {
            activity.runWithPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS) {
                _binding!!.btnGetContact.visibility = GONE
                openProgressDialog(activity)
                getContact()
            }
        }

        _binding!!.sectionpickerCountries.visibility = GONE
        getFilterContact()
        allContact = prepareMyContactModel(appUserContacts, allContact)
//        if (!allContact.isNullOrEmpty()) {
//            setAdapter(allContact)
//        } else {
        activity.runWithPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS) {
            if (!allContact.isNullOrEmpty()) {
                setAdapter(allContact)
            } else {
                _binding!!.btnGetContact.visibility = GONE
                openProgressDialog(activity)
                getContact()
            }
        }

        binding.txtInvite.setOnClickListener {
            requireActivity().launchActivity<InviteActivity> {

            }
        }

//        }

        showHideView(false)
    }




    fun getFilterContact() {
        allContacts = getAllMyContact()
        appUserContacts = getAllMyContact()!!.filter { it.isAppUser == 1 }
        inviteUserContacts = getAllMyContact()!!.filter { it.isAppUser == 0 }
    }

    fun prepareMyContactModel(allMyContact: List<MyContacts>?, contacts: ArrayList<ResponseMyContact.Data>): ArrayList<ResponseMyContact.Data> {
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

    fun getContact() {
        ContactsHelper(requireActivity()).getContacts { (contacts, contactSync) ->
            hideProgressDialog()
            val json = gson.toJson(contactSync)
            App.fastSave.saveString(Constants.contact, json)
            WorkManagerScheduler.refreshPeriodicWorkContact(/*App.getAppInstance()*/requireActivity() as AppCompatActivity)
        }
    }

    fun setAdapter(contacts: ArrayList<ResponseMyContact.Data>) {
        allContact = contacts
        (allContact as ArrayList<ResponseMyContact.Data>).sortBy { it.fullName }
        adapter.filterList(transformCountriesForRecyclerView(allContact) as ArrayList<ContactRecyclerViewModel>)
//        if (allContact.isNullOrEmpty()) {
//            binding.txtEmptyContact.visibility = VISIBLE
//        } else {
//            binding.txtEmptyContact.visibility = GONE
//        }
        initSectionPicker()
        _binding!!.btnGetContact.isClickable = false
        _binding!!.btnGetContact.visibility = GONE
        _binding!!.rvList.visibility = VISIBLE
        _binding!!.sectionpickerCountries.visibility = VISIBLE
    }

    fun filter(query: String) {
        if (query != "") {
            appContact.clear()
            inviteContact.clear()
            showHideView(true)
            val filteredList: ArrayList<ResponseMyContact.Data> = ArrayList()
            for (item in prepareMyContactModel(appUserContacts!!, appContact)) {
                if (item.fullName.lowercase().startsWith(query.lowercase())) {
                    filteredList.add(item)
                }
            }
            adapter1.filterList(transformCountriesForRecyclerViewForSearch(filteredList) as ArrayList<ContactRecyclerViewModel>)

            if (filteredList.isNullOrEmpty()) {
                binding.rvAppUser.visibility = GONE
            } else {
                binding.rvAppUser.visibility = VISIBLE
            }
            val filteredList1: ArrayList<ResponseMyContact.Data> = ArrayList()
            for (item in prepareMyContactModel(inviteUserContacts!!, inviteContact)) {
                if (item.fullName.lowercase().startsWith(query.lowercase())) {
                    filteredList1.add(item)
                }
            }
            adapter2.filterList(transformCountriesForRecyclerViewForSearch(filteredList1) as ArrayList<ContactRecyclerViewModel>)
            if (filteredList1.isNullOrEmpty()) {
                binding.layInvite.visibility = GONE
            } else {
                binding.layInvite.visibility = VISIBLE
            }

            if (filteredList.isNullOrEmpty() && filteredList1.isNullOrEmpty()) {
                binding.txtEmptyContact.visibility = VISIBLE
            } else {
                binding.txtEmptyContact.visibility = GONE
            }

        } else {
            showHideView(false)
        }
    }

    fun showHideView(isSerch: Boolean) {
        try {
            if (isSerch) {
                binding.layMain.visibility = GONE
                binding.laySearch.visibility = VISIBLE
            } else {
                binding.layMain.visibility = VISIBLE
                binding.laySearch.visibility = GONE
//                if (allContact.isNullOrEmpty()) {
//                    binding.txtEmptyContact.visibility = VISIBLE
//                } else {
//                    binding.txtEmptyContact.visibility = GONE
//                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun transformCountriesForRecyclerView(myContacts: List<ResponseMyContact.Data>?): List<ContactRecyclerViewModel?>? {
        countriesRecyclerViewModels = ArrayList()
        if (myContacts != null && myContacts.isNotEmpty()) {
            var letter = ""
            for (contact in myContacts) {

                var contactLetter: String = ""
                if (contact.fullName.isNotEmpty()) {
                    contactLetter = contact.fullName.substring(0, 1)
                } else {
                    contactLetter = "A".substring(0, 1)
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

    private fun transformCountriesForRecyclerViewForSearch(myContacts: List<ResponseMyContact.Data>?): List<ContactRecyclerViewModel?>? {
        countriesRecyclerViewModels = ArrayList()
        if (myContacts != null && myContacts.isNotEmpty()) {
            for (contact in myContacts) {
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
        _binding!!.sectionpickerCountries.textViewIndicator = _binding!!.textviewSection
        _binding!!.sectionpickerCountries.setSections(sections)
        _binding!!.sectionpickerCountries.isNestedScrollingEnabled = true
        _binding!!.sectionpickerCountries.setOnTouchingLetterChangedListener { s ->
            val position = adapter.getPositionForSection(s[0].toInt())
            if (position != -1) {
                linearLayoutManager.scrollToPositionWithOffset(position, 0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
    }

}
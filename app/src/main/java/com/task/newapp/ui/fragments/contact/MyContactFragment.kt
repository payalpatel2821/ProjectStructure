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
import com.task.newapp.BuildConfig
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.models.contact.Contact
import com.task.newapp.adapter.contact.ContactAdapter
import com.task.newapp.models.contact.ContactRecyclerViewModel
import com.task.newapp.utils.contactUtils.ContactsHelper
import com.task.newapp.databinding.FragmentContactBinding
import com.task.newapp.models.ResponseIsAppUser
import com.task.newapp.ui.activities.profile.OtherUserProfileActivity
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList
import com.google.gson.reflect.TypeToken
import com.task.newapp.App
import java.lang.reflect.Type


/**
 * A placeholder fragment containing a simple view.
 **/
class MyContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private lateinit var countriesRecyclerViewModels: ArrayList<ContactRecyclerViewModel>
    private lateinit var adapter: ContactAdapter
    private var allContact: ArrayList<Contact> = ArrayList()
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
        adapter.onItemClick = { mobileNumber, emailId ->
            if (mobileNumber.isNotEmpty() || emailId.isNotEmpty()) {
                callAPICheckHowUser(mobileNumber, emailId)
            } else {
                requireActivity().showToast("This contact hase no mobile number or email Id")
            }
        }

        _binding!!.btnGetContact.setOnClickListener {
            activity.runWithPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS) {
                _binding!!.btnGetContact.visibility = GONE
                openProgressDialog(activity)
                getContact()
            }
        }

        _binding!!.sectionpickerCountries.visibility = GONE
        val contactList = App.fastSave.getString(Constants.contact, "")
        val type: Type = object : TypeToken<List<Contact?>?>() {}.type
        val contacts: ArrayList<Contact> = gson.fromJson(contactList, type)
        if (!contacts.isNullOrEmpty()) {
            setAdapter(contacts)
        } else {
            activity.runWithPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS) {
                _binding!!.btnGetContact.visibility = GONE
                openProgressDialog(activity)
                getContact()
            }
        }
    }

    fun getContact() {
        ContactsHelper(requireActivity()).getContacts { contacts ->
            hideProgressDialog()
            val json = gson.toJson(contacts)
            App.fastSave.saveString(Constants.contact, json)
            setAdapter(contacts)
        }
    }

    fun setAdapter(contacts: ArrayList<Contact>) {
        allContact = contacts
        (allContact as ArrayList<Contact>).sortBy { it.firstName }
        adapter.filterList(transformCountriesForRecyclerView(allContact) as ArrayList<ContactRecyclerViewModel>)
        initSectionPicker()
        _binding!!.btnGetContact.isClickable = false
        _binding!!.btnGetContact.visibility = GONE
        _binding!!.rvList.visibility = VISIBLE
        _binding!!.sectionpickerCountries.visibility = VISIBLE
    }

    fun filter(query: String) {
        val filteredList: ArrayList<Contact> = ArrayList()
        for (item in allContact) {
            if (item.getNameToDisplay().lowercase().startsWith(query.lowercase())) {
                filteredList.add(item)
            }
        }
        adapter.filterList(transformCountriesForRecyclerView(filteredList) as ArrayList<ContactRecyclerViewModel>)
    }

    private fun callAPICheckHowUser(mobileNumber: String, emailId: String) {
        if (!requireActivity().isNetworkConnected()) {
            requireActivity().showToast(getString(R.string.no_internet))
            return
        }
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.mobile to mobileNumber,
                Constants.email to emailId,
            )
            mCompositeDisposable.add(
                ApiClient.create()
                    .getIsAppUser(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseIsAppUser>() {
                        override fun onNext(responseIsAppUser: ResponseIsAppUser) {
                            if (responseIsAppUser.success == 1) {
                                if (responseIsAppUser.data.size == 1) {
                                    requireActivity().launchActivity<OtherUserProfileActivity> {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        putExtra(Constants.user_id, responseIsAppUser.data[0].id)
                                    }
                                } else {
                                    DialogUtils().showAppUserDialog(
                                        activity as AppCompatActivity,
                                        responseIsAppUser.data
                                    )
                                }
                            } else {
                                onShareClicked()
                            }
                        }

                        override fun onError(e: Throwable) {
                            hideProgressDialog()
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun transformCountriesForRecyclerView(countries: List<Contact>?): List<ContactRecyclerViewModel?>? {
        countriesRecyclerViewModels = ArrayList()
        if (countries != null && countries.isNotEmpty()) {
            var letter = ""
            for (country in countries) {

                var countryLetter: String = ""
                if (country.firstName.isNotEmpty()) {
                    countryLetter = country.firstName.substring(0, 1)
                } else {
                    countryLetter = "A".substring(0, 1)
                }

                if (TextUtils.isEmpty(letter) || letter != countryLetter) {
                    countriesRecyclerViewModels.add(
                        ContactRecyclerViewModel(
                            null,
                            countryLetter,
                            adapter.TYPE_LETTER
                        )
                    )
                    letter = countryLetter
                }
                countriesRecyclerViewModels.add(
                    ContactRecyclerViewModel(
                        country,
                        null,
                        adapter.TYPE_COUNTRY
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

    private fun onShareClicked() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage = """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                
                
                """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: java.lang.Exception) {
            //e.toString();
        }
    }

    override fun onResume() {
        super.onResume()

    }

}
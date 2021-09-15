package com.task.newapp.ui.fragments.contact

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.appizona.yehiahd.fastsave.FastSave
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.paginate.Paginate
import com.task.newapp.App
import com.task.newapp.BuildConfig
import com.task.newapp.R
import com.task.newapp.adapter.contact.ContactAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentContactBinding
import com.task.newapp.models.ResponseIsAppUser
import com.task.newapp.models.contact.Contact
import com.task.newapp.models.contact.ContactRecyclerViewModel
import com.task.newapp.models.contact.ContactSyncAPIModel
import com.task.newapp.ui.activities.profile.OtherUserProfileActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.contactUtils.ContactsHelper
import com.task.newapp.workmanager.WorkManagerScheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


/**
 * A placeholder fragment containing a simple view.
 **/
class MyContactFragment : Fragment(), Paginate.Callbacks {

    private var contacts: ArrayList<ContactSyncAPIModel> = ArrayList()
    private var _binding: FragmentContactBinding? = null
    private lateinit var countriesRecyclerViewModels: ArrayList<ContactRecyclerViewModel>
    private lateinit var adapter: ContactAdapter
    private var allContact: ArrayList<ResponseIsAppUser.Data> = ArrayList()
    lateinit var linearLayoutManager: LinearLayoutManager
    private val mCompositeDisposable = CompositeDisposable()
    private val binding get() = _binding!!
    val gson = Gson()
    var isloading = false
    var hasLoadedAllItems = false
    private var paginate: Paginate? = null
    var offset: Int = 0


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

        _binding!!.sectionpickerCountries.visibility = GONE

        val contactList = App.fastSave.getString(Constants.contact, "")
        val type: Type = object : TypeToken<List<Contact?>?>() {}.type
        contacts = gson.fromJson(contactList, type)

        _binding!!.btnGetContact.setOnClickListener {
            checkAndGetContact()
        }

        setAdapter(allContact)
        checkAndGetContact()
    }

    private fun checkAndGetContact() {
        activity.runWithPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS) {
            if (!contacts.isNullOrEmpty()) {
                initPaginate()
            } else {
                _binding!!.btnGetContact.visibility = GONE
                openProgressDialog(activity)
                getContact()
            }
        }
    }

    fun getContact() {
        ContactsHelper(requireActivity()).getContacts { (contacts, contactSync) ->
            hideProgressDialog()
            val gson = Gson()
            val json: String = gson.toJson(contactSync)
            FastSave.getInstance().saveString(Constants.contact, json)
            WorkManagerScheduler.refreshPeriodicWorkContact(App.getAppInstance())
            initPaginate()
        }
    }

    fun setAdapter(contacts: ArrayList<ResponseIsAppUser.Data>) {
        allContact = contacts
        (allContact).sortBy { it.fullname }
        refreshAdapter()
    }

    private fun refreshAdapter() {
        adapter.filterList(transformCountriesForRecyclerView(allContact) as ArrayList<ContactRecyclerViewModel>)
        initSectionPicker()
        _binding!!.btnGetContact.isClickable = false
        _binding!!.btnGetContact.visibility = GONE
        _binding!!.rvList.visibility = VISIBLE
        _binding!!.sectionpickerCountries.visibility = VISIBLE
    }

    fun filter(query: String, isPaginate: Boolean) {
        allContact.clear()
        adapter.filterList(transformCountriesForRecyclerView(allContact) as ArrayList<ContactRecyclerViewModel>)
        requireActivity().callAPISearchSyncContact(offset.toString(), query, isPaginate)
    }

    private fun transformCountriesForRecyclerView(contacts: List<ResponseIsAppUser.Data>): List<ContactRecyclerViewModel?>? {
        countriesRecyclerViewModels = ArrayList()
        if (!contacts.isNullOrEmpty() && contacts.isNotEmpty()) {
            var letter = ""
            for (contact in contacts) {

                var countryLetter: String = ""
                countryLetter = if (contact.fullname.isNotEmpty()) {
                    contact.fullname.substring(0, 1)
                } else {
                    "A".substring(0, 1)
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
                        contact,
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
        _binding!!.sectionpickerCountries.visibility = VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

    }

    private fun Activity.callAPISearchSyncContact(offset: String, term: String, isPaginate: Boolean) {
        if (!isNetworkConnected()) {
            showToast(getString(R.string.no_internet))
            return
        }
        val hashMap: HashMap<String, Any> = if (isPaginate) {
            hashMapOf(
                Constants.limit to requireActivity().resources.getString(R.string.limit_20),
                Constants.offset to offset,
                Constants.term to term,
            )
        } else {
            hashMapOf(
                Constants.term to term,
            )
        }
//        openProgressDialog(this)

        try {
            mCompositeDisposable.add(
                ApiClient.create()
                    .searchContactSync(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseIsAppUser>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(response: ResponseIsAppUser) {
                            if (isPaginate) {
                                if (response.success == 1) {
                                    allContact.addAll(response.data)
                                    adapter.filterList(transformCountriesForRecyclerView(allContact) as ArrayList<ContactRecyclerViewModel>)
                                    isloading = false
                                    hasLoadedAllItems = false
                                    initSectionPicker()
                                    if (response.data.size < requireActivity().resources.getString(R.string.limit_20).toInt()) {
                                        hasLoadedAllItems = true
                                    }
                                } else {
                                    hasLoadedAllItems = true
                                    if (allContact.isEmpty()) {
                                        _binding!!.btnGetContact.isClickable = true
                                        _binding!!.btnGetContact.visibility = VISIBLE
                                        _binding!!.rvList.visibility = GONE
                                        _binding!!.sectionpickerCountries.visibility = GONE
                                    } else {
                                        _binding!!.btnGetContact.isClickable = false
                                        _binding!!.btnGetContact.visibility = GONE
                                        _binding!!.rvList.visibility = VISIBLE
                                        _binding!!.sectionpickerCountries.visibility = VISIBLE
                                    }
                                }
                            } else {
                                hasLoadedAllItems = true
                                if (response.success == 1) {
                                    allContact.addAll(response.data)
                                    adapter.filterList(transformCountriesForRecyclerView(allContact) as ArrayList<ContactRecyclerViewModel>)
                                    initSectionPicker()
                                } else {
                                    allContact.clear()
                                    adapter.filterList(transformCountriesForRecyclerView(allContact) as ArrayList<ContactRecyclerViewModel>)
                                    _binding!!.sectionpickerCountries.visibility = GONE
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
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

    private fun initPaginate() {
        paginate = Paginate.with(_binding!!.rvList, this)
            .setLoadingTriggerThreshold(17)
            .addLoadingListItem(false)
            .setLoadingListItemCreator(null)
            .build()
    }

    override fun onLoadMore() {
        isloading = true
        val scrollPosition: Int = requireActivity().resources.getString(R.string.limit_20).toInt() * offset
        showLog("position", scrollPosition.toString())
        requireActivity().callAPISearchSyncContact(scrollPosition.toString(), "", true)
    }

    override fun isLoading(): Boolean {
        return isloading
    }

    override fun hasLoadedAllItems(): Boolean {
        return hasLoadedAllItems
    }


}
package com.task.newapp.ui.fragments.contact

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import biz.laenger.android.vpbs.BottomSheetUtils
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.task.newapp.R
import com.task.newapp.adapter.contact.SectionsPagerAdapter
import com.task.newapp.databinding.ActivityContactBinding
import com.task.newapp.utils.Constants
import com.task.newapp.utils.showLog
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import java.util.concurrent.TimeUnit


class ContactBottomSheet : ViewPagerBottomSheetDialogFragment()/*, SearchView.OnQueryTextListener*/ {

    private lateinit var tabs: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var binding: ActivityContactBinding
    private lateinit var bottomSheet: ViewGroup
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    lateinit var serviceBroadRequestReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDetach() {
        super.onDetach()
        requireContext().unregisterReceiver(serviceBroadRequestReceiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_contact, container, false)
        sectionsPagerAdapter = SectionsPagerAdapter(requireActivity(), childFragmentManager)
        viewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        tabs = binding.tabs
        tabs.setupWithViewPager(viewPager)
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.searchLayout.searchView.setQuery("", false)
                if (tab!!.position == 0) {
                    val myContactFragment: MyContactFragment = viewPager.adapter!!.instantiateItem(viewPager, 0) as MyContactFragment
                    myContactFragment.showHideView(false)
                }else{
                    val exploreFragment: ExploreFragment = viewPager.adapter!!.instantiateItem(viewPager, 1) as ExploreFragment
                    exploreFragment.filter("")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        BottomSheetUtils.setupViewPager(viewPager)
        initSearchView()
        initReceiver()
        registerReceiver()
        return binding.root
    }

    private fun initReceiver() {
        serviceBroadRequestReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Constants.INTENT_CONTACT_COMPLETE) {
                    //Show post process
//                if (::viewPager.isInitialized) {
                    showLog("BroadcastReceiver", "INTENT_CONTACT_COMPLETE")
                    val myContactFragment: MyContactFragment = viewPager.adapter!!.instantiateItem(viewPager, 0) as MyContactFragment
                    myContactFragment.getFilterContact()
                    myContactFragment.allContact.clear()
                    myContactFragment.allContact = myContactFragment.prepareMyContactModel(myContactFragment.appUserContacts, myContactFragment.allContact)
                    if (!myContactFragment.allContact.isNullOrEmpty()) {
                        myContactFragment.setAdapter(myContactFragment.allContact)
                    }
//                }
                }
            }
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(Constants.INTENT_CONTACT_COMPLETE)
        requireContext().registerReceiver(serviceBroadRequestReceiver, filter)
    }

    private fun closeClick() {
        binding.searchLayout.searchView.isActivated = true
        binding.searchLayout.searchView.setQuery("", false)
        binding.searchLayout.searchView.isIconified = true
        binding.searchLayout.searchView.onActionViewExpanded()
        binding.searchLayout.searchView.clearFocus()
        binding.searchLayout.searchView.findViewById<View>(R.id.search_button).performClick()
        if (tabs.selectedTabPosition == 1) {
            val exploreFragment: ExploreFragment = viewPager.adapter!!.instantiateItem(viewPager, 1) as ExploreFragment
            exploreFragment.filter("")
        } else {
            val myContactFragment: MyContactFragment = viewPager.adapter!!.instantiateItem(viewPager, 0) as MyContactFragment
            myContactFragment.filter("")
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
                requireActivity().runOnUiThread {
                    val selectedTabPosition = tabs.selectedTabPosition
                    if (selectedTabPosition == 0) {
                        val myContactFragment: MyContactFragment = viewPager.adapter!!.instantiateItem(viewPager, viewPager.currentItem) as MyContactFragment
                        myContactFragment.filter(query!!)
                    } else {
                        val exploreFragment: ExploreFragment = viewPager.adapter!!.instantiateItem(viewPager, viewPager.currentItem) as ExploreFragment
                        exploreFragment.filter(query!!)
                    }
                }


            }, { throwable -> showLog("subscribe_throwable", throwable.toString()) })

    }

}
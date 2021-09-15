package com.task.newapp.ui.fragments.contact

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
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.task.newapp.R
import com.task.newapp.adapter.contact.SectionsPagerAdapter
import com.task.newapp.databinding.ActivityContactBinding
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
//        bottomSheet =
//            dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup // notice the R root package
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
//        // SETUP YOUR BEHAVIOR HERE
//        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
//        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
//            override fun onStateChanged(view: View, i: Int) {
//                if (BottomSheetBehavior.STATE_EXPANDED == i) {
////                    showView(appBarLayout, getActionBarSize()) // show app bar when expanded completely
//                }
//                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
////                    hideAppBar(appBarLayout) // hide app bar when collapsed
//                }
//                if (BottomSheetBehavior.STATE_HIDDEN == i) {
//                    dismiss() // destroy the instance
//                }
//            }
//
//            override fun onSlide(view: View, v: Float) {}
//        })
//
////        hideAppBar(appBarLayout)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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
        BottomSheetUtils.setupViewPager(viewPager)
        initSearchView()
        return binding.root
    }

    private fun initSearchView() {
        binding.searchLayout.searchView.findViewById<View>(R.id.search_close_btn)
            .setOnClickListener(View.OnClickListener {
                binding.searchLayout.searchView.isActivated = true
                binding.searchLayout.searchView.setQuery("", false)
                binding.searchLayout.searchView.isIconified = true
                binding.searchLayout.searchView.onActionViewExpanded()
                binding.searchLayout.searchView.clearFocus()
                binding.searchLayout.searchView.findViewById<View>(R.id.search_button).performClick()
                if (tabs.selectedTabPosition == 1) {
                    val exploreFragment: ExploreFragment = viewPager.adapter!!.instantiateItem(viewPager, viewPager.currentItem) as ExploreFragment
                    exploreFragment.filter("")
                }
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
                        if (query == "") {
                            val myContactFragment: MyContactFragment = viewPager.adapter!!.instantiateItem(viewPager, viewPager.currentItem) as MyContactFragment
                            myContactFragment.filter(query, true)
                        } else {
                            val myContactFragment: MyContactFragment = viewPager.adapter!!.instantiateItem(viewPager, viewPager.currentItem) as MyContactFragment
                            myContactFragment.filter(query!!, false)
                        }

                    } else {
                        val exploreFragment: ExploreFragment = viewPager.adapter!!.instantiateItem(viewPager, viewPager.currentItem) as ExploreFragment
                        exploreFragment.filter(query!!)
                    }
                }


            }, { throwable -> showLog("subscribe_throwable", throwable.toString()) })
    }


}
package com.task.newapp.ui.fragments.contact

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.task.newapp.R
import com.task.newapp.adapter.contact.SectionsPagerAdapter
import com.task.newapp.databinding.ActivityContactBinding


class ContactBottomSheet : BottomSheetDialogFragment() {

    private lateinit var tabs: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var binding: ActivityContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        initSearchView()
        return binding.root
    }

    private fun initSearchView() {
        binding.searchLayout.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val selectedTabPosition = tabs.selectedTabPosition
                if (selectedTabPosition == 0) {
                    val myContactFragment: MyContactFragment = viewPager.adapter!!.instantiateItem(viewPager, viewPager.currentItem) as MyContactFragment
                    myContactFragment.filter(newText!!)
                } else {
                    val exploreFragment: ExploreFragment = viewPager.adapter!!.instantiateItem(viewPager, viewPager.currentItem) as ExploreFragment
                    exploreFragment.filter(newText!!)
                }
                return true
            }
        })

        binding.searchLayout.searchView.findViewById<View>(R.id.search_close_btn)
            .setOnClickListener(View.OnClickListener {
                Log.d("called", "this is called.")
                binding.searchLayout.searchView.isActivated = true
                binding.searchLayout.searchView.setQuery("", false)
                binding.searchLayout.searchView.isIconified = true
                binding.searchLayout.searchView.onActionViewExpanded()
                binding.searchLayout.searchView.clearFocus()
                binding.searchLayout.searchView.findViewById<View>(R.id.search_button).performClick()
            })
    }


}
package com.task.newapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.task.newapp.R
import com.task.newapp.adapter.RegistrationPagerAdapter
import com.task.newapp.databinding.ActivityRegistrationBinding
import com.task.newapp.ui.fragments.registration.RegistrationStep1Fragment
import com.task.newapp.ui.fragments.registration.RegistrationStep2Fragment
import com.task.newapp.ui.fragments.registration.RegistrationStep3Fragment
import com.task.newapp.ui.fragments.registration.RegistrationStep4Fragment

class RegistrationActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        setupViewPager(binding.vpRegistration)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        var adapter: RegistrationPagerAdapter = RegistrationPagerAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )

        // LoginFragment is the name of Fragment and the Login
        // is a title of tab
        adapter.addFragment(RegistrationStep1Fragment(), "Basic Info")
        adapter.addFragment(RegistrationStep2Fragment(), "Validate Yourself")
        adapter.addFragment(RegistrationStep3Fragment(), "Setup Password")
        adapter.addFragment(RegistrationStep4Fragment(), "Setup UserName")

        // setting adapter to view pager.
        viewPager.setAdapter(adapter)
        
    }
}
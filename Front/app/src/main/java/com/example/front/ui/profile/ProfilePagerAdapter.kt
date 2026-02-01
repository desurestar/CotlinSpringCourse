package com.example.front.ui.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfilePagerAdapter(
    fragment: Fragment,
    private val employeeId: Long
) : FragmentStateAdapter(fragment) {
    
    override fun getItemCount(): Int = 5
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileTabFragment.newInstance(ProfileTabFragment.TAB_INFO, employeeId)
            1 -> ProfileTabFragment.newInstance(ProfileTabFragment.TAB_MY_ARTICLES, employeeId)
            2 -> ProfileTabFragment.newInstance(ProfileTabFragment.TAB_PARTICIPATION_ARTICLES, employeeId)
            3 -> ProfileTabFragment.newInstance(ProfileTabFragment.TAB_MY_TEAMS, employeeId)
            4 -> ProfileTabFragment.newInstance(ProfileTabFragment.TAB_PARTICIPATION_TEAMS, employeeId)
            else -> ProfileTabFragment.newInstance(ProfileTabFragment.TAB_INFO, employeeId)
        }
    }
}

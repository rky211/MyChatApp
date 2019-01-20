package com.example.raj.mychatapp

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.raj.mychatapp.fragments.ChatsFragment
import com.example.raj.mychatapp.fragments.FriendsFragment
import com.example.raj.mychatapp.fragments.RequestsFragement

class SectionsPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {

        when(position){
             0-> return RequestsFragement()
             1-> return ChatsFragment()
             2-> return FriendsFragment()
             else-> return null
        }

        return null
    }

    override fun getCount(): Int {

        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0-> return "REQUESTS"
            1-> return "CHATS"
            2-> return "FRIENDS"
            else-> return null
        }

        return null


    }
}
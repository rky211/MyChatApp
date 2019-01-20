package com.example.raj.mychatapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
 import com.google.firebase.database.ServerValue

class MainActivity : AppCompatActivity() {

    private var mAuth:FirebaseAuth?=null

    private var mToolbar:android.support.v7.widget.Toolbar?=null
    private var mViewPager:ViewPager?=null
    private var mSectionsPagerAdapter:SectionsPagerAdapter?=null
    private var mTablsLayout:TabLayout?=null
    var mUserRef:DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()

        if (mAuth?.currentUser != null) {
            mUserRef = FirebaseDatabase.getInstance().reference.child("Users")
                    .child(mAuth?.currentUser?.uid.toString())
            mUserRef?.keepSynced(true)
        }

        mToolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.main_page_app_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "MyChatApp"

        mViewPager = findViewById<ViewPager>(R.id.main_tabPager)
        mTablsLayout =findViewById(R.id.main_tabs)

        //set ViewPager with Fragments through  Adapter
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mViewPager?.adapter = mSectionsPagerAdapter

        //set tabs with ViewPager
        mTablsLayout?.setupWithViewPager(mViewPager)
    }

    override fun onStart() {
        super.onStart()

        //Check if user is signed in (i.e non-null) and update UI according to it
        if(mAuth?.currentUser == null){
           updateUI()
        }
        else{

            mUserRef?.child("online")?.setValue(true)
        }

    }

    override fun onStop() {
        super.onStop()
        if(mAuth?.currentUser != null) {
            mUserRef?.child("online")?.setValue(ServerValue.TIMESTAMP)
        }
    }

    private fun updateUI() {
        var startIntent:Intent = Intent(this@MainActivity,StartActivity::class.java)
        startActivity(startIntent)
        finish() //so that we can't go back to main activity by back button if it not log in
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.main_logout_btn->{

                                mUserRef?.child("online")?.setValue(ServerValue.TIMESTAMP)
                                    FirebaseAuth.getInstance().signOut()
                                    updateUI()
                                    return true
                                    }
            R.id.main_settings_btn->{
                startActivity(Intent(this,SettingsActivity::class.java))
                return true
            }

            R.id.main_users_btn->{
                startActivity(Intent(this,AllUsersActivity::class.java))
                return true
            }

            else ->return super.onOptionsItemSelected(item)


        }
    }
}

package com.example.raj.mychatapp

import android.app.Application
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso

class MyChatApp:Application() {


    var mAuth:FirebaseAuth?=null
    var mUserDbRef:DatabaseReference?=null



    override fun onCreate() {
        super.onCreate()

        //for offline fetching data
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        //for offline picasso

        var builder: Picasso.Builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Long.MAX_VALUE))

        var built: Picasso = builder.build()
        built.setIndicatorsEnabled(true)
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)


        //for online presence
        mAuth = FirebaseAuth.getInstance()

        if (mAuth?.currentUser != null) {


            mUserDbRef = FirebaseDatabase.getInstance().reference.child("Users")
                    .child(mAuth?.currentUser?.uid.toString())

            mUserDbRef?.addValueEventListener(object : ValueEventListener {


                override fun onDataChange(p0: DataSnapshot) {

                    if (p0 != null) {
                        mUserDbRef?.child("online")?.onDisconnect()?.setValue(ServerValue.TIMESTAMP)

                    }

                }

                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(this@MyChatApp, "Error", Toast.LENGTH_SHORT).show()
                }

            })


        }
    }
}
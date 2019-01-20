package com.example.raj.mychatapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class StatusActivity : AppCompatActivity() {


    var mDbRef: DatabaseReference?=null
    var currentUser: FirebaseUser?=null

    var mEditStatus:EditText?=null
    var mSubmitStatus:Button?=null
    var mToolbar:Toolbar?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        mToolbar = findViewById(R.id.status_appBar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Status"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        mEditStatus = findViewById<EditText>(R.id.edtStatus)
        mSubmitStatus = findViewById(R.id.submit_status)

        var prevStatus:String = intent.getStringExtra(SettingsActivity.STATUS_VALUE)

        mEditStatus?.setText(prevStatus)
    }

    override fun onStart() {
        super.onStart()

        currentUser = FirebaseAuth.getInstance().currentUser

        mDbRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUser?.uid.toString())

        mSubmitStatus?.setOnClickListener {
            var status:String = mEditStatus?.text.toString()
            mDbRef?.child("status")?.setValue(status)?.addOnCompleteListener{
                if (it.isSuccessful){
                    Toast.makeText(this,"Status Successfully Change",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,SettingsActivity::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this,"Some Error",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

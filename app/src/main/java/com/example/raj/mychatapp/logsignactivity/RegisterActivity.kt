package com.example.raj.mychatapp.logsignactivity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.raj.mychatapp.MainActivity
import com.example.raj.mychatapp.R
import com.example.raj.mychatapp.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class RegisterActivity : AppCompatActivity() {

    private var mName:EditText?=null
    private var mPswrd:EditText?=null
    private var mEmail:EditText?=null
    private var mCrtBtn:Button?=null
    private var mToolbar:android.support.v7.widget.Toolbar?=null

    private var mDbRef:DatabaseReference?=null
    private var mAuth:FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //app bar
        mToolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.reg_page_app_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Create Account"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()

        mName = findViewById<EditText>(R.id.reg_name)
        mEmail = findViewById<EditText>(R.id.reg_email)
        mPswrd = findViewById<EditText>(R.id.reg_pswrd)
        mCrtBtn = findViewById<Button>(R.id.create_account)
        mCrtBtn?.setOnClickListener{
            var name:String = mName?.text.toString()
            var email:String = mEmail?.text.toString()
            var pswrd:String = mPswrd?.text.toString()

            if (!validateForm(name,email,pswrd)) {
                return@setOnClickListener
            }
            else {
                registerUser(name, email, pswrd)
            }
        }
    }

    private fun registerUser(name:String,email:String,pswrd:String){
        mAuth?.createUserWithEmailAndPassword(email,pswrd)?.addOnCompleteListener{
            if (it.isSuccessful)
            {
                Toast.makeText(this,"Successfully Register",Toast.LENGTH_SHORT).show()

                writeUser(name,email,pswrd,"this is default Status","img","thumb img")



            }
            else if(it.exception is FirebaseAuthUserCollisionException){
                Toast.makeText(this,"Email Already Registered ",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Get Some Error",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun writeUser(name: String, email: String, pswrd: String, s: String, s1: String,s2:String) {


        var mToken = FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {

            mDbRef = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth?.currentUser?.uid.toString())

            var mUser = Users(name, email, pswrd, s, s1, s2,it.token)

            mDbRef?.setValue(mUser)?.addOnCompleteListener {
                if (it.isSuccessful){

                    //Clear Previous Activity with start new activity
                    var mIntent = Intent(this, MainActivity::class.java)
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
                    startActivity(mIntent)
                    finish()
                }
                else{
                    Toast.makeText(this,"Error in Saving Data",Toast.LENGTH_SHORT).show()
                }
            }

        }



    }


    fun validateForm(name: String, email: String,paswrd: String): Boolean {

        if (TextUtils.isEmpty(name)) {
            mName?.error = "Email Can't Blank"
            mName?.requestFocus()
            return false

        }
        if (TextUtils.isEmpty(email)) {
            mEmail?.error = "Email Can't be empty"
            mEmail?.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail?.error = "Enter valid format of email"
            mEmail?.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(paswrd)) {
            mPswrd?.error = "Phone No Can't Blank"
            mPswrd?.requestFocus()
            return false
        }

        if (paswrd.length < 6) {
            mPswrd?.error = "Password length must be greater than 6"
            mPswrd?.requestFocus()
            return false
        }

        return true
    }
}

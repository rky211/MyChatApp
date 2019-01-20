package com.example.raj.mychatapp.logsignactivity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Patterns
import android.widget.*
import com.example.raj.mychatapp.MainActivity
import com.example.raj.mychatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class LoginActivity : AppCompatActivity() {

    private var mToolbar: Toolbar?=null
    private var mLogin:Button?=null
    private var logEmail:EditText?=null
    private var logPswrd:EditText?=null
    private var mForget:TextView?=null

    private var mAuth:FirebaseAuth?=null
    private var mDbRef:DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

            //SetUp Toolbar
            mToolbar = findViewById<Toolbar>(R.id.log_page_app_toolbar)
            setSupportActionBar(mToolbar)
            supportActionBar?.title = "Login"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            mAuth = FirebaseAuth.getInstance()
            mDbRef = FirebaseDatabase.getInstance().reference

            logEmail = findViewById<EditText>(R.id.log_email)
            logPswrd = findViewById<EditText>(R.id.log_pswrd)
            mLogin = findViewById<Button>(R.id.login)
            mForget = findViewById<TextView>(R.id.forget)

            //login btn Event---
            mLogin?.setOnClickListener{
                var myEmail:String = logEmail?.text.toString()
                var myPswrd:String = logPswrd?.text.toString()
                if (!validateForm(myEmail,myPswrd)) {
                    return@setOnClickListener
                }
                else
                logIn(myEmail,myPswrd)
            }

            //Forget pswrd Btn Event---
            mForget?.setOnClickListener{
                    startActivity(Intent(this, ForgetPasswordActivity::class.java))
                }
    }//endof Oncreate

    //loginCheck----
    private fun logIn(myEmail:String,myPswrd:String) {
        mAuth?.signInWithEmailAndPassword(myEmail,myPswrd)?.addOnCompleteListener {
                if (it.isSuccessful){

                    var mToken = FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {



                        mDbRef?.child("Users")?.child(mAuth?.currentUser?.uid.toString())?.child("deviceToken")
                                ?.setValue(it.token)?.addOnCompleteListener {

                                    if (it.isSuccessful){
                                        Toast.makeText(this,"LogIn Successfully",Toast.LENGTH_SHORT).show()
                                        //Clear Previous Activity with start new activity
                                        var mIntent = Intent(this, MainActivity::class.java)
                                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
                                        startActivity(mIntent)
                                        finish()
                                    }
                                }

                    }


                }
                else if(it.exception is FirebaseAuthInvalidUserException)
                {
                    Toast.makeText(this,"Email Not Registered", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"Wrong Password",Toast.LENGTH_SHORT).show()
                }
        }

    }

    //Validate form---
    fun validateForm(email: String, pswrd: String): Boolean {


        if (TextUtils.isEmpty(email)) {
            logEmail?.error = "Email Can't be empty"
            logEmail?.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            logEmail?.error = "Enter valid format of email"
            logEmail?.requestFocus()
            return false
        }


        if (TextUtils.isEmpty(pswrd)) {
            logPswrd?.error = "Phone No Can't Blank"
            logPswrd?.requestFocus()
            return false
        }

        if (pswrd.length < 6) {
            logPswrd?.error = "Password length must be greater than 6"
            logPswrd?.requestFocus()
            return false
        }

        return true
    }//End of Validate form
}

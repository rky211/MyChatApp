package com.example.raj.mychatapp.logsignactivity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.raj.mychatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ForgetPasswordActivity : AppCompatActivity() {

    private var mFrgtEmail:EditText?=null
    private var mSendBtn:Button?=null
    private var mToolbar: Toolbar?=null

    private var mAuth: FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        //SetUp Toolbar
        mToolbar = findViewById<Toolbar>(R.id.frgt_page_app_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Forget Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()

        mFrgtEmail = findViewById<EditText>(R.id.frgt_email)

        mSendBtn = findViewById<Button>(R.id.frgt_send)

        mSendBtn?.setOnClickListener{
            var email:String = mFrgtEmail?.text.toString()
            if (!validateForm(email)) {
                return@setOnClickListener
            }
            else
                sendVerification(email)
        }
    }

    private fun sendVerification(email:String) {
        mAuth?.sendPasswordResetEmail(email)?.addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(this,"Please Check Your Email",Toast.LENGTH_SHORT).show()
            }
            else if (it.exception is FirebaseAuthInvalidUserException){
                Toast.makeText(this,"Email Not Registerd",Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this,"Some Error",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm(email: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            mFrgtEmail?.error = "Email Can't be empty"
            mFrgtEmail?.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mFrgtEmail?.error = "Enter valid format of email"
            mFrgtEmail?.requestFocus()
            return false
        }
        return true
    }
}

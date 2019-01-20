package com.example.raj.mychatapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.raj.mychatapp.logsignactivity.LoginActivity
import com.example.raj.mychatapp.logsignactivity.RegisterActivity

class StartActivity : AppCompatActivity() {

    private var mRegBtn:Button?=null
    private var mLogBtn:Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        mRegBtn = findViewById<Button>(R.id.regbtn)
        mLogBtn = findViewById<Button>(R.id.logbtn)
        mRegBtn?.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        mLogBtn?.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }
}

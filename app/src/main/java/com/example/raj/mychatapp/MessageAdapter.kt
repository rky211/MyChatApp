package com.example.raj.mychatapp

import android.app.Notification
import android.graphics.Color
import android.support.v4.view.GravityCompat
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.msg_single_layout.view.*

class MessageAdapter(): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {


    var mAuth:FirebaseAuth?=null
    var mMsgList = mutableListOf<Messages>()
    constructor(mMsgList:MutableList<Messages>) : this() {
        this.mMsgList  = mMsgList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.msg_single_layout,parent,false)

        return MessageViewHolder(view)

    }

    override fun getItemCount(): Int {
        return  mMsgList.size
    }

    override fun onBindViewHolder(p0: MessageViewHolder, p1: Int) {

        mAuth = FirebaseAuth.getInstance()
        val m = mMsgList.get(p1)


        if (mAuth?.currentUser?.uid?.equals(m.from) as Boolean){

            p0.msgText.setBackgroundColor(Color.CYAN)
            p0.msgText.setTextColor(Color.BLACK)
            p0.msglyt.gravity = Gravity.END
            PersonalMsgActivity()
            p0.msgText.text = m.message
        }
        else{

            p0.msgText.text = m.message

        }

    }

    class MessageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        var msgText = itemView.txtMsg
        var msglyt= itemView.mgLayout

    }



}
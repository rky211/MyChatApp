package com.example.raj.mychatapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.lang.Exception
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class UsersProfileActivity : AppCompatActivity() {

    var mProfileImg:ImageView?=null
    var mProfileName:TextView?=null
    var mProfileStatus:TextView?=null
    var mProfileFriends:TextView?=null
    var mSendReqBtn:Button?=null
    var mRejectReqBtn:Button?=null

    var mDbRef:DatabaseReference?=null
    var mFrndReqDbRef:DatabaseReference?=null
    var mFrndListDbRef:DatabaseReference?=null
    var mNotifDbRef:DatabaseReference?=null
    var reqState:String?=null
    lateinit var currentUserId:String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_profile)

        var userId = intent.getStringExtra("User_Id")

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        mDbRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        mDbRef?.keepSynced(true)

        mFrndReqDbRef =FirebaseDatabase.getInstance().reference.child("Friend_req")
        mFrndListDbRef =FirebaseDatabase.getInstance().reference.child("Friend_list")
        mNotifDbRef = FirebaseDatabase.getInstance().reference.child("Notifications")

        reqState = "Not a Friend"

        mProfileImg =findViewById(R.id.profile_img)
        mProfileName = findViewById(R.id.profile_name)
        mProfileStatus =findViewById(R.id.profile_status)
        mProfileFriends = findViewById(R.id.profile_friends)
        mSendReqBtn = findViewById(R.id.send_frnd_reqst)
        mRejectReqBtn = findViewById(R.id.reject_frnd_reqst)




        mDbRef?.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                var name:String = p0.child("name").value.toString()
                var status:String = p0.child("status").value.toString()
                var img:String = p0.child("img").value.toString()

                mProfileName?.text = name
                mProfileStatus?.text = status

                if (!img.equals("img")){
                    Picasso.get().load(img).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.my).into(mProfileImg,object: Callback {
                                override fun onSuccess() {

                                }

                                override fun onError(e: Exception?) {
                                    Picasso.get().load(img).placeholder(R.drawable.my).into(mProfileImg)                                    }

                            })
                }


                //check req sent or recv
                mFrndReqDbRef?.child(currentUserId)?.addListenerForSingleValueEvent(object :ValueEventListener{

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.hasChild(userId)){

                            var state:String = p0.child(userId).child("request_type").value.toString()

                            if (state == "Recv"){
                                reqState = "req_recv"
                                mSendReqBtn?.text = "Accept Request"
                                mRejectReqBtn?.visibility = VISIBLE
                                mRejectReqBtn?.isEnabled = true
                            }
                            if (state == "Sent"){
                                reqState = "req_sent"
                                mSendReqBtn?.text = "Cancel Friend Request"
                                mRejectReqBtn?.visibility = INVISIBLE
                                mRejectReqBtn?.isEnabled = false
                            }
                        }

                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })

            //check friend or not
                mFrndListDbRef?.child(currentUserId)?.addListenerForSingleValueEvent(object :ValueEventListener{

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.hasChild(userId)){

                            reqState = "Friend"
                            mSendReqBtn?.text = "Unfriend"
                            mRejectReqBtn?.visibility = INVISIBLE
                            mRejectReqBtn?.isEnabled = false
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })
            }

            override fun onCancelled(p0: DatabaseError) {

                Toast.makeText(this@UsersProfileActivity,"Some Error",Toast.LENGTH_SHORT).show()
            }


        })

        //--------------send request-------------------
        mSendReqBtn?.setOnClickListener {
            if (reqState.equals("Not a Friend")) {
                mSendReqBtn?.isEnabled = false //i.e disable

                //---------Sent Friend Request --------------
                mFrndReqDbRef?.child(currentUserId)?.child(userId)?.child("request_type")
                        ?.setValue("Sent")?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                mFrndReqDbRef?.child(userId)?.child(currentUserId)?.child("request_type")
                                        ?.setValue("Recv")?.addOnCompleteListener {
                                            if (it.isSuccessful) {

                                                var notifData = HashMap<String,String>()
                                                notifData.put("from",currentUserId)
                                                notifData.put("type","Request")
                                                mNotifDbRef?.child(userId)?.push()?.setValue(notifData)?.addOnCompleteListener {

                                                    if (it.isSuccessful){
                                                        mSendReqBtn?.isEnabled = true //Enable
                                                        reqState = "req_sent"
                                                        mSendReqBtn?.text = "Cancel Friend Request"
                                                        mRejectReqBtn?.visibility = INVISIBLE
                                                        mRejectReqBtn?.isEnabled = false
                                                        Toast.makeText(this@UsersProfileActivity, "Friend Req Successfully Sent", Toast.LENGTH_SHORT).show()
                                                    }
                                                }

                                            }
                                        }
                            } else {
                                Toast.makeText(this@UsersProfileActivity, "Failed Sending Req", Toast.LENGTH_SHORT).show()
                            }
                        }
            }

            //-----------------Cancel Friend Request ----------------
            else if (reqState.equals("req_sent")) {
                mFrndReqDbRef?.child(currentUserId)?.child(userId)?.removeValue()?.addOnCompleteListener {

                    if (it.isSuccessful) {
                        mFrndReqDbRef?.child(userId)?.child(currentUserId)?.removeValue()?.addOnCompleteListener {

                            mSendReqBtn?.isEnabled = true //Enable
                            reqState = "Not a Friend"
                            mSendReqBtn?.text = "Send Friend Request"
                            mRejectReqBtn?.visibility = INVISIBLE
                            mRejectReqBtn?.isEnabled = false
                            Toast.makeText(this@UsersProfileActivity, "Friend Req Cancel", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }


            //--------------Friend List Accept-----------------
            else if (reqState.equals("req_recv")){

                var currentDate:String = DateFormat.getDateTimeInstance().format(Date())
                mFrndListDbRef?.child(currentUserId)?.child(userId)?.child("Date")?.setValue(currentDate)?.addOnCompleteListener {

                    if (it.isSuccessful){
                        mFrndListDbRef?.child(userId)?.child(currentUserId)?.child("Date")?.setValue(currentDate)?.addOnCompleteListener {

                            if (it.isSuccessful){

                                //then delete send and receive state data of user after accept
                                mFrndReqDbRef?.child(currentUserId)?.child(userId)?.removeValue()?.addOnCompleteListener {

                                    if (it.isSuccessful) {
                                        mFrndReqDbRef?.child(userId)?.child(currentUserId)?.removeValue()?.addOnCompleteListener {

                                            mSendReqBtn?.isEnabled = true //Enable
                                            reqState = "Friend"
                                            mSendReqBtn?.text = "Unfriend"
                                            mRejectReqBtn?.visibility = INVISIBLE
                                            mRejectReqBtn?.isEnabled = false
                                            Toast.makeText(this@UsersProfileActivity, "Friend Req Cancel", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //---------Unfriend--------------
            else if (reqState.equals("Friend")){

                mFrndListDbRef?.child(currentUserId)?.child(userId)?.removeValue()?.addOnCompleteListener {

                    if (it.isSuccessful) {
                        mFrndListDbRef?.child(userId)?.child(currentUserId)?.removeValue()?.addOnCompleteListener {

                            mSendReqBtn?.isEnabled = true //Enable
                            reqState = "Not a Friend"
                            mSendReqBtn?.text = "Send Friend Request"
                            mRejectReqBtn?.visibility = INVISIBLE
                            mRejectReqBtn?.isEnabled = false
                            Toast.makeText(this@UsersProfileActivity, "Friend Req Cancel", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                }
            }


        //-----------------Reject Friend Request ----------------
        mRejectReqBtn?.setOnClickListener {

            mFrndReqDbRef?.child(currentUserId)?.child(userId)?.removeValue()?.addOnCompleteListener {

                if (it.isSuccessful) {
                    mFrndReqDbRef?.child(userId)?.child(currentUserId)?.removeValue()?.addOnCompleteListener {

                        mSendReqBtn?.isEnabled = true //Enable
                        reqState = "Not a Friend"
                        mSendReqBtn?.text = "Send Friend Request"
                        mRejectReqBtn?.visibility = INVISIBLE
                        mRejectReqBtn?.isEnabled = false
                        Toast.makeText(this@UsersProfileActivity, "Friend Req Cancel", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        }


    }


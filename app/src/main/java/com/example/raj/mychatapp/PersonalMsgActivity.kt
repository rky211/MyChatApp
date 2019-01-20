package com.example.raj.mychatapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class PersonalMsgActivity : AppCompatActivity() {

    private var mToolbar: Toolbar?=null
    var mDbRef: DatabaseReference?=null
    var mFrndName:TextView?=null
    var mFrndOnlineStats:TextView?=null
    var mFrndImg:ImageView?=null
    var mRootRef:DatabaseReference?=null
    var mAuth:FirebaseAuth?=null
    var edtMsg:EditText?=null
    var sendMsgBtn:ImageButton?=null
    var frnduserId:String?=null
    var mMsgList:RecyclerView?=null
    var messageList = mutableListOf<Messages>()
    var mLinearLayout:LinearLayoutManager?=null
    var mMsgAdapter:MessageAdapter?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_msg)



        mDbRef = FirebaseDatabase.getInstance().reference.child("Users")
        mDbRef?.keepSynced(true)

        mRootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        mFrndName = findViewById(R.id.frnd_name)
        mFrndOnlineStats = findViewById(R.id.frnd_online)
        mFrndImg = findViewById(R.id.frnd_img)

        edtMsg = findViewById(R.id.edtMsg)
        sendMsgBtn = findViewById(R.id.sendImgBtn)

        mMsgAdapter = MessageAdapter(messageList)

        mMsgList = findViewById(R.id.msgListRecycl)
        mLinearLayout = LinearLayoutManager(this)
        mMsgList?.setHasFixedSize(true)
        mMsgList?.layoutManager = mLinearLayout
        mMsgList?.adapter = mMsgAdapter

        loadMessages()


        frnduserId = intent.getStringExtra("User_Id")
        val mFrndUserId = frnduserId //becz we dont want to change this

        mToolbar =findViewById(R.id.personal_msg_app_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_icon)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mDbRef?.child(mFrndUserId.toString())?.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                    var name:String = p0.child("name").value.toString()
                    var tmbImg:String = p0.child("thumbImg").value.toString()
                    var online:String = p0.child("online").value.toString()

                    if(online.equals("true")){

                        mFrndOnlineStats?.text = "online"
                    }
                    else{
                        var lastTime:Long = online.toLong()
                        var lastSeen:String? = GetTimeAgo().getTimeAgo(lastTime,applicationContext)
                        mFrndOnlineStats?.text = lastSeen
                    }

                mFrndName?.text = name

                if (!(tmbImg?.equals("thumb img") as Boolean))
                    Picasso.get().load(tmbImg).into(mFrndImg)

            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@PersonalMsgActivity,"Error", Toast.LENGTH_SHORT).show()
            }


        })


        mRootRef?.child("Chat")?.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                if (!p0.hasChild(mFrndUserId.toString())){

                    var chatAddMap = HashMap<String,Any>()
                    chatAddMap.put("seen",false)
                    chatAddMap.put("timestamp",ServerValue.TIMESTAMP)

                    var chatUserMap = HashMap<String,Any>()
                    chatUserMap.put("Chat/" + mAuth?.currentUser?.uid + "/" + frnduserId,chatAddMap )
                    chatUserMap.put("Chat/"  + frnduserId + "/"+ mAuth?.currentUser?.uid ,chatAddMap )

                    mRootRef?.updateChildren(chatUserMap,object :DatabaseReference.CompletionListener{
                        override fun onComplete(p0: DatabaseError?, p1: DatabaseReference) {

                                if (p0 != null){

                                    Log.d("CHAT ERROR",p0.message)
                                }
                            }

                    })


                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        sendMsgBtn?.setOnClickListener {

            SendMsg()
        }
    }

    private fun loadMessages() {
        frnduserId = intent.getStringExtra("User_Id")
        var msgRef = FirebaseDatabase.getInstance().reference.child("Messages")
                .child(mAuth?.currentUser?.uid.toString()).child(frnduserId.toString())

        Toast.makeText(this,msgRef.toString(),Toast.LENGTH_SHORT).show()
                msgRef?.addChildEventListener(object :ChildEventListener{

                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                        if (p0.exists()) {

                           /* var m: String = p0.child("message").value.toString()
                            var s:String = p0.child("seen").value.toString()
                            var t:String = p0.child("type").value.toString()
                            var getMsgMap = HashMap<String,Any>()
                            getMsgMap.put("message",m)
                            getMsgMap.put("seen",s)
                            getMsgMap.put("type",t)*/

                            var msg = p0.getValue(Messages::class.java) as Messages
                            messageList.add(msg)
                            mMsgAdapter?.notifyDataSetChanged()
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                        Toast.makeText(this@PersonalMsgActivity,p0.child("message").toString(),Toast.LENGTH_SHORT).show()
                    }

                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                        Toast.makeText(this@PersonalMsgActivity,"hii",Toast.LENGTH_SHORT).show()

                    }

                    override fun onChildRemoved(p0: DataSnapshot) {
                        Toast.makeText(this@PersonalMsgActivity,p0.child("message").toString(),Toast.LENGTH_SHORT).show()                    }

                })
    }

    private fun SendMsg() {
        var msg:String = edtMsg?.text.toString()

        if(!TextUtils.isEmpty(msg)){

            var current_user_ref = "Messages/"+ mAuth?.currentUser?.uid + "/" + frnduserId
            var frnd_user_ref:String = "Messages/"+ frnduserId+ "/" + mAuth?.currentUser?.uid

            var user_msg_push:DatabaseReference? = mRootRef?.child("Messages")?.child(mAuth?.currentUser?.uid.toString())
                                                    ?.child(frnduserId.toString())?.push()

            var pushId:String? = user_msg_push?.key

            var MsgMap = HashMap<String,Any>()
            MsgMap.put("message",msg)
            MsgMap.put("seen",false)
            MsgMap.put("type","text")
            MsgMap.put("time",ServerValue.TIMESTAMP)
            MsgMap.put("from",mAuth?.currentUser?.uid.toString())

            var MsgChatMap = HashMap<String,Any>()
            MsgChatMap.put(current_user_ref + "/" + pushId,MsgMap)
            MsgChatMap.put(frnd_user_ref + "/" + pushId,MsgMap)

            mRootRef?.updateChildren(MsgChatMap) { DbError, DbRef ->

                edtMsg?.setText("")
                if (DbError != null){
                    Log.d("MSG ERROR",DbError.message)
                }
            }

        }
    }


}

package com.example.raj.mychatapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.users_single_layout.view.*

class AllUsersActivity : AppCompatActivity() {

    private var mToolbar:Toolbar?=null
    private var mUsersList:RecyclerView?=null
    private var mDBRef:DatabaseReference?=null
    var mAdapter:FirebaseRecyclerAdapter<Users,UsersViewHolder>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_users)

        mUsersList = findViewById(R.id.users_list)

        mToolbar =findViewById(R.id.users_appBar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "All Users"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mDBRef = FirebaseDatabase.getInstance().getReference("Users")

    }

    override fun onStart() {
        super.onStart()

        mUsersList?.setHasFixedSize(true)
        mUsersList?.layoutManager = LinearLayoutManager(this)





        // On the main screen of your app, you may want to show the 50 most recent chat/User messages.
        // With Firebase you would use the following query:
        var query:Query = FirebaseDatabase.getInstance()
                .reference
                .child("Users")
                .limitToLast(50)

        var options:FirebaseRecyclerOptions<Users> = FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(query,Users::class.java)
                .build()

        mAdapter =object :FirebaseRecyclerAdapter<Users,UsersViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {

                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                var view:View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.users_single_layout,parent,false)
                return UsersViewHolder(view)
            }

            override fun onBindViewHolder(holder: UsersViewHolder, position: Int, model: Users) {
                // Bind the Chat object to the ChatHolder
                holder.userName.text = model.name
                holder.userStatus.text =model.status
                if (!(model.thumbImg?.equals("thumb img") as Boolean))
                Picasso.get().load(model.thumbImg).into(holder.userImg)

                // get key postin for each user
                var user_id:String = getRef(position).key as String
                holder.itemView.setOnClickListener{

                    var prof_Intent = Intent(this@AllUsersActivity,UsersProfileActivity::class.java)
                    prof_Intent.putExtra("User_Id",user_id)
                    startActivity(prof_Intent)
                }
            }

        }
        mUsersList?.adapter =mAdapter

        mAdapter?.startListening()
    }

    private fun loadList() {


    }

    //to hold/create view of single user layout for access them through this created view
    public class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userName = itemView.users_single_name
        var userStatus = itemView.users_single_status
        var userImg = itemView.users_single_img


    }

    override fun onStop() {
        super.onStop()
        mAdapter?.stopListening()
    }
}

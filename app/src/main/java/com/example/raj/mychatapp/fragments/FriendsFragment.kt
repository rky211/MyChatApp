package com.example.raj.mychatapp.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.raj.mychatapp.*
import com.example.raj.mychatapp.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.friends_single_layout.view.*
import kotlinx.android.synthetic.main.users_single_layout.view.*


class FriendsFragment : Fragment() {

    private var mFrndList: RecyclerView?=null
    private var mFrndDBRef: DatabaseReference?=null
    private var mAuth:FirebaseAuth?=null
    private var current_user_id:String?=null
    private var mDBRef:DatabaseReference?=null


    var mAdapter: FirebaseRecyclerAdapter<Friends, FriendsViewHolder>?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_friends, container, false)

        mAuth = FirebaseAuth.getInstance()
        current_user_id = mAuth?.currentUser?.uid
        mFrndDBRef = FirebaseDatabase.getInstance().reference.child("Friend_list").child(current_user_id.toString())
        mDBRef = FirebaseDatabase.getInstance().getReference("Users")

        mFrndList = view.findViewById(R.id.frnd_list)

        mFrndList?.setHasFixedSize(true)
        mFrndList?.layoutManager = LinearLayoutManager(activity)

        return view
    }

    override fun onStart() {
        super.onStart()

        // With Firebase you would use the following query:
        var query: Query = FirebaseDatabase.getInstance()
                .reference
                .child("Friend_list")
                .child(current_user_id.toString())
                .limitToLast(50)

        var options: FirebaseRecyclerOptions<Friends> = FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(query,Friends::class.java)
                .build()

        mAdapter = object :FirebaseRecyclerAdapter<Friends,FriendsViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
                var view:View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.friends_single_layout,parent,false)
                return FriendsViewHolder(view)
            }

            override fun onBindViewHolder(holder: FriendsViewHolder, position: Int, model: Friends) {

                holder.date.text = model.date

                var user_id= getRef(position).key.toString()

                mFrndDBRef?.child(user_id)?.addValueEventListener(object :ValueEventListener{


                    override fun onDataChange(p0: DataSnapshot) {
                        var date:String = p0.child("Date").value.toString()

                        holder.date.text = date

                        mDBRef?.child(user_id)?.addValueEventListener(object :ValueEventListener{


                            override fun onDataChange(p0: DataSnapshot) {

                                var frnd_name:String = p0.child("name").value.toString()
                                var frnd_thmb_img:String = p0.child("thumbImg").value.toString()
                                var frnd_online:String = p0.child("online").value.toString()

                                holder.name.text = frnd_name

                                if(p0.hasChild("online")){
                                    if (frnd_online.equals(true)){
                                        holder.onlineImg.visibility = View.VISIBLE

                                    }
                                    else{

                                        holder.onlineImg.visibility = View.INVISIBLE
                                    }
                                }


                                if (!(frnd_thmb_img?.equals("thumb img") as Boolean))
                                    Picasso.get().load(frnd_thmb_img).into(holder.img)


                            }
                            override fun onCancelled(p0: DatabaseError) {
                                Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show()                    }

                        })
                    }
                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show()
                    }

                })

             holder.itemView.setOnClickListener {

                 var profMsgIntent = Intent(activity,PersonalMsgActivity::class.java)
                 profMsgIntent.putExtra("User_Id",user_id)
                 startActivity(profMsgIntent)
             }


            }

        }

        mFrndList?.adapter =mAdapter

        mAdapter?.startListening()
    }


    override fun onStop() {
        super.onStop()

        mAdapter?.stopListening()
    }

    public class FriendsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var date = itemView.frnds_single_status
        var name = itemView.frnds_single_name
        var img = itemView.frnds_single_img
        var onlineImg = itemView.img_online
    }

}

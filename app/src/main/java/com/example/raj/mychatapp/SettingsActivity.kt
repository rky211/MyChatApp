package com.example.raj.mychatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import java.net.URI
import android.R.attr.data
import android.app.PendingIntent.getActivity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import com.example.raj.mychatapp.R.drawable.my
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception


class SettingsActivity : AppCompatActivity() {

    private var mName:TextView?=null
    private var mStatus:TextView?=null
    private var mImg:CircleImageView?=null
    private var mStatusBtn:Button?=null
    private var mImgBtn:Button?=null

    var mDbRef:DatabaseReference?=null
    var currentUser:FirebaseUser?=null
    var mImgStorageRef:StorageReference?=null


    companion object {
        var STATUS_VALUE:String = "status value"
        var GALLERY_PICK:Int = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mName = findViewById(R.id.settings_name)
        mStatus = findViewById(R.id.settings_status)
        mImg = findViewById<CircleImageView>(R.id.settings_img)
        mStatusBtn = findViewById(R.id.setting_stauts_btn)
        mImgBtn  = findViewById(R.id.setting_img_btn)


        currentUser = FirebaseAuth.getInstance().currentUser
        mImgStorageRef = FirebaseStorage.getInstance().reference
    }

    override fun onStart() {
        super.onStart()



        mDbRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUser?.uid.toString())
        mDbRef?.keepSynced(true)

        mDbRef?.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                var name:String = p0.child("name").value.toString()
                var status:String = p0.child("status").value.toString()
                var img:String = p0.child("img").value.toString()
                var thumbImg:String = p0.child("thumbImg").value.toString()

                mName?.text = name
                mStatus?.text = status


                    if (!img.equals("img")){
                        Picasso.get().load(img).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.my).into(mImg,object:Callback{
                                    override fun onSuccess() {

                                    }

                                    override fun onError(e: Exception?) {
                                        Picasso.get().load(img).placeholder(R.drawable.my).into(mImg)                                    }

                                })
                    }

            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }


        )


        mStatusBtn?.setOnClickListener {

            var statusIntent = Intent(this,StatusActivity::class.java)
            statusIntent.putExtra(STATUS_VALUE,mStatus?.text.toString())
            startActivity(statusIntent)
        }

        mImgBtn?.setOnClickListener {

            //1st Method
            var gallery_Intent = Intent()
            gallery_Intent.type = "image/*"
            gallery_Intent.action = Intent.ACTION_GET_CONTENT //for retrieve selected img

            // because above choose img from only document
            // so we use createChooser for select image from External folder
            startActivityForResult(Intent.createChooser(gallery_Intent,"SELECT IMAGE"), GALLERY_PICK)


            /* 2nd Method
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this)
            */


            /*3 for fragment (DO NOT use `getActivity()`)
            CropImage.activity()
                    .start(getContext(), this);
            */
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_PICK && resultCode == Activity.RESULT_OK)
        {
            var imgUri:Uri = data?.data as Uri

            // start cropping activity for pre-acquired image saved on the device
                       CropImage
                               .activity(imgUri)
                               .setAspectRatio(1,1)
                               .setMinCropWindowSize(500,500)
                               .start(this);

            Toast.makeText(this,imgUri.toString(),Toast.LENGTH_SHORT)
                    .show()
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {

            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK)
            {
                val resultUri = result.uri
                orignalImg(result.uri)

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                val error = result.error
            }
        }

    }

    private fun orignalImg(resultUri:Uri) {
        var imgFilepath:StorageReference = mImgStorageRef?.child("Profile_Img")
                ?.child(currentUser?.uid.toString()) as StorageReference

        //---store in Storage
        imgFilepath.putFile(resultUri).addOnCompleteListener {
            if (it.isSuccessful)
            {
                Toast.makeText(this,"Image Uploaded in Storage",Toast.LENGTH_SHORT).show()

                //---store in database
                imgFilepath.downloadUrl.addOnSuccessListener {
                    mDbRef?.child("img")?.setValue(it.toString())?.addOnCompleteListener {
                        if (it.isSuccessful){
                            thumbImg(resultUri)
                            Toast.makeText(this,"Image Uploaded in Database",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this,"Error ",Toast.LENGTH_SHORT).show()
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this,"Error in Retrieving",Toast.LENGTH_LONG).show()
                }

            }
            else{
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun thumbImg(resultUri:Uri) {
        var thumb_filePath = File(resultUri.path)
        //Compress Image File to Bitmap
        var thumb_bitMap =  Compressor(this)
                .setMaxWidth(200)   // decrease width size from 500px to 200px
                .setMaxHeight(200)  // decrease height size from 500px to 200px
                .setQuality(75)     //decrease quality from 100% to 75%
                .compressToBitmap(thumb_filePath);

        var baos:ByteArrayOutputStream = ByteArrayOutputStream()
        thumb_bitMap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        var thum_byte = baos.toByteArray()

        var thmbImgFilepath:StorageReference = mImgStorageRef?.child("Profile_Img")
                ?.child("Thumb")?.child(currentUser?.uid.toString()) as StorageReference

        //---store in Storage
        thmbImgFilepath.putBytes(thum_byte).addOnCompleteListener {
            if (it.isSuccessful)
            {
                Toast.makeText(this,"Image Uploaded in Storage",Toast.LENGTH_SHORT).show()

                //---store in database
                thmbImgFilepath.downloadUrl.addOnSuccessListener {
                    mDbRef?.child("thumbImg")?.setValue(it.toString())?.addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this,"Image Uploaded in Database",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this,"Error ",Toast.LENGTH_SHORT).show()
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this,"Error in Retrieving",Toast.LENGTH_LONG).show()
                }

            }
            else{
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
            }
        }
    }
}

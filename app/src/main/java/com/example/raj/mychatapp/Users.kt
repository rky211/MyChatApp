package com.example.raj.mychatapp

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Users {

    var name:String?=null
    var email:String?=null
    var pswrd:String?=null
    var status:String?=null
    var thumbImg:String?=null
    var img:String?=null
    var deviceToken:String?=null
    constructor(){


    }

    constructor(name:String,email:String,pswrd:String,status:String,img:String,thumbImg:String,dt:String){
        this.name = name
        this.email = email
        this.pswrd = pswrd
        this.status = status
        this.img = img
        this.thumbImg = thumbImg
        this.deviceToken = dt
    }




}
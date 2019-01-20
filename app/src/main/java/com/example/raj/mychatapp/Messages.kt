package com.example.raj.mychatapp

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Messages {

    var message:String?=null
    var seen:Boolean?=null
    var time:Long?=null
    var type:String?=null
    var from:String?=null

    constructor(){

    }


    constructor(msg:String,seen:Boolean,time:Long,typ:String,frm:String){

        this.message =msg
        this.type = typ
        this.seen = seen
        this.time = time
        this.from = frm
    }
}
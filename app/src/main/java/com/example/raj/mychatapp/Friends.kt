package com.example.raj.mychatapp

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Friends {
    var date:String?=null
    var thumbImg:String?=null
    var name:String?=null
    constructor(){


    }
    constructor(nam:String,dt:String,timg:String){

        this.name = nam
        this.date = dt
        this.thumbImg = timg
    }



}
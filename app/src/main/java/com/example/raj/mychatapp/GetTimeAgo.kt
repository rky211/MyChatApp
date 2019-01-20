package com.example.raj.mychatapp

import android.app.Application
import android.content.Context
import android.renderscript.Long2
import android.renderscript.Long3
import android.renderscript.Long4

class GetTimeAgo : Application() {


    companion object {
        val SECOND_MILLIS: Int = 1000;
        val MINUTE_MILLIS: Int = 60 * SECOND_MILLIS;
        val HOUR_MILLIS: Int = 60 * MINUTE_MILLIS;
        val DAY_MILLIS: Int = 24 * HOUR_MILLIS;

    }
    fun getTimeAgo(time:Long, ctx: Context): String? {

        var mTime = time
        // if timestamp given in seconds, convert to millis
        if (mTime < 1000000000000L) {

            mTime *= 1000
        }

        var now: Long = System.currentTimeMillis()
        if (mTime > now || mTime <= 0) {
            return null;
        }


        val diff: Long = now - mTime;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < (2 * MINUTE_MILLIS)) {
            return "a minute ago";
        } else if (diff < (50 * MINUTE_MILLIS)) {
            var i = diff / MINUTE_MILLIS
            return i.toString() + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            var i =diff / HOUR_MILLIS
                    return i.toString()+" hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            var i = diff / DAY_MILLIS
            return i.toString()+ " days ago";
        }

    }


}

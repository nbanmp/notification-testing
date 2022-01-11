package com.example.flutter_app_notifs

import android.app.Notification
import android.app.Person
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

class NotificationCollection(private var applicationContext: Context) {
    var notifications = HashMap<String, ArrayList<Notification>>();

    fun getAppName(packageName: String) : String {
        val pm = applicationContext.packageManager
        val ai: ApplicationInfo?
        ai = try {
            pm.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }

        // return package name on fail
        return (if (ai != null) pm.getApplicationLabel(ai) else packageName) as String;
    }

    fun addNotification(notification: Notification, packageName: String) {
        // TODO: Check for Duplicates
        val appName = getAppName(packageName);

        // Not sure if creating an arraylist is required when it doesn't exist.
        if(!notifications.containsKey(appName)){
            notifications[appName] = ArrayList<Notification>();
        }

        // Add notification if it's not already been added
        if(!notifications[appName]!!.contains(notification)) {
            notifications[appName]!!.add(notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun removeNotification(notificationId: String) {
        // FIXME: TITLE IS AWFUL, also use appname
        var done = false
        var appNameToRemove : String? = null;
        var notificationToRemove : Notification? = null;
        for(appName in this.notifications.keys) {
            for(notification in this.notifications[appName]!!) {
                // TODO: Save this somewhere, create a NotificationCollector object
                if (notificationId == notification.extras.getCharSequence(Notification.EXTRA_TITLE).toString()) {
                    notificationToRemove = notification
                    appNameToRemove = appName
                    done = true;
                    break
                }
                if(done) break
            }
        }
        if(appNameToRemove != null && notificationToRemove != null) {
            this.notifications[appNameToRemove]!!.remove(notificationToRemove)
            println("Successfully removed notification.")
            if (this.notifications[appNameToRemove]!!.isEmpty()) {
                this.notifications.remove(appNameToRemove)
            }
        }
    }

    fun dumpNotification(notification: Notification) {
        //println(notification.toString())
        val notif: Notification = notification
        //println(notif.toString())
        //println(notif.extras)
        //println(notif.extras.getCharSequence(EXTRA_AUDIO_CONTENTS_URI))
        //println(notif.extras.getCharSequence(EXTRA_BACKGROUND_IMAGE_URI))
        println("big text")
        println(notif.extras.getCharSequence(Notification.EXTRA_BIG_TEXT))
        println("text")
        println(notif.extras.getCharSequence(Notification.EXTRA_TEXT))
        println("text lines")
        println(notif.extras.getCharSequence(Notification.EXTRA_TEXT_LINES))
        //println(notif.extras.getCharSequence(EXTRA_CHANNEL_GROUP_ID))
        //println(notif.extras.getCharSequence(EXTRA_CHANNEL_ID))
        //println(notif.extras.getCharSequence(EXTRA_CHRONOMETER_COUNT_DOWN))
        //println(notif.extras.getCharSequence(EXTRA_COLORIZED))
        println("conversation title")
        println(notif.extras.get(Notification.EXTRA_CONVERSATION_TITLE))
        //println(notif.extras.getCharSequence(EXTRA_HISTORIC_MESSAGES))
        //println(notif.extras.getCharSequence(EXTRA_INFO_TEXT))
        println("messaging person")
        println(notif.extras.getParcelable<Person>(Notification.EXTRA_MESSAGING_PERSON))
        println("messages")
        println(notif.extras.getParcelableArray(Notification.EXTRA_MESSAGES))
        println("title")
        println(notif.extras.getCharSequence(Notification.EXTRA_TITLE))
        println("title big")
        println(notif.extras.getCharSequence(Notification.EXTRA_TITLE_BIG))

    }
}
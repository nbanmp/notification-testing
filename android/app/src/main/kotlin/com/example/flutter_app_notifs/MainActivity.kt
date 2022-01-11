package com.example.flutter_app_notifs

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity: FlutterActivity() {
    private val CHANNEL = "flutter_app_notifs.example.com/notifications"

    companion object {
        lateinit var notificationCollection : NotificationCollection;
        lateinit var newNotificationHandler : MethodChannel.Result;
    }


    @RequiresApi(VERSION_CODES.KITKAT)
    private fun getNotifications(): ArrayList<ArrayList<String>> {
        /*
        TODO: Re-enable this, and actually consume notifications when they are found.
        val myNotificationService : NotificationListener? = NotificationListener.get();
        if(myNotificationService != null) {
            for (statusBarNotification in myNotificationService.getActiveNotifications()) {
                notificationCollection.addNotification(
                        statusBarNotification.notification,
                        statusBarNotification.packageName
                )

                println("New Notification:    " + statusBarNotification.getPackageName() + " / " + statusBarNotification.getTag())
                val applicationName = notificationCollection.getAppName(statusBarNotification.packageName)
                println("App name: " + applicationName)
                notificationCollection.dumpNotification(statusBarNotification.notification)
            }
        }
        */

        val list = ArrayList<ArrayList<String>>()
        // Better to do this per app.
        for(appName in notificationCollection?.notifications!!.keys) {
            for(notification in notificationCollection?.notifications!![appName]!!) {
                // TODO: Save this somewhere, create a NotificationCollector object
                val title = notification.extras.getCharSequence(Notification.EXTRA_TITLE).toString()
                val text = notification.extras.getCharSequence(Notification.EXTRA_TEXT).toString()
                val notificationID = notification.extras.getCharSequence(Notification.EXTRA_TITLE).toString()

                val notificationL = ArrayList<String>();
                notificationL.add(appName)
                notificationL.add(title)
                notificationL.add(text)
                notificationL.add(notificationID)
                if(list.find({ notifL : ArrayList<String> ->
                    notifL[3] == notificationID
                }) == null) {
                    list.add(notificationL);
                }
            }
        }

        return list
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val channelId = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(VERSION_CODES.KITKAT)
    private fun deleteNotification(str: String?) {
        println("Delete Notification " + str);
        if(str != null) {
            notificationCollection.removeNotification(str);
        }
    }


    @RequiresApi(VERSION_CODES.KITKAT)
    private fun sendNotification(str: String?) {
        println(str);
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        var builder = NotificationCompat.Builder(this, getString(R.string.channel_name))
                .setSmallIcon(R.drawable.launch_background) // TODO: better icon
                .setContentTitle(str)
                .setContentText(str)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        println("Building notification")

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(str!!.toInt(), builder.build())
            println("Called Notify.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("On Create")
        var legalNotificationListeners = NotificationManagerCompat.getEnabledListenerPackages(
                applicationContext
        );
        if (legalNotificationListeners.contains(applicationContext.packageName)) {
            println("Can notify.")
        } else {
            println("Can't notify, starting activity to request permission.")
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }

        notificationCollection = NotificationCollection(applicationContext);

        createNotificationChannel();

    }

    @RequiresApi(VERSION_CODES.KITKAT)
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            call, result ->
            // Note: this method is invoked on the main thread.
            if (call.method == "getNotifications") {
                println("Get Notifications Called.");
                val notifications = getNotifications()

                //if (notifications) {
                    result.success(notifications)
                //} else {
                    //result.error("UNAVAILABLE", "Notifications not available.", null)
                //}
            } else if (call.method == "sendNotification") {
                println("Send Notification Called.");
                sendNotification(call.argument<String>("str"))
            } else if (call.method == "deleteNotification") {
                println("Delete Notification Called.");
                deleteNotification(call.argument<String>("str"))
                result.success(0);
            } else if (call.method == "loadNewNotificationHandler") {
                newNotificationHandler = result;
            } else {
                result.notImplemented()
            }
        }
    }
}

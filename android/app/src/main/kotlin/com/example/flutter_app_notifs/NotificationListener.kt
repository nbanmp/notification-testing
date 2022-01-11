package com.example.flutter_app_notifs

import android.annotation.SuppressLint
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi

// sem : Semaphore = Semaphore(0);

@RequiresApi(Build.VERSION_CODES.KITKAT)
@SuppressLint("OverrideAbstract")
class NotificationListener : NotificationListenerService() {

    companion object {
        var _this : NotificationListener? = null;

        @JvmStatic // may not be needed
        fun get() : NotificationListener? {
            println("Get called.")
            var ret : NotificationListener? = _this;
            return ret
        }
    }

    public override fun onListenerConnected() {
        println("Listener connected.")
        _this = this
    }

    public override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        println("Notification Posted.")
        if(sbn != null) {
            MainActivity.notificationCollection.addNotification(sbn.notification, sbn.packageName)
            MainActivity.newNotificationHandler.success(1);
        }
    }
}

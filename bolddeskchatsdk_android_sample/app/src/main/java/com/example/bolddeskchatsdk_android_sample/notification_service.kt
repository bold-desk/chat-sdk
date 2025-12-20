package com.example.bolddeskchatsdk_android_sample

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.syncfusion.bolddeskandroidchatSDK.BoldDeskChatSDK

// A service to handle incoming FCM messages and new token generation
class NotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (BoldDeskChatSDK.isFromChatSDK(remoteMessage.getData())) {
            // Call this method to handle the incoming push notification
            BoldDeskChatSDK.handlePushNotifications(
                this,
                remoteMessage.getData(),
                icon = R.drawable.ic_launcher_background
            )
        }
    }


    override fun onNewToken(token: String) {
        // If token updated then send the new token to BoldDesk SDK
        super.onNewToken(token)
        fcm_token = token
        BoldDeskChatSDK.enablePushNotification(token)
    }

}


fun getToken() {
    try {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            fcm_token = token
        })
    } catch (e: Exception) {
        Log.d("Firebase", e.toString())
    }
}
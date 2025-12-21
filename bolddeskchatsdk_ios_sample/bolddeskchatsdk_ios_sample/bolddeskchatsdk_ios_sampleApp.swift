import SwiftUI
import UIKit
import FirebaseCore
import FirebaseMessaging
import UserNotifications
import BoldDeskChatSDK

class AppDelegate: NSObject, UIApplicationDelegate,
    UNUserNotificationCenterDelegate, MessagingDelegate {
    // Called when the app finishes launching.
    // Configures Firebase, registers for notifications, and handles launch from a remote notification.
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication
            .LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // Configure Firebase.
//        FirebaseApp.configure()
        
        
        
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self
        // Request permission to show alerts, badges, and sounds.
        UNUserNotificationCenter.current().requestAuthorization(options: [
            .alert, .badge, .sound,
        ]) { _, _ in }
        // Register for push notifications.
        application.registerForRemoteNotifications()
        return true
    }

    // Called when the app successfully registers with APNs and receives the device token.
    // Links the APNs token with Firebase Messaging to enable FCM.
    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        Messaging.messaging().apnsToken = deviceToken
        // Retrieve the FCM token associated with this device.
        Messaging.messaging().token { token, _ in
            if let token = token {
                print("fcm token: \(token)")
            }
        }
    }
    
    // Called when a new FCM registration token is generated or updated.
    // You can send this token to your server or SDK.
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
            BDChatSDK.enablePushNotification(fcmToken: fcmToken ?? "") // Uncomment if BolddeskChatSDK supports it
    }
    
    // Called when a notification is received while the app is in the foreground.
    // Displays the notification banner, sound, and badge even if app is active.
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler:
            @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        let userInfo = notification.request.content.userInfo
        if BDChatSDK.isFromChatSDK(userInfo: userInfo) {
            // Only show notification if chat is open, otherwise suppress it.
            // You'll need to implement BoldDeskChatSDK.chatIsOpen() or a similar mechanism.
            if BDChatSDK.isChatOpen() { // Assuming chatIsOpen() is a method in BoldDeskChatSDK
                completionHandler([]) // Suppress the notification
            } else {
                completionHandler([.banner, .sound, .badge])
            }
        } else {
            // For non-SDK notifications, show them as usual.
            completionHandler([.banner, .sound, .badge])
        }
    }
    // Called when the user taps on a notification.
    func userNotificationCenter(_ center: UNUserNotificationCenter,didReceive response: UNNotificationResponse,withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            if BDChatSDK.isFromChatSDK(userInfo: userInfo) {
                BDChatSDK.showChat()
            }
        }
        completionHandler()
    }
}

// MARK: - SwiftUI App
@main
struct bolddeskchatsdk_ios_sampleApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

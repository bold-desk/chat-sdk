//
//  bolddeskchatsdk_ios_sampleApp.swift
//  bolddeskchatsdk_ios_sample
//
//  Created by Jaganathan Raja on 07/12/25.
//

import SwiftUI
import SampleSwiftUIFramework

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        BDChatSDK.configure(appToken: "android_sdk_LjjZtgcIkOVZJA5z04ttkv2aiEdoTJQQuDj3d78oKQw", domainURL: "https://dev-chat-integration.bolddesk.com")
        return true
    }
}

@main
struct bolddeskchatsdk_ios_sampleApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

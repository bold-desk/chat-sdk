//
//  ContentView.swift
//  bolddeskchatsdk_ios_sample
//
//  Created by Jaganathan Raja on 07/12/25.
//

import SwiftUI
import SampleSwiftUIFramework


struct ContentView: View {
    var body: some View {
        VStack(spacing: 20) {
            Button("Show Chat (manual)") {
                BDChatSDK.showChat()
            }
            Button("Clear Chat") {
                BDChatSDK.clearChatSession()
            }
            Button("Set light theme") {
                BDChatSDK.setPreferredTheme(.light)
            }
            Button("Set dark theme") {
                BDChatSDK.setPreferredTheme(.dark) // Fixed: was incorrectly using .Theme =
            }
        }
        .padding()
        // This will automatically open the chat when the view appears
    }
}

#Preview {
    ContentView()
}

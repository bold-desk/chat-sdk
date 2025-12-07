package com.example.bolddeskchatsdk_android_sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bolddeskchatsdk_android_sample.ui.theme.Bolddeskchatsdk_android_sampleTheme
import com.syncfusion.bolddeskandroidchatSDK.BDChatSDK

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        BDChatSDK.configure(
//            "android_sdk_n3m7wgQSRK23Y0BJ32JKbpj5t2tZH3kYkxnNPzAKrMY",
//            "https://stagingboldsign.bolddesk.com/",
//            "en-US"
//        )
//        BDChatSDK.showChat(context = this)
//        enableEdgeToEdge()
//        setContent {
//            Bolddeskchatsdk_android_sampleTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Configure the SDK once (sample values shown)
        BDChatSDK.configure(
            "android_sdk_LjjZtgcIkOVZJA5z04ttkv2aiEdoTJQQuDj3d78oKQw",
            "https://dev-chat-integration.bolddesk.com/",
            "en-US"
        )

        setContent {
            // Use your app theme wrapper; inner MaterialTheme used for demo UI
            Bolddeskchatsdk_android_sampleTheme {
                HostAppUI()
            }
        }
    }
}

@Composable
fun HostAppUI() {
    val context = LocalContext.current

    MaterialTheme(colorScheme = lightColorScheme()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Host App - Configure SDK and Show Chat Widget",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Theme Set Buttons
            Text("Set Theme", style = MaterialTheme.typography.titleSmall)
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { BDChatSDK.setPreferredTheme(BDChatSDK.SDKTheme.LIGHT) }) {
                    Text("Set Light Theme")
                }
                Button(onClick = { BDChatSDK.setPreferredTheme(BDChatSDK.SDKTheme.DARK) }) {
                    Text("Set Dark Theme")
                }
            }

//            // Set User Data
//            Text("Set User Data", style = MaterialTheme.typography.titleSmall)
//            Button(onClick = {
//                BDChatSDK.setUserEmail("testandroidsdk2@gmail.com")
//                BDChatSDK.setUserName("dinesh2")
//                BDChatSDK.setUserPhoneNo("9876543012")
//                BDChatSDK.setUserToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.[REDACTED].Ydrvdd5_QywVli0z1qPkdZYrmy8zDhwdDbaFngBUKhk")
//            }) {
//                Text("Set user data")
//            }

//            Button(onClick = {
//                BDChatSDK.setOnValidate { formData ->
//                    // Example validation hook
//                    Pair(true, "Welcome! Thanks for verifying.")
//                }
//            }) {
//                Text("Set OnValidate")
//            }

            // Show Widget Button
            Button(onClick = { BDChatSDK.showChat(context) }) {
                Text("Show Chat Widget")
            }

            // Clear Session Button
            Button(onClick = { BDChatSDK.clearChatSession() }) {
                Text("Clear Chat Session")
            }
        }
    }
}
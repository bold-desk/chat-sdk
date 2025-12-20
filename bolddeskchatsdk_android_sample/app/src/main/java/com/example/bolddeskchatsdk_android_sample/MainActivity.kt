package com.example.bolddeskchatsdk_android_sample

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.syncfusion.bolddeskandroidchatSDK.BoldDeskChatSDK
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardOptions
import com.example.bolddeskchatsdk_android_sample.ui.theme.Bolddeskchatsdk_android_sampleTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BrandColor = Color(0xFF155EEF)
var fcm_token = ""

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Configure the SDK once (sample values shown)
//        FirebaseApp.initializeApp(this)
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                return@OnCompleteListener
//            }
//            val token = task.result
//            fcm_token = token
//            BoldDeskChatSDK.enablePushNotification(fcmToken = token)
//        })

        BoldDeskChatSDK.enableLogging()

        setContent {
            Bolddeskchatsdk_android_sampleTheme {
//                NotificationPermissionScreen()
                HostAppUI()
            }
        }
    }
}

@Composable
fun HostAppUI() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("BDChatPrefs", Context.MODE_PRIVATE) }

    var appToken by remember {
        mutableStateOf(
            sharedPrefs.getString("appToken", "") ?: ""
        )
    }
    var domainUrl by remember {
        mutableStateOf(
            sharedPrefs.getString("domainUrl", "") ?: ""
        )
    }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(appToken, domainUrl) {
        if (appToken.isNotBlank() && domainUrl.isNotBlank()) {
            BoldDeskChatSDK.configure(context, appToken.trim(), domainUrl.trim())
            with(sharedPrefs.edit()) {
                putString("appToken", appToken.trim())
                putString("domainUrl", domainUrl.trim())
                apply()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SDK Configuration",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = appToken,
            onValueChange = { appToken = it },
            label = { Text("App ID") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            value = domainUrl,
            onValueChange = { domainUrl = it },
            label = { Text("Brand URL") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                isSaving = true
                Toast.makeText(context, "Configuration saved & applied!", Toast.LENGTH_SHORT).show()
                kotlinx.coroutines.GlobalScope.launch {
                    delay(600)
                    isSaving = false
                }
            },
            enabled = appToken.isNotBlank() && domainUrl.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = BrandColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text("Configure", color = Color.White)
        }
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(
            onClick = { BoldDeskChatSDK.showChat(context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Show Chat", color = BrandColor)
        }

        TextButton(
            onClick = {
                BoldDeskChatSDK.clearSession()
                BoldDeskChatSDK.disablePushNotification(fcm_token)
                Toast.makeText(context, "Session cleared", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Clear Chat", color = BrandColor)
        }

        TextButton(
            onClick = { BoldDeskChatSDK.setPreferredTheme(BoldDeskChatSDK.SDKTheme.LIGHT) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set light theme", color = BrandColor)
        }

        TextButton(
            onClick = { BoldDeskChatSDK.setPreferredTheme(BoldDeskChatSDK.SDKTheme.DARK) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set dark theme", color = BrandColor)
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}
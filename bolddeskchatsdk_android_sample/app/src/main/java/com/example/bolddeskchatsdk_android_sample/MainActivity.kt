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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.draw.drawBehind
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
    var culture by remember {
        mutableStateOf(
            sharedPrefs.getString("culture", "en-US") ?: "en-US"
        )
    }
    var isSaving by remember { mutableStateOf(false) }

    // Prefill fields state
    var prefEmail by remember { mutableStateOf("") }
    var prefName by remember { mutableStateOf("") }
    var prefPhone by remember { mutableStateOf("") }

    // Custom fields state
    var customKey by remember { mutableStateOf("") }
    var customValue by remember { mutableStateOf("") }
    var customFields by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }

    // Theme colors state
    var appbarColor by remember { mutableStateOf(Color(0x00, 0xF7, 0xFF)) }
    var accentColor by remember { mutableStateOf(Color(0xA8, 0xDF, 0x8E)) }
    var backgroundColor by remember { mutableStateOf(Color(0xF6, 0xF0, 0xD7)) }
    var stickyButtonColor by remember { mutableStateOf(Color(0xAE, 0xDE, 0xFC)) }

    var statusMessage by remember { mutableStateOf("") }

    val fontOptions = listOf("Inter", "Roboto", "Poppins", "Open Sans")
    var selectedFont by remember { mutableStateOf("Inter") }
    var expandFonts by remember { mutableStateOf(false) }

    data class FontSet(val regular: Int, val medium: Int, val semibold: Int, val bold: Int)

    val fontMap = mapOf(
        "Inter" to FontSet(
            R.font.inter_regular, R.font.inter_medium, R.font.inter_semibold, R.font.inter_bold
        ),
        "Roboto" to FontSet(
            R.font.roboto_regular, R.font.roboto_medium, R.font.roboto_semibold, R.font.roboto_bold
        ),
        "Poppins" to FontSet(
            R.font.poppins_regular,
            R.font.poppins_medium,
            R.font.poppins_semibold,
            R.font.poppins_bold
        ),
        "Open Sans" to FontSet(
            R.font.open_sans_regular,
            R.font.open_sans_medium,
            R.font.open_sans_semibold,
            R.font.open_sans_bold
        )
    )


    LaunchedEffect(appToken, domainUrl, culture) {
        if (appToken.isNotBlank() && domainUrl.isNotBlank()) {
            val cultureValue = culture.trim().ifBlank { "en-US" }
            BoldDeskChatSDK.configure(context, appToken.trim(), domainUrl.trim(), cultureValue)
            with(sharedPrefs.edit()) {
                putString("appToken", appToken.trim())
                putString("domainUrl", domainUrl.trim())
                putString("culture", cultureValue)
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
                val cultureValue = culture.trim().ifBlank { "en-US" }
                BoldDeskChatSDK.configure(context, appToken.trim(), domainUrl.trim(), cultureValue)
                with(sharedPrefs.edit()) {
                    putString("appToken", appToken.trim())
                    putString("domainUrl", domainUrl.trim())
                    putString("culture", cultureValue)
                    apply()
                }
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
                BoldDeskChatSDK.disablePushNotification()
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

        // Theme Colors Section
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Theme Colors",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            ColorPickerRow(
                label = "Appbar Color",
                color = appbarColor,
                onColorChange = { appbarColor = it }
            )
            ColorPickerRow(
                label = "Accent Color",
                color = accentColor,
                onColorChange = { accentColor = it }
            )
            ColorPickerRow(
                label = "Background Color",
                color = backgroundColor,
                onColorChange = { backgroundColor = it }
            )
            ColorPickerRow(
                label = "Sticky Button Color",
                color = stickyButtonColor,
                onColorChange = { stickyButtonColor = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    BoldDeskChatSDK.applyTheme(
                        context = context,
                        appBarColor = appbarColor.toHex(),
                        accentColor = accentColor.toHex(),
                        backgroundColor = backgroundColor.toHex(),
                        stickyButtonColor = stickyButtonColor.toHex()
                    )
                    statusMessage = "Theme applied."
                    Toast.makeText(context, "Theme applied", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Theme", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Prefill / Custom Fields Section
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Prefill / Custom Fields",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = prefEmail,
                onValueChange = { prefEmail = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = prefName,
                onValueChange = { prefName = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = prefPhone,
                onValueChange = { prefPhone = it },
                label = { Text("Phone") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Custom field key-value input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customKey,
                    onValueChange = { customKey = it },
                    label = { Text("Key") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = customValue,
                    onValueChange = { customValue = it },
                    label = { Text("Value") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        val key = customKey.trim()
                        if (key.isNotEmpty()) {
                            customFields = customFields + (key to parseValue(customValue))
                            customKey = ""
                            customValue = ""
                        }
                    },
                    enabled = customKey.trim().isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandColor.copy(alpha = 0.15f)),
                    modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically)
                ) {
                    Text("Add", color = BrandColor)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display custom fields
            if (customFields.isNotEmpty()) {
                customFields.toList().sortedBy { it.first }.forEach { (key, value) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = value.toString(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            IconButton(onClick = {
                                customFields = customFields - key
                            }) {
                                Text("🗑️", color = Color.Red)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    BoldDeskChatSDK.setPrefillFields(
                        name = prefName,
                        email = prefEmail,
                        phoneNumber = prefPhone,
                        chatFields = customFields.ifEmpty { null }
                    )
                    statusMessage = "Prefill fields applied."
                    Toast.makeText(context, "Prefill fields applied", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Custom Fields", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { expandFonts = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(selectedFont)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            DropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = expandFonts,
                onDismissRequest = { expandFonts = false }
            ) {
                fontOptions.forEach { name ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            selectedFont = name
                            expandFonts = false
                            fontMap[name]?.let { f ->
                                BoldDeskChatSDK.applyCustomFontFamily(
                                    f.regular, f.medium, f.semibold, f.bold
                                )
                                Toast.makeText(context, "Applied $name", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        // Status message
        if (statusMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun ColorPickerRow(
    label: String,
    color: Color,
    onColorChange: (Color) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = color),
            modifier = Modifier.height(36.dp)
        ) {
            Text(color.toHex(), color = Color.White)
        }
    }

    if (showDialog) {
        ColorPickerDialog(
            initialColor = color,
            onColorSelected = { newColor ->
                onColorChange(newColor)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var red by remember { mutableStateOf(initialColor.red) }
    var green by remember { mutableStateOf(initialColor.green) }
    var blue by remember { mutableStateOf(initialColor.blue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick a Color") },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(vertical = 8.dp)
                        .then(
                            Modifier.drawBehind {
                                drawRect(Color(red, green, blue))
                            }
                        )
                )
                Text("Red: ${(red * 255).toInt()}")
                Slider(
                    value = red,
                    onValueChange = { red = it },
                    valueRange = 0f..1f
                )
                Text("Green: ${(green * 255).toInt()}")
                Slider(
                    value = green,
                    onValueChange = { green = it },
                    valueRange = 0f..1f
                )
                Text("Blue: ${(blue * 255).toInt()}")
                Slider(
                    value = blue,
                    onValueChange = { blue = it },
                    valueRange = 0f..1f
                )
                Text("Hex: ${Color(red, green, blue).toHex()}")
            }
        },
        confirmButton = {
            Button(onClick = { onColorSelected(Color(red, green, blue)) }) {
                Text("Select")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper function to parse value from string
fun parseValue(str: String): Any {
    val trimmed = str.trim()
    if (trimmed.isEmpty()) return ""

    val lower = trimmed.lowercase()

    // Check for boolean
    if (lower == "true") return true
    if (lower == "false") return false

    // Check for integer
    trimmed.toIntOrNull()?.let { return it }

    // Check for double
    trimmed.toDoubleOrNull()?.let { return it }

    // Check for JSON array
    if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
        try {
            // Simple array parsing for comma-separated values
            val content = trimmed.substring(1, trimmed.length - 1)
            if (content.isNotEmpty()) {
                val items = content.split(",").map { it.trim() }
                return items.mapNotNull { item ->
                    item.toIntOrNull() ?: item.toDoubleOrNull() ?: item
                }
            }
        } catch (e: Exception) {
            // If parsing fails, return as string
        }
    }

    // Check for comma-separated values
    if (trimmed.contains(",")) {
        val parts = trimmed.split(",").map { part ->
            val s = part.trim()
            s.toIntOrNull() ?: s.toDoubleOrNull() ?: when (s.lowercase()) {
                "true" -> true
                "false" -> false
                else -> s
            }
        }
        return parts
    }

    // Return as string
    return trimmed
}

// Extension function to convert Color to Hex
fun Color.toHex(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}
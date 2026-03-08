import SwiftUI
import BoldDeskChatSDK
import UIKit
import FirebaseMessaging

struct ContentView: View {
    let fonts = ["Inter", "Roboto", "Poppins", "Times New Roman", "Open Sans"]
    @State private var selectedFont = "Inter"
    @AppStorage("bolddesk.sample.appKey") private var appId = ""
    @AppStorage("bolddesk.sample.brandUrl") private var brandId = ""
    @State private var languageCode = ""
    @State private var isConfigured = false
    @State private var statusMessage: String?
    @State private var customKey = ""
    @State private var customValue = ""
    @State private var customFields: [String: Any] = [:]

    @AppStorage("bolddesk.sample.prefill.email") private var prefEmail = ""
    @State private var prefName = ""
    @State private var prefPhone = ""
    @State private var userToken = ""
    @State private var appbarColor: Color = Color(red: 0.0, green: 247.0/255.0, blue: 1.0)
    @State private var accentColor: Color = Color(red: 168.0/255.0, green: 223.0/255.0, blue: 142.0/255.0)
    @State private var backgroundColor: Color = Color(red: 246.0/255.0, green: 240.0/255.0, blue: 215.0/255.0)
    @State private var stickyButtonColor: Color = Color(red: 174.0/255.0, green: 222.0/255.0, blue: 252.0/255.0)

    var body: some View {
        ScrollView(.vertical) {
            VStack(alignment: .leading, spacing: 20) {
            VStack(alignment: .leading, spacing: 12) {
                Text("SDK Configuration")
                    .font(.headline)

                configurationField(title: "App ID", text: $appId)
                configurationField(title: "Brand URL", text: $brandId)
                configurationField(title: "Language Code (e.g. en-US)", text: $languageCode)

                Button(action: configureSDK) {
                    Text("Configure")
                        .fontWeight(.semibold)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.accentColor)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
                .disabled(appId.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || brandId.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
            }

            if let statusMessage {
                Text(statusMessage)
                    .font(.footnote)
                    .foregroundColor(.green)
            }

            Divider()
            // MARK: - Prefill / Custom fields UI
            VStack(alignment: .leading, spacing: 12) {
                // Theme color pickers
                VStack(alignment: .leading, spacing: 8) {
                    Text("Theme Colors")
                        .font(.headline)

                    ColorPicker("Appbar Color", selection: $appbarColor, supportsOpacity: false)
                    ColorPicker("Accent Color", selection: $accentColor, supportsOpacity: false)
                    ColorPicker("Background Color", selection: $backgroundColor, supportsOpacity: false)
                    ColorPicker("Sticky Button Color", selection: $stickyButtonColor, supportsOpacity: false)

                    Button(action: applyTheme) {
                        Text("Apply Theme")
                            .fontWeight(.semibold)
                            .frame(maxWidth: .infinity)
                            .padding(8)
                            .background(Color.accentColor.opacity(0.9))
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                }
                
                Text("Prefill / Custom Fields")
                    .font(.headline)

                TextField("Email", text: $prefEmail)
                    .autocapitalization(.none)
                    .autocorrectionDisabled()
                    .padding(8)
                    .background(RoundedRectangle(cornerRadius: 8).stroke(Color.primary.opacity(0.15)))

                TextField("Name", text: $prefName)
                    .autocapitalization(.none)
                    .autocorrectionDisabled()
                    .padding(8)
                    .background(RoundedRectangle(cornerRadius: 8).stroke(Color.primary.opacity(0.15)))

                TextField("Phone", text: $prefPhone)
                    .autocapitalization(.none)
                    .autocorrectionDisabled()
                    .padding(8)
                    .background(RoundedRectangle(cornerRadius: 8).stroke(Color.primary.opacity(0.15)))

                HStack(spacing: 8) {
                    TextField("Key", text: $customKey)
                        .autocapitalization(.none)
                        .autocorrectionDisabled()
                        .padding(8)
                        .background(RoundedRectangle(cornerRadius: 8).stroke(Color.primary.opacity(0.15)))

                    TextField("Value", text: $customValue)
                        .autocapitalization(.none)
                        .autocorrectionDisabled()
                        .padding(8)
                        .background(RoundedRectangle(cornerRadius: 8).stroke(Color.primary.opacity(0.15)))

                    Button(action: addCustomField) {
                        Text("Add")
                            .padding(.horizontal, 12)
                            .padding(.vertical, 8)
                            .background(Color.accentColor.opacity(0.15))
                            .cornerRadius(8)
                    }
                    .disabled(customKey.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                }

                if !customFields.isEmpty {
                    ForEach(Array(customFields.keys).sorted(), id: \.self) { key in
                        HStack {
                            Text(key)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            Spacer()
                            Text(String(describing: customFields[key]!))
                                .font(.subheadline)
                            Button(action: { removeCustomField(key: key) }) {
                                Image(systemName: "trash")
                                    .foregroundColor(.red)
                            }
                        }
                        .padding(6)
                        .background(RoundedRectangle(cornerRadius: 6).fill(Color.primary.opacity(0.02)))
                    }
                }

                Button(action: applyCustomFields) {
                    Text("Apply Custom Fields")
                        .fontWeight(.semibold)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.accentColor)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
            }

            VStack(spacing: 12) {
                Button("Show Chat") {
                    BDChatSDK.enableLogging()
                    applyFontPreference()
                    BDChatSDK.showChat()
                }

                Button("Clear Chat") {
                    BDChatSDK.clearSession()
                }

                Button("Set light theme") {
                    BDChatSDK.setPreferredTheme(.light)
                }

                Button("Set dark theme") {
                    BDChatSDK.setPreferredTheme(.dark)
                }
                
                StyledPicker(selection: $selectedFont, options: fonts)
            }
            }
            .padding()
        }
        .onChange(of: selectedFont) { _ in
            applyFontPreference()
        }
    }

    private func configureSDK() {
        let trimmedAppId = appId.trimmingCharacters(in: .whitespacesAndNewlines)
        let trimmedBrandId = brandId.trimmingCharacters(in: .whitespacesAndNewlines)
        let trimmedLanguage = languageCode.trimmingCharacters(in: .whitespacesAndNewlines)

        guard !trimmedAppId.isEmpty, !trimmedBrandId.isEmpty else {
            isConfigured = false
            statusMessage = "Please enter both the App ID and Brand ID before configuring."
            return
        }
        BDChatSDK.configure(appKey: trimmedAppId, brandUrl: trimmedBrandId, trimmedLanguage.isEmpty ? nil : trimmedLanguage)
        isConfigured = true
        statusMessage = "SDK configured successfully."
        applyFontPreference()
    }

    private func applyFontPreference() {
        guard isConfigured else { return }
        BDChatSDK.customFontName = selectedFont
    }

    private func applyUserToken() {
        let trimmedToken = userToken.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmedToken.isEmpty {
            BDChatSDK.setUserToken(nil)
            statusMessage = "User token cleared."
        } else {
            BDChatSDK.setUserToken(trimmedToken)
            statusMessage = "User token applied successfully."
        }
    }

    // MARK: - Custom fields helpers
    private func parseValue(_ str: String) -> Any {
        let trimmed = str.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.isEmpty { return "" }
        let lower = trimmed.lowercased()
        if lower == "true" { return true }
        if lower == "false" { return false }
        if let int = Int(trimmed) { return int }
        if let dbl = Double(trimmed) { return dbl }

        if trimmed.first == "[" && trimmed.last == "]" {
            if let data = trimmed.data(using: .utf8) {
                if let obj = try? JSONSerialization.jsonObject(with: data, options: [] ) {
                    return obj
                }
            }
        }

        if trimmed.contains(",") {
            let parts = trimmed.split(separator: ",").map { part -> Any in
                let s = part.trimmingCharacters(in: .whitespacesAndNewlines)
                if let i = Int(s) { return i }
                if let d = Double(s) { return d }
                let l = s.lowercased()
                if l == "true" { return true }
                if l == "false" { return false }
                return s
            }
            return parts
        }

        return trimmed
    }

    private func addCustomField() {
        let key = customKey.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !key.isEmpty else { return }
        customFields[key] = parseValue(customValue)
        customKey = ""
        customValue = ""
    }

    private func removeCustomField(key: String) {
        customFields.removeValue(forKey: key)
    }

    private func applyCustomFields() {
        BDChatSDK.setPrefillFields(email: prefEmail, name: prefName, phoneNo: prefPhone, fields: customFields)
        statusMessage = "Prefill fields applied."
    }

    private func applyTheme() {
        let appbarHex = appbarColor.toHex()
        let accentHex = accentColor.toHex()
        let backgroundHex = backgroundColor.toHex()
        let stickyHex = stickyButtonColor.toHex()
        BDChatSDK.applyTheme(appbarColor: appbarHex, accentColor: accentHex, backgroundColor: backgroundHex, stickyButtonColor: stickyHex)
        statusMessage = "Theme applied."
    }

    @ViewBuilder
    private func configurationField(title: String, text: Binding<String>) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.subheadline)
            TextField(title, text: text)
                .autocapitalization(.none)
                .autocorrectionDisabled()
                .padding(12)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(Color.primary.opacity(0.2), lineWidth: 1)
                )
        }
    }
}

// MARK: - StyledPicker
struct StyledPicker: View {
    @Binding var selection: String
    var options: [String]

    var body: some View {
        Menu {
            ForEach(options, id: \.self) { option in
                Button(action: { selection = option }) {
                    Text(option)
                }
            }
        } label: {
            HStack {
                Text(selection)
                    .foregroundColor(.primary)
                    .font(.system(size: 16))
                    .frame(maxWidth: .infinity, alignment: .leading)
                Image(systemName: "chevron.down")
                    .foregroundColor(.secondary)
            }
            .padding(12)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(Color.primary.opacity(0.2), lineWidth: 1)
            )
        }
    }
}

#Preview {
    ContentView()
}

// MARK: - Color hex helper
extension Color {
    func toHex() -> String {
        let ui = UIColor(self)
        var r: CGFloat = 0
        var g: CGFloat = 0
        var b: CGFloat = 0
        var a: CGFloat = 0
        ui.getRed(&r, green: &g, blue: &b, alpha: &a)
        return String(format: "#%02X%02X%02X", Int(r * 255), Int(g * 255), Int(b * 255))
    }
}

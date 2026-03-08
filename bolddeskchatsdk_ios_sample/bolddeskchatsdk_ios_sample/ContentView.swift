import SwiftUI
import BoldDeskChatSDK

struct ContentView: View {
    let themes = [SDKTheme.light , SDKTheme.dark, SDKTheme.system]
    @State private var selectedTheme = SDKTheme.system
    @State private var appId = ""
    @State private var brandId = ""
    @State private var isConfigured = false
    @State private var statusMessage: String?

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            VStack(alignment: .leading, spacing: 12) {
                Text("SDK Configuration")
                    .font(.headline)

                configurationField(title: "App Key", text: $appId)
                configurationField(title: "Brand URL", text: $brandId)

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
                    .foregroundColor(isConfigured ? .green : .red)
            }

            Divider()

            VStack(spacing: 12) {
                Button("Show Chat") {
                    applyThemePreference()
                    BDChatSDK.showChat()
                }

                Button("Clear Chat") {
                    BDChatSDK.clearSession()
                }

                StyledPicker(selection: $selectedTheme, options: themes, labelProvider: themeDisplayName)
            }
        }
        .padding()
        .onChange(of: selectedTheme) { _ in
            applyThemePreference()
        }
    }

    private func configureSDK() {
        let trimmedAppId = appId.trimmingCharacters(in: .whitespacesAndNewlines)
        let trimmedBrandId = brandId.trimmingCharacters(in: .whitespacesAndNewlines)

        guard !trimmedAppId.isEmpty, !trimmedBrandId.isEmpty else {
            isConfigured = false
            statusMessage = "Please enter both the App ID and Brand ID before configuring."
            return
        }

        BDChatSDK.configure(appKey: trimmedAppId, brandUrl: trimmedBrandId)
        isConfigured = true
        statusMessage = "SDK configured successfully."
        applyThemePreference()
    }

    private func applyThemePreference() {
        guard isConfigured else { return }
        BDChatSDK.setPreferredTheme(selectedTheme)
    }

    private func themeDisplayName(_ theme: SDKTheme) -> String {
        switch theme {
        case SDKTheme.light: return "Light"
        case .dark: return "Dark"
        case .system: return "System"
        @unknown default: return String(describing: theme)
        }
    }

    @ViewBuilder
    private func configurationField(title: String, text: Binding<String>) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.subheadline)
            TextField(title, text: text)
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
struct StyledPicker<T: Hashable>: View {
    @Binding var selection: T
    var options: [T]
    var labelProvider: (T) -> String

    var body: some View {
        Menu {
            ForEach(options, id: \.self) { option in
                Button(action: { selection = option }) {
                    Text(labelProvider(option))
                }
            }
        } label: {
            HStack {
                Text(labelProvider(selection))
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

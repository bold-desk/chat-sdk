import SwiftUI
import SampleSwiftUIFramework

struct ContentView: View {
    let fonts = ["Inter", "Roboto", "Poppins", "Times New Roman", "Open Sans"]
    @State private var selectedFont = "Inter"
    @State private var appId = ""
    @State private var brandId = ""
    @State private var isConfigured = false
    @State private var statusMessage: String?

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            VStack(alignment: .leading, spacing: 12) {
                Text("SDK Configuration")
                    .font(.headline)

                configurationField(title: "App Id", text: $appId)
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
                    applyFontPreference()
                    BDChatSDK.showChat()
                }
                .disabled(!isConfigured)

                Button("Clear Chat") {
                    BDChatSDK.clearChatSession()
                }
                .disabled(!isConfigured)

                Button("Set light theme") {
                    BDChatSDK.setPreferredTheme(.light)
                }
                .disabled(!isConfigured)

                Button("Set dark theme") {
                    BDChatSDK.Theme = .dark
                }
                .disabled(!isConfigured)

                StyledPicker(selection: $selectedFont, options: fonts)
                    .disabled(!isConfigured)
            }
        }
        .padding()
        .onChange(of: selectedFont) { _ in
            applyFontPreference()
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

        BDChatSDK.configure(appToken: trimmedAppId, domainURL: trimmedBrandId)
        isConfigured = true
        statusMessage = "SDK configured successfully."
        applyFontPreference()
    }

    private func applyFontPreference() {
        guard isConfigured else { return }
//        BDChatSDK.customFontName = selectedFont
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

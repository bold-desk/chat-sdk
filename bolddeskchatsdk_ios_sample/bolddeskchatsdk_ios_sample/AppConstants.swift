import SwiftUI

@propertyWrapper
struct Preference {
    let key: String
    let defaultValue: String

    var wrappedValue: String {
        get { UserDefaults.standard.string(forKey: key) ?? defaultValue }
        set { UserDefaults.standard.set(newValue, forKey: key) }
    }
}

struct AppConstants {
    @Preference(key: "appKey", defaultValue: "") static var appKey: String
    @Preference(key: "brandUrl", defaultValue: "") static var brandURL: String
}

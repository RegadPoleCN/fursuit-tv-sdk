# Fursuit.TV SDK iOS 集成指南

本文档说明如何在 iOS 项目中集成 Kotlin Multiplatform 编译的 Fursuit.TV SDK。

## 前置要求

- Xcode 16.0+
- iOS 13.0+
- Swift 5.9+
- CocoaPods 或 Swift Package Manager

## 方法 1: 使用 Swift Package Manager (推荐)

### 步骤 1: 添加 Package 依赖

在 Xcode 中：
1. 打开项目
2. 选择 `File` → `Add Package Dependencies...`
3. 输入仓库 URL: `https://github.com/RegadPoleCN/fursuit-tv-sdk`
4. 选择版本范围 (例如 `0.1.0` 到 `1.0.0`)
5. 点击 `Add Package`

或在 `Package.swift` 中添加：

```swift
dependencies: [
    .package(url: "https://github.com/RegadPoleCN/fursuit-tv-sdk", from: "0.1.0")
]
```

### 步骤 2: 添加依赖到 Target

```swift
// Package.swift
targets: [
    .target(
        name: "YourApp",
        dependencies: [
            .product(name: "FursuitTvSdk", package: "fursuit-tv-sdk")
        ]
    )
]
```

## 方法 2: 使用 CocoaPods

### 步骤 1: 创建 Podfile

```ruby
# Podfile
platform :ios, '13.0'

target 'YourApp' do
  use_frameworks!
  
  pod 'FursuitTvSdk', '~> 0.1'
end
```

### 步骤 2: 安装 Pods

```bash
pod install
```

### 步骤 3: 打开 .xcworkspace

```bash
open YourApp.xcworkspace
```

## 步骤 3: 创建 Swift 包装器

由于 SDK 是 Kotlin 编写的，需要创建 Swift 包装器来使用：

```swift
// UserService.swift
import Foundation
import FursuitTvSdk
import Combine

@MainActor
class UserService: ObservableObject {
    private var sdk: FursuitTvSdk?
    private let clientId: String
    private let clientSecret: String
    
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var userProfile: UserProfile?
    
    init(clientId: String, clientSecret: String) {
        self.clientId = clientId
        self.clientSecret = clientSecret
    }
    
    func initialize() async throws {
        // 使用 Kotlin DSL 初始化 SDK
        // 注意：实际调用方式取决于 Kotlin/Native 的互操作层
        sdk = try await FursuitTvSdk.Companion.create { config in
            config.clientId = self.clientId
            config.clientSecret = self.clientSecret
        }
    }
    
    func loadUser(username: String) async {
        isLoading = true
        errorMessage = nil
        
        do {
            guard let currentSdk = sdk else {
                throw NSError(domain: "SDK", code: -1, userInfo: [NSLocalizedDescriptionKey: "SDK not initialized"])
            }
            
            // 获取用户资料
            let profile = try await currentSdk.user.getUserProfile(username: username)
            
            userProfile = profile
            isLoading = false
        } catch {
            errorMessage = error.localizedDescription
            isLoading = false
        }
    }
    
    func close() {
        sdk?.close()
        sdk = nil
    }
    
    deinit {
        close()
    }
}
```

## 步骤 4: 在 SwiftUI 中使用

```swift
// UserView.swift
import SwiftUI

struct UserView: View {
    @StateObject private var userService = UserService(
        clientId: "vap_xxxxxxxxxxxxxxxx",
        clientSecret: "your-client-secret"
    )
    
    var body: some View {
        VStack(spacing: 20) {
            if userService.isLoading {
                ProgressView("加载中...")
            } else if let error = userService.errorMessage {
                Text("错误：\(error)")
                    .foregroundColor(.red)
            } else if let profile = userService.userProfile {
                VStack {
                    Text("用户：\(profile.displayName)")
                        .font(.title)
                    Text("@\(profile.username)")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                }
            }
            
            Button("加载用户") {
                Task {
                    await userService.loadUser(username: "username")
                }
            }
            .buttonStyle(.borderedProminent)
            .disabled(userService.isLoading)
        }
        .padding()
        .task {
            do {
                try await userService.initialize()
            } catch {
                userService.errorMessage = error.localizedDescription
            }
        }
    }
}
```

## 步骤 5: 在 UIKit 中使用

```swift
// UserViewController.swift
import UIKit

class UserViewController: UIViewController {
    
    private let userService: UserService
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var usernameLabel: UILabel!
    @IBOutlet weak var loadingIndicator: UIActivityIndicatorView!
    
    init(clientId: String, clientSecret: String) {
        self.userService = UserService(clientId: clientId, clientSecret: clientSecret)
        super.init(nibName: "UserViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        Task { @MainActor in
            do {
                try await userService.initialize()
                await loadUser()
            } catch {
                nameLabel.text = "初始化错误：\(error.localizedDescription)"
            }
        }
    }
    
    private func loadUser() async {
        loadingIndicator.startAnimating()
        
        await userService.loadUser(username: "username")
        
        loadingIndicator.stopAnimating()
        
        if let profile = userService.userProfile {
            nameLabel.text = "用户：\(profile.displayName)"
            usernameLabel.text = "@\(profile.username)"
        } else if let error = userService.errorMessage {
            nameLabel.text = "错误：\(error)"
        }
    }
    
    deinit {
        userService.close()
    }
}
```

## 步骤 6: OAuth 支持 (可选)

### 使用 ASWebAuthenticationSession (推荐)

```swift
// OAuthManager.swift
import AuthenticationServices

@MainActor
class OAuthManager: NSObject, ASWebAuthenticationPresentationContextProviding {
    
    static let shared = OAuthManager()
    
    func presentOAuth(
        authorizeUrl: String,
        callbackUrlScheme: String,
        from viewController: UIViewController
    ) async throws -> String {
        
        return try await withCheckedThrowingContinuation { continuation in
            guard let url = URL(string: authorizeUrl) else {
                continuation.resume(throwing: URLError(.badURL))
                return
            }
            
            let session = ASWebAuthenticationSession(
                url: url,
                callbackURLScheme: callbackUrlScheme
            ) { callbackURL, error in
                if let error = error {
                    continuation.resume(throwing: error)
                } else if let url = callbackURL {
                    if let components = URLComponents(url: url, resolvingAgainstBaseURL: true),
                       let code = components.queryItems?.first(where: { $0.name == "code" })?.value {
                        continuation.resume(returning: code)
                    } else {
                        continuation.resume(throwing: OAuthError.invalidCallback)
                    }
                }
            }
            
            session.presentationContextProvider = self
            session.prefersEphemeralWebBrowserSession = false
            session.start()
        }
    }
    
    func presentationAnchor(for session: ASWebAuthenticationSession) -> ASPresentationAnchor {
        return UIApplication.shared.windows.first ?? ASPresentationAnchor()
    }
}

enum OAuthError: LocalizedError {
    case invalidCallback
    case userCancelled
    
    var errorDescription: String? {
        switch self {
        case .invalidCallback: return "无效的回调 URL"
        case .userCancelled: return "用户取消了授权"
        }
    }
}
```

### 配置 Info.plist

```xml
<!-- Info.plist -->
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLSchemes</key>
        <array>
            <string>myapp</string>
        </array>
        <key>CFBundleURLName</key>
        <string>com.example.app</string>
    </dict>
</array>
```

## 完整示例项目结构

```
YourApp/
├── YourApp/
│   ├── AppDelegate.swift
│   ├── SceneDelegate.swift
│   ├── Views/
│   │   └── UserView.swift
│   ├── ViewModels/
│   │   └── UserViewModel.swift
│   ├── Services/
│   │   └── UserService.swift
│   └── Info.plist
├── YourApp.xcodeproj
└── Podfile (如果使用 CocoaPods)
```

## 注意事项

### 1. ATS 配置

确保在 `Info.plist` 中配置 App Transport Security：

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <false/>
    <key>NSExceptionDomains</key>
    <dict>
        <key>open-global.vdsentnet.com</key>
        <dict>
            <key>NSIncludesSubdomains</key>
            <true/>
            <key>NSThirdPartyExceptionAllowsInsecureHTTPLoads</key>
            <false/>
        </dict>
    </dict>
</dict>
```

### 2. 异步编程

所有 API 都是异步的，使用 Swift Concurrency：

```swift
// ✅ 正确：在 Task 或 async 函数中调用
Task {
    let profile = try await sdk.user.getUserProfile(username: "username")
}

// ❌ 错误：不能同步调用
let profile = sdk.user.getUserProfile(username: "username")  // 编译错误
```

### 3. 主线程安全

确保在主线程更新 UI：

```swift
@MainActor
func updateUserUI(profile: UserProfile) {
    self.userProfile = profile
    self.isLoading = false
}
```

### 4. 内存管理

避免循环引用：

```swift
class UserViewModel: ObservableObject {
    weak var delegate: UserViewModelDelegate?  // 使用 weak
}
```

### 5. 错误处理

处理所有可能的异常类型：

```swift
do {
    let profile = try await sdk.user.getUserProfile(username: "username")
} catch let error as NotFoundException {
    print("用户不存在: \(error.localizedDescription)")
} catch let error as AuthenticationException {
    print("认证失败: \(error.localizedDescription)")
} catch let error as TokenExpiredException {
    print("令牌过期，SDK 会自动刷新")
} catch let error as NetworkException {
    print("网络错误: \(error.localizedDescription)")
} catch {
    print("其他错误: \(error.localizedDescription)")
}
```

### 6. Kotlin/Native 互操作注意事项

由于 SDK 是 Kotlin 编译的：
- 类名和方法名会转换为 Swift 风格（驼峰命名）
- 可空类型会映射为 Swift Optional
- 协程 suspend 函数会变为 async 函数
- 具体映射规则请参考生成的 Framework 头文件

## 测试

```swift
// UserViewModelTests.swift
import XCTest
@testable import YourApp

class UserViewModelTests: XCTestCase {
    
    var viewModel: UserViewModel!
    
    override func setUp() {
        viewModel = UserViewModel(
            clientId: "test_app_id",
            clientSecret: "test_secret"
        )
    }
    
    func testLoadUser() async throws {
        await viewModel.loadUser(username: "testuser")
        
        XCTAssertNotNil(viewModel.userProfile)
        XCTAssertFalse(viewModel.isLoading)
    }
    
    override func tearDown() {
        viewModel = nil
    }
}
```

## 更多信息

- [认证文档](../../docs/authentication.md)
- [JVM 示例](../jvm/README.md) - 包含完整的 API 调用示例
- [API 参考](../../docs/API_REFERENCE.md)
- [Swift Concurrency](https://docs.swift.org/swift-book/LanguageGuide/Concurrency.html)

## 许可证

MIT License

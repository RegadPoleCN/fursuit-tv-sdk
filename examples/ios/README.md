# Fursuit.TV SDK iOS 集成指南

本文档说明如何在 iOS 项目中集成 Fursuit.TV SDK。

## 前置要求

- Xcode 14.0+
- iOS 13.0+
- Swift 5.7+
- CocoaPods 或 Swift Package Manager

## 方法 1: 使用 Swift Package Manager (推荐)

### 步骤 1: 添加 Package 依赖

在 Xcode 中：
1. 打开项目
2. 选择 `File` → `Add Package Dependencies...`
3. 输入仓库 URL: `https://github.com/RegadPoleCN/fursuit-tv-sdk`
4. 选择版本范围 (例如 `1.0.0` 到 `2.0.0`)
5. 点击 `Add Package`

或在 `Package.swift` 中添加：

```swift
dependencies: [
    .package(url: "https://github.com/RegadPoleCN/fursuit-tv-sdk", from: "1.0.0")
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
  
  pod 'FursuitTvSdk', '~> 1.0'
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

由于 SDK 是 Kotlin 编写的，需要创建 Swift 包装器：

```swift
// UserService.swift
import Foundation
import FursuitTvSdk
import Combine

class UserService: ObservableObject {
    private let sdk: FursuitTvSdk
    private let appId: String
    private let appSecret: String
    
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var userProfile: UserProfile?
    
    init(appId: String, appSecret: String) {
        self.appId = appId
        self.appSecret = appSecret
        self.sdk = FursuitTvSdk(appId: appId, appSecret: appSecret)
    }
    
    func loadUser(username: String) async {
        await MainActor.run {
            isLoading = true
            errorMessage = nil
        }
        
        do {
            // 确保令牌有效
            try await sdk.auth.getValidAccessToken(
                appId: appId,
                appSecret: appSecret
            )
            
            // 获取用户资料
            let profile = try await sdk.user.getUserProfile(username: username)
            
            await MainActor.run {
                self.userProfile = profile
                self.isLoading = false
            }
        } catch {
            await MainActor.run {
                self.errorMessage = error.localizedDescription
                self.isLoading = false
            }
        }
    }
    
    func close() {
        sdk.close()
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
        appId: "vap_xxxxxxxxxxxxxxxx",
        appSecret: "your-app-secret"
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
    
    init(appId: String, appSecret: String) {
        self.userService = UserService(appId: appId, appSecret: appSecret)
        super.init(nibName: "UserViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        loadUser()
    }
    
    private func loadUser() {
        loadingIndicator.startAnimating()
        
        Task {
            await userService.loadUser(username: "username")
            
            await MainActor.run {
                loadingIndicator.stopAnimating()
                
                if let profile = userService.userProfile {
                    nameLabel.text = "用户：\(profile.displayName)"
                    usernameLabel.text = "@\(profile.username)"
                } else if let error = userService.errorMessage {
                    nameLabel.text = "错误：\(error)"
                }
            }
        }
    }
    
    deinit {
        userService.close()
    }
}
```

## 步骤 6: OAuth 支持

### 使用 ASWebAuthenticationSession (推荐)

```swift
// OAuthManager.swift
import AuthenticationServices

class OAuthManager {
    
    static let shared = OAuthManager()
    
    func presentOAuth(
        appId: String,
        callbackUrlScheme: String,
        from viewController: UIViewController
    ) async throws -> String {
        
        return try await withCheckedThrowingContinuation { continuation in
            let authorizeUrl = "https://..." // SDK 生成的授权 URL
            
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
                    // 从 URL 中提取授权码
                    if let components = URLComponents(url: url, resolvingAgainstBaseURL: true),
                       let code = components.queryItems?.first(where: { $0.name == "code" })?.value {
                        continuation.resume(returning: code)
                    } else {
                        continuation.resume(throwing: OAuthError.invalidCallback)
                    }
                }
            }
            
            // iOS 13+ 需要设置 presentationContextProvider
            if #available(iOS 13.0, *) {
                session.presentationContextProvider = self
            }
            
            session.start()
        }
    }
}

// 实现 presentationContextProvider
extension OAuthManager: ASWebAuthenticationPresentationContextProviding {
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

## 步骤 7: 使用 Combine 响应式编程

```swift
// UserViewModel.swift
import Foundation
import Combine
import FursuitTvSdk

class UserViewModel: ObservableObject {
    private let sdk: FursuitTvSdk
    private var cancellables = Set<AnyCancellable>()
    
    @Published var userProfile: UserProfile?
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    init(appId: String, appSecret: String) {
        self.sdk = FursuitTvSdk(appId: appId, appSecret: appSecret)
        setupBindings()
    }
    
    private func setupBindings() {
        // 响应式加载
    }
    
    func loadUser(username: String) {
        isLoading = true
        
        Task {
            do {
                try await sdk.auth.getValidAccessToken(
                    appId: appId,
                    appSecret: appSecret
                )
                
                let profile = try await sdk.user.getUserProfile(username: username)
                
                await MainActor.run {
                    self.userProfile = profile
                    self.isLoading = false
                }
            } catch {
                await MainActor.run {
                    self.errorMessage = error.localizedDescription
                    self.isLoading = false
                }
            }
        }
    }
    
    deinit {
        sdk.close()
    }
}
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

### 2. 后台任务

使用 Background Tasks 处理长时间运行的任务：

```swift
import BackgroundTasks

func scheduleBackgroundRefresh() {
    let request = BGAppRefreshTaskRequest(identifier: "com.example.app.refresh")
    request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60)
    
    try? BGTaskScheduler.shared.submit(request)
}
```

### 3. 错误处理

```swift
do {
    let profile = try await sdk.user.getUserProfile(username: "username")
} catch let error as NotFoundException {
    print("用户不存在")
} catch let error as AuthenticationException {
    print("认证失败")
} catch {
    print("其他错误：\(error.localizedDescription)")
}
```

### 4. 内存管理

使用 `weak` 或 `unowned` 避免循环引用：

```swift
class UserViewModel: ObservableObject {
    weak var delegate: UserViewModelDelegate?
    
    // ...
}
```

### 5. 线程安全

确保在主线程更新 UI：

```swift
await MainActor.run {
    // 更新 UI
    self.userProfile = profile
}
```

## 测试

```swift
// UserViewModelTests.swift
import XCTest
@testable import YourApp

class UserViewModelTests: XCTestCase {
    
    var viewModel: UserViewModel!
    
    override func setUp() {
        viewModel = UserViewModel(
            appId: "test_app_id",
            appSecret: "test_secret"
        )
    }
    
    func testLoadUser() async {
        await viewModel.loadUser(username: "username")
        
        XCTAssertNotNil(viewModel.userProfile)
        XCTAssertFalse(viewModel.isLoading)
    }
    
    override func tearDown() {
        viewModel = nil
    }
}
```

## 更多信息

- [开发者指南](../../docs/DEVELOPER_GUIDE.md)
- [平台指南](../../docs/PLATFORM_GUIDE.md)
- [Swift Concurrency](https://docs.swift.org/swift-book/LanguageGuide/Concurrency.html)

## 许可证

MIT License

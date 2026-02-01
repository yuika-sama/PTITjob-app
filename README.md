# 📱 PTIT Job - Android Application

<div align="center">

![Android](https://img.shields.io/badge/Android-10+-3DDC84?style=flat-square&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2-7F52FF?style=flat-square&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-1.7-4285F4?style=flat-square&logo=jetpackcompose)
![Min SDK](https://img.shields.io/badge/Min_SDK-29-green?style=flat-square)
![Target SDK](https://img.shields.io/badge/Target_SDK-36-blue?style=flat-square)

**Ứng dụng Android Native cho hệ thống tuyển dụng PTIT Job**

</div>

---

## 📖 Tổng quan

**PTIT Job Android** là ứng dụng mobile native được xây dựng hoàn toàn bằng **Kotlin** và **Jetpack Compose**, tuân theo **Clean Architecture** và **MVVM pattern**. Ứng dụng cung cấp trải nghiệm tìm kiếm việc làm mượt mà, hiện đại với UI Material Design 3.

### ✨ Đặc điểm nổi bật

- 🎨 **100% Jetpack Compose** - Modern declarative UI
- 🏗️ **Clean Architecture** - Data, Domain, UI layers
- 💉 **Dependency Injection** - Hilt/Dagger
- 🔄 **Reactive Programming** - Kotlin Coroutines & Flow
- 💾 **Local Caching** - Room Database
- 🌐 **Networking** - Retrofit + OkHttp
- 📄 **Pagination** - Paging 3 library
- 🖼️ **Image Loading** - Coil
- 🧭 **Navigation** - Jetpack Navigation Compose
- 🎯 **Material Design 3** - Latest design system

## 🎯 Tính năng

### 👤 Người dùng

#### Authentication
- ✅ Đăng ký tài khoản
- ✅ Đăng nhập (JWT Token)
- ✅ Quên mật khẩu
- ✅ Persistent session với DataStore
- ✅ Auto logout khi token expired

#### Tìm kiếm việc làm
- 🔍 Tìm kiếm công việc với filters
- 📋 Danh sách công việc với pagination
- 💼 Chi tiết công việc
- 🏢 Xem thông tin công ty
- ⭐ Công việc nổi bật
- 📌 Lưu công việc yêu thích (Coming soon)

#### Ứng tuyển
- 📄 Upload CV (PDF)
- ✍️ Viết cover letter
- 📊 Theo dõi trạng thái đơn ứng tuyển
- 📜 Lịch sử ứng tuyển

#### Hồ sơ cá nhân
- 👤 Quản lý thông tin cá nhân
- 📸 Upload avatar
- 📧 Cập nhật thông tin liên hệ

#### AI Features (Planned)
- 🤖 Đánh giá CV với AI
- 💬 Mô phỏng phỏng vấn
- 🎯 Gợi ý công việc phù hợp

### 🎨 UI/UX Features
- 🌓 Dark/Light theme (System default)
- ♿ Accessibility support
- 📱 Responsive layouts
- 🎭 Smooth animations & transitions
- 🔔 Push notifications (Planned)

## 🛠 Tech Stack

### Core Technologies

```kotlin
android {
    compileSdk = 36
    minSdk = 29        // Android 10+
    targetSdk = 36     // Android 14+
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
}
```

### Architecture & Patterns

- **Architecture**: Clean Architecture (Data, Domain, UI)
- **Design Pattern**: MVVM (Model-View-ViewModel)
- **UI Framework**: Jetpack Compose
- **Language**: Kotlin 2.2.21
- **Build System**: Gradle Kotlin DSL

### Dependencies

| Library | Version | Mục đích |
|---------|---------|----------|
| **Jetpack Compose** | BOM 2025.10.01 | UI toolkit |
| **Material3** | 1.5.4 | Material Design 3 |
| **Hilt** | 2.57.2 | Dependency Injection |
| **Navigation Compose** | 2.9.5 | Navigation |
| **Retrofit** | 3.0.0 | HTTP client |
| **OkHttp** | 5.2.1 | HTTP engine |
| **Room** | 2.8.3 | Local database |
| **Paging 3** | 3.3.6 | Pagination |
| **Coil** | 2.7.0 | Image loading |
| **Coroutines** | 1.10.2 | Async operations |
| **DataStore** | 1.1.7 | Key-value storage |
| **Sceneform** | 1.23.0 | 3D/AR rendering |

### Jetpack Components

- ✅ Compose UI
- ✅ ViewModel & LiveData
- ✅ Navigation
- ✅ Room Database
- ✅ Paging 3
- ✅ DataStore Preferences
- ✅ Lifecycle
- ✅ Hilt

## 📁 Cấu trúc dự án

```
PTITjob-app/
├── 📄 build.gradle.kts          # Project-level build config
├── 📄 settings.gradle.kts       # Settings & modules
├── 📄 local.properties          # Local config (API URLs)
├── 📄 gradle.properties         # Gradle properties
│
├── 📂 gradle/
│   └── libs.versions.toml      # Version catalog
│
└── 📂 app/
    ├── build.gradle.kts        # App-level build config
    ├── proguard-rules.pro      # ProGuard rules
    │
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── res/            # Resources (layouts, drawables, strings)
        │   │
        │   └── java/com/example/ptitjob/
        │       │
        │       ├── 📱 MainActivity.kt      # Entry point activity
        │       ├── 📱 PtitJobApp.kt        # Application class (Hilt)
        │       │
        │       ├── 📦 data/                # Data Layer
        │       │   ├── api/                # API interfaces
        │       │   │   ├── AuthApi.kt
        │       │   │   ├── JobApi.kt
        │       │   │   ├── CompanyApi.kt
        │       │   │   ├── ApplicationApi.kt
        │       │   │   └── UserApi.kt
        │       │   │
        │       │   ├── model/              # Data models & DTOs
        │       │   │   ├── User.kt
        │       │   │   ├── Job.kt
        │       │   │   ├── Company.kt
        │       │   │   ├── Application.kt
        │       │   │   ├── ApiResponse.kt
        │       │   │   └── AuthRequest.kt
        │       │   │
        │       │   └── repository/         # Repository implementations
        │       │       ├── AuthRepository.kt
        │       │       ├── JobRepository.kt
        │       │       ├── CompanyRepository.kt
        │       │       └── ApplicationRepository.kt
        │       │
        │       ├── 💉 di/                  # Dependency Injection
        │       │   ├── AppModule.kt        # App-wide dependencies
        │       │   ├── NetworkModule.kt    # Retrofit, OkHttp
        │       │   └── DatabaseModule.kt   # Room database
        │       │
        │       └── 🎨 ui/                  # UI Layer
        │           │
        │           ├── theme/              # Compose theme
        │           │   ├── Color.kt
        │           │   ├── Theme.kt
        │           │   ├── Type.kt
        │           │   └── Shape.kt
        │           │
        │           ├── component/          # Reusable UI components
        │           │   ├── PTITAppContainer.kt
        │           │   ├── TopBar.kt
        │           │   ├── BottomBar.kt
        │           │   ├── JobCard.kt
        │           │   ├── CompanyCard.kt
        │           │   ├── LoadingIndicator.kt
        │           │   └── ErrorMessage.kt
        │           │
        │           ├── navigation/         # Navigation setup
        │           │   ├── CandidateNavGraph.kt
        │           │   ├── NavRoutes.kt
        │           │   └── NavHost.kt
        │           │
        │           ├── screen/             # Screens (Composables)
        │           │   ├── auth/
        │           │   │   ├── LoginScreen.kt
        │           │   │   ├── RegisterScreen.kt
        │           │   │   └── ForgotPasswordScreen.kt
        │           │   │
        │           │   ├── home/
        │           │   │   ├── HomeScreen.kt
        │           │   │   └── DashboardScreen.kt
        │           │   │
        │           │   ├── job/
        │           │   │   ├── JobListScreen.kt
        │           │   │   ├── JobDetailScreen.kt
        │           │   │   └── JobSearchScreen.kt
        │           │   │
        │           │   ├── company/
        │           │   │   ├── CompanyListScreen.kt
        │           │   │   └── CompanyDetailScreen.kt
        │           │   │
        │           │   ├── application/
        │           │   │   ├── ApplicationListScreen.kt
        │           │   │   ├── ApplyJobScreen.kt
        │           │   │   └── ApplicationDetailScreen.kt
        │           │   │
        │           │   └── profile/
        │           │       ├── ProfileScreen.kt
        │           │       └── EditProfileScreen.kt
        │           │
        │           └── viewmodel/          # ViewModels
        │               ├── AuthViewModel.kt
        │               ├── JobViewModel.kt
        │               ├── CompanyViewModel.kt
        │               ├── ApplicationViewModel.kt
        │               └── ProfileViewModel.kt
        │
        ├── androidTest/                    # Instrumented tests
        │   └── java/com/example/ptitjob/
        │
        └── test/                           # Unit tests
            └── java/com/example/ptitjob/
```

## 🚀 Cài đặt và chạy

### Yêu cầu hệ thống

- **Android Studio**: Ladybug (2024.2.1) hoặc mới hơn
- **JDK**: 17 (bundled with Android Studio)
- **Android SDK**: API 29+ (Android 10+)
- **Gradle**: 8.13 (wrapper included)
- **Kotlin**: 2.2.21

### Bước 1: Clone & Open Project

```bash
# Clone repository
git clone <repository-url>
cd PTITjob-app

# Mở bằng Android Studio
# File -> Open -> Chọn thư mục PTITjob-app
```

### Bước 2: Cấu hình API URLs

Tạo/sửa file `local.properties` trong thư mục root:

```properties
## This file must *NOT* be checked into Version Control Systems
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk

# API Configuration
API_URL=http://10.0.2.2:5000/api/
AI_API_URL=http://10.0.2.2:8000/api/v1/
```

**Lưu ý về API URLs:**
- `10.0.2.2` = localhost của máy host khi chạy trên **Android Emulator**
- Nếu chạy trên **thiết bị thật**, thay bằng IP thực của máy: `http://192.168.x.x:5000/api/`
- Đảm bảo backend đang chạy trước khi test app

### Bước 3: Sync Project

```bash
# Trong Android Studio:
# File -> Sync Project with Gradle Files

# Hoặc qua command line:
./gradlew --refresh-dependencies
```

### Bước 4: Chạy Backend Services

Trước khi chạy app, đảm bảo backend đang chạy:

```bash
# Terminal 1: Node.js Backend
cd ../PTIT-Job/server/nodeServer
bun run dev

# Terminal 2: Python AI Service (nếu cần)
cd ../PTIT-Job/server/pyAI
python -m uvicorn main:app --reload --port 8000
```

### Bước 5: Chạy App

#### Trên Emulator

```bash
# Tạo Android Virtual Device (AVD) trong Android Studio:
# Tools -> Device Manager -> Create Device
# Chọn: Pixel 6 Pro, API 34 (Android 14)

# Run app:
# Click nút Run (▶️) hoặc Shift+F10
```

#### Trên Thiết bị thật

```bash
# 1. Bật Developer Options & USB Debugging trên điện thoại
# 2. Kết nối USB
# 3. Chọn device trong Android Studio
# 4. Click Run
```

#### Build APK

```bash
# Debug APK
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Release APK (signed)
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

## 🏗️ Architecture

### Clean Architecture Layers

```
┌─────────────────────────────────────┐
│         Presentation (UI)           │
│  ┌──────────────────────────────┐  │
│  │  Composables & ViewModels    │  │
│  └──────────────────────────────┘  │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│       Domain (Business Logic)        │
│  ┌──────────────────────────────┐  │
│  │    Use Cases (Optional)      │  │
│  └──────────────────────────────┘  │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│          Data Layer                  │
│  ┌──────────────────────────────┐  │
│  │  Repository Implementations  │  │
│  │  API Services & Room DB      │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘
```

### MVVM Pattern

```kotlin
// ViewModel
@HiltViewModel
class JobViewModel @Inject constructor(
    private val repository: JobRepository
) : ViewModel() {
    
    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs.asStateFlow()
    
    fun loadJobs() {
        viewModelScope.launch {
            repository.getJobs().collect { result ->
                _jobs.value = result
            }
        }
    }
}

// Screen (Composable)
@Composable
fun JobListScreen(
    viewModel: JobViewModel = hiltViewModel()
) {
    val jobs by viewModel.jobs.collectAsState()
    
    LazyColumn {
        items(jobs) { job ->
            JobCard(job = job)
        }
    }
}
```

### Dependency Injection

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideJobApi(retrofit: Retrofit): JobApi {
        return retrofit.create(JobApi::class.java)
    }
}
```

## 🌐 Networking

### API Configuration

```kotlin
// BuildConfig (auto-generated)
BuildConfig.API_URL = "http://10.0.2.2:5000/api/"
BuildConfig.AI_API_URL = "http://10.0.2.2:8000/api/v1/"
```

### Retrofit Setup

```kotlin
interface JobApi {
    @GET("jobs")
    suspend fun getJobs(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ApiResponse<List<Job>>
    
    @GET("jobs/{id}")
    suspend fun getJobById(
        @Path("id") id: Long
    ): ApiResponse<Job>
    
    @POST("applications")
    suspend fun applyJob(
        @Header("Authorization") token: String,
        @Body request: ApplyJobRequest
    ): ApiResponse<Application>
}
```

### Authentication

```kotlin
// JWT Token Interceptor
class AuthInterceptor @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            dataStore.data.first()[TOKEN_KEY]
        }
        
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
            
        return chain.proceed(request)
    }
}
```

## 💾 Local Storage

### DataStore (Preferences)

```kotlin
// Save auth token
suspend fun saveToken(token: String) {
    dataStore.edit { preferences ->
        preferences[TOKEN_KEY] = token
    }
}

// Read auth token
val token: Flow<String?> = dataStore.data.map { preferences ->
    preferences[TOKEN_KEY]
}
```

### Room Database (Caching)

```kotlin
@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val company: String,
    val location: String,
    val salary: String
)

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs")
    fun getAllJobs(): Flow<List<JobEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobEntity>)
}
```

## 🎨 UI/UX Guidelines

### Material Design 3

```kotlin
// Theme.kt
@Composable
fun PtitjobTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = PrimaryColor,
            secondary = SecondaryColor,
            // ...
        )
    } else {
        lightColorScheme(
            primary = PrimaryColor,
            secondary = SecondaryColor,
            // ...
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

### Responsive Layouts

```kotlin
@Composable
fun ResponsiveLayout(
    modifier: Modifier = Modifier
) {
    BoxWithConstraints {
        when {
            maxWidth < 600.dp -> CompactLayout()
            maxWidth < 840.dp -> MediumLayout()
            else -> ExpandedLayout()
        }
    }
}
```

## 🧪 Testing

### Unit Tests

```bash
# Run unit tests
./gradlew test

# With coverage
./gradlew testDebugUnitTest
```

### Instrumented Tests

```bash
# Run on connected device/emulator
./gradlew connectedAndroidTest
```

### Example Test

```kotlin
@Test
fun `login with valid credentials returns success`() = runTest {
    // Given
    val email = "test@example.com"
    val password = "password123"
    
    // When
    val result = authRepository.login(email, password)
    
    // Then
    assertTrue(result.isSuccess)
    assertNotNull(result.data?.accessToken)
}
```

## 📦 Build & Release

### Debug Build

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

```bash
# 1. Tạo keystore (lần đầu)
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias

# 2. Cấu hình signing trong build.gradle.kts
android {
    signingConfigs {
        create("release") {
            storeFile = file("my-release-key.jks")
            storePassword = "your-store-password"
            keyAlias = "my-key-alias"
            keyPassword = "your-key-password"
        }
    }
}

# 3. Build
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### App Bundle (for Google Play)

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

## 🔧 Configuration

### ProGuard Rules

```proguard
# Keep models for Gson
-keep class com.example.ptitjob.data.model.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes Annotation
-keep class retrofit2.** { *; }

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
```

### Network Security Config

```xml
<!-- res/xml/network_security_config.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>
```

## 🐛 Troubleshooting

### Lỗi kết nối API

```kotlin
// Check API URL trong BuildConfig
Log.d("API", "Base URL: ${BuildConfig.API_URL}")

// Verify backend đang chạy
curl http://localhost:5000/api/test-db
```

### Lỗi Gradle Sync

```bash
# Clean & Rebuild
./gradlew clean
./gradlew build

# Invalidate caches trong Android Studio
File -> Invalidate Caches / Restart
```

### Lỗi Hilt Injection

```kotlin
// Ensure Application class có @HiltAndroidApp
@HiltAndroidApp
class PtitJobApp : Application()

// Ensure MainActivity có @AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

### Emulator không kết nối được localhost

```bash
# Sử dụng 10.0.2.2 thay vì localhost
API_URL=http://10.0.2.2:5000/api/

# Hoặc test bằng adb reverse
adb reverse tcp:5000 tcp:5000
```

## 📱 Screenshots

### Light Theme
*(Thêm ảnh chụp màn hình ở đây)*

### Dark Theme
*(Thêm ảnh chụp màn hình ở đây)*

## 🚀 Roadmap

- [ ] Implement dark theme toggle
- [ ] Add push notifications
- [ ] Offline mode with Room caching
- [ ] AI CV evaluation integration
- [ ] Interview simulation feature
- [ ] Chat between employer and candidate
- [ ] Video call integration
- [ ] Job recommendations ML model
- [ ] Multi-language support (Vietnamese/English)
- [ ] Accessibility improvements
- [ ] Widget for home screen
- [ ] Wear OS companion app

## 🤝 Contributing

1. Fork the project
2. Create feature branch: `git checkout -b feature/NewFeature`
3. Commit changes: `git commit -m 'Add NewFeature'`
4. Push to branch: `git push origin feature/NewFeature`
5. Submit Pull Request

### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable/function names
- Add KDoc comments for public APIs
- Keep functions small and focused
- Use dependency injection

## 📚 Resources

### Official Documentation
- [Android Developers](https://developer.android.com)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs)
- [Material Design 3](https://m3.material.io)

### Libraries
- [Hilt](https://dagger.dev/hilt/)
- [Retrofit](https://square.github.io/retrofit/)
- [Room](https://developer.android.com/training/data-storage/room)
- [Coil](https://coil-kt.github.io/coil/)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

**Android Development Team**
- UI/UX Design
- Clean Architecture Implementation
- Jetpack Compose Development
- Backend Integration

## 📧 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/ptitjob-app/issues)
- **Email**: support@ptitjob.com
- **Documentation**: [Wiki](https://github.com/yourusername/ptitjob-app/wiki)

---

<div align="center">

**Built with 💙 using Jetpack Compose & Kotlin**

Made with ❤️ by PTIT Students

⭐ Star this repo if you find it helpful!

[⬆ Back to top](#-ptit-job---android-application)

</div>

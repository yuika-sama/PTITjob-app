import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-parcelize")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

fun resolveBaseUrl(key: String, defaultValue: String): String {
    val raw = localProperties.getProperty(key)?.takeIf { it.isNotBlank() } ?: defaultValue
    return if (raw.endsWith("/")) raw else "$raw/"
}

val primaryApiUrl = resolveBaseUrl("API_URL", "http://10.0.2.2:5000/api/")
val aiApiUrl = resolveBaseUrl("AI_API_URL", "http://10.0.2.2:8000/api/")

android {
    namespace = "com.example.ptitjob"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ptitjob"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Base API URL for Retrofit (10.0.2.2 points to host machine from Android emulator)
    buildConfigField("String", "API_URL", "\"$primaryApiUrl\"")
    buildConfigField("String", "AI_API_URL", "\"$aiApiUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        @Suppress("DEPRECATION")
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig= true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation ("androidx.navigation:navigation-compose:2.9.5")

    implementation ("com.google.dagger:hilt-android:2.57.2")
    ksp ("com.google.dagger:hilt-compiler:2.57.2")
    implementation ("androidx.hilt:hilt-navigation-compose:1.3.0")

    implementation( "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    // Retrofit + Gson
    implementation ("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    implementation ("com.squareup.okhttp3:okhttp:5.2.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation ("io.coil-kt:coil-compose:2.7.0")

    implementation ("androidx.paging:paging-runtime:3.3.6")
    implementation ("androidx.paging:paging-compose:3.3.6")

    implementation ("androidx.room:room-runtime:2.8.3")
    implementation("androidx.room:room-ktx:2.8.3")
    ksp ("androidx.room:room-compiler:2.8.3")

    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.30.1")

    implementation("androidx.datastore:datastore-preferences:1.1.7")

    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    implementation("androidx.graphics:graphics-shapes:1.1.0")

    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
}
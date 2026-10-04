
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.helakuru.promax"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.helakuru.promax"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0 Pro Max"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures { viewBinding = true }
}
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.mlkit:translate:17.0.2")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}

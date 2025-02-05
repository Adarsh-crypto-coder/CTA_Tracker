plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ctatracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ctatracker"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.ads)
    implementation (libs.swiperefreshlayout)
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation ("com.google.android.material:material:1.9.0")
//    implementation 'androidx.core:core-splashscreen:1.0.1'
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
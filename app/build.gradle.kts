plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.kulipai.qrwallet"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kulipai.qrwallet"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    flavorDimensions += "abi"

    productFlavors {
        create("armv7") {
            dimension = "abi"
            ndk {
                abiFilters.add("armeabi-v7a")
            }
        }
        create("arm64") {
            dimension = "abi"

            ndk {
                abiFilters.add("arm64-v8a")
            }
        }
        create("x86") {
            dimension = "abi"

            ndk {
                abiFilters.add("x86")
            }
        }
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
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
    implementation(libs.ui.graphics)
    implementation(libs.material3)
    implementation(libs.androidx.foundation)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)



    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.core)
    ksp(libs.compose.destinations.ksp)

    implementation(libs.androidx.material3.android)

    implementation(libs.qrose)
    implementation(libs.qrose.oned)

    implementation(libs.androidx.material.icons.extended)

    implementation(libs.colorpicker)

    implementation(libs.scanner)

    implementation(libs.accompanist.permissions)

}
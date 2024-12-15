plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    // Google Services plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mymoviejournal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mymoviejournal"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "TMDB_API_KEY", "\"${project.findProperty("TMDB_API_KEY") ?: ""}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true // Beholder fra kode 2
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,*.kotlin_module}"
        }
    }

    configurations.all {
        resolutionStrategy {
            // Force consistent version of androidx.appcompat
            force("androidx.appcompat:appcompat:1.4.1")
            force("androidx.appcompat:appcompat-resources:1.4.1")
        }
    }

dependencies {
    // AndroidX and Jetpack Compose
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui:1.5.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")
    implementation("androidx.compose.material3:material3:1.1.1")
    implementation("androidx.compose.material:material:1.5.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Firebase SDKs
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx") // Tilføjet fra kode 2

    // Retrofit for HTTP Requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coil for Image Loading
    implementation("io.coil-kt:coil-compose:2.0.0")

    // Accompanist Libraries
    implementation("com.google.accompanist:accompanist-permissions:0.30.1")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.30.1")

    // Google Maps and Places SDK
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.maps.android:maps-ktx:3.3.0")
    implementation("com.google.android.libraries.places:places:2.6.0")
    implementation("com.google.maps.android:maps-compose:2.13.0")

    // Compose Lifecycle ViewModel Integration
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.1")
}

    // Apply the Google Services plugin
    apply(plugin = "com.google.gms.google-services")
}


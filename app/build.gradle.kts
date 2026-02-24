import java.util.Properties

// 1. Load the local.properties file securely
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    // Add the Google services Gradle plugin for Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.finalprojectandroiddev2"
    compileSdk = 36

    // 2. Enable BuildConfig generation for our environment variables
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.finalprojectandroiddev2"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 3. Inject the keys into BuildConfig
        buildConfigField("String", "TMDB_READ_ACCESS_TOKEN", "\"${localProperties.getProperty("TMDB_READ_ACCESS_TOKEN")}\"")
        buildConfigField("String", "TMDB_API_KEY", "\"${localProperties.getProperty("TMDB_API_KEY")}\"")

        // Inject Firebase Route URL
        buildConfigField("String", "FB_ROUTE_INSTANCE_URL", "\"${localProperties.getProperty("FB_ROUTE_INSTANCE_URL")}\"")
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
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // 4. Standard Retrofit library
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson converter para automatic na mag-map yung JSON to Java Objects (Unit 2 vibes)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp (usually dependency na 'to ni Retrofit, but good to have explicit)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // 5. Firebase Cloud Backend Integration
    implementation(platform("com.google.firebase:firebase-bom:34.9.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")

    // 6. Glide — image loading for movie posters
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // 7. Lottie — JSON-based animations (confetti on match screen)
    implementation("com.airbnb.android:lottie:6.4.1")
}
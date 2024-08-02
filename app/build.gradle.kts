plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.kotlinaprendiz"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kotlinaprendiz"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")
    implementation ("com.google.android.material:material:1.8.0")


    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth:22.1.0")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database:20.2.2")

    // Firebase Core (opcional, para Analytics u otras funcionalidades)
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")

    // Firebase BOM (para gestionar versiones de Firebase de forma coherente)
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation(libs.firebase.storage.ktx)

    // Librerías de testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.androidcodr.movieapp_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.androidcodr.movieapp_project"
        minSdk = 24
        targetSdk = 33
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

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //ViewModel
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    //Recyclerview
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0-alpha05")
    implementation("androidx.cardview:cardview:1.0.0")

    //Retrofit
      implementation("com.squareup.retrofit2:retrofit:2.5.0")
      implementation("com.squareup.retrofit2:converter-gson:2.5.0")
      implementation("com.squareup.retrofit2:adapter-rxjava2:2.5.0")

    //Gson
    implementation("com.google.code.gson:gson:2.8.5")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0") // Use the latest version available
    implementation ("androidx.core:core-ktx:1.7.0") // Use the latest version available

    //Paging
    implementation("androidx.paging:paging-runtime:2.1.0")

    //Rx
    implementation("io.reactivex.rxjava2:rxjava:2.2.7")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
}
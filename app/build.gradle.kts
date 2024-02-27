plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")


}

android {
    namespace = "com.card.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.card.myapplication"
        minSdk = 26
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

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(project(":lp_server"))
    implementation("androidx.privacysandbox.tools:tools-core:1.0.0-alpha07")
    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.github.liujingxing.rxhttp:rxhttp:3.2.4")
    kapt("com.github.liujingxing.rxhttp:rxhttp-compiler:3.2.4")

    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
}
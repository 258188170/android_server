import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.scope.ProjectInfo.Companion.getBaseName
import com.android.tools.r8.internal.LI
import org.jetbrains.kotlin.cli.jvm.main
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}



android {
    namespace = "com.card.lp_server"
    compileSdk = 34

    defaultConfig {
//        applicationId = "com.card.lp_server"
        minSdk = 26
        targetSdk = 34
//        versionCode = 1
//        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = true
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
    libraryVariants.all {
        outputs.all {
            val output = this as BaseVariantOutputImpl
            output.outputFileName = output.outputFileName
        }
    }


//    sourceSets.getByName("main") {
//        // Changes the directory for Java sources. The default directory is
//        // 'src/main/java'.
//        java.setSrcDirs(listOf("other/java"))
//        jniLibs.srcDirs("libs")
//        // If you list multiple directories, Gradle uses all of them to collect
//        // sources. Because Gradle gives these directories equal priority, if
//        // you define the same resource in more than one directory, you receive an
//        // error when merging resources. The default directory is 'src/main/res'.
//        res.setSrcDirs(listOf("other/res1", "other/res2"))
//
//    }
}


dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    api("org.nanohttpd:nanohttpd:2.3.1")
    api("com.blankj:utilcodex:1.31.1")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    api(files("libs/LPEncode.jar"))
    implementation(files("libs/core-3.0.1.jar"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //room
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:$room_version")
    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")
    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")

    api("com.alibaba:fastjson:2.0.28")
}
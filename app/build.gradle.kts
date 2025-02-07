plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.rentmycar"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rentmycar"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }


    packaging {
        resources {
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
        }
    }
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.hilt.android)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.play.services.location)
    implementation(libs.converter.kotlinx.serialization.vlatestversion)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation(libs.kotlin.mockito.kotlin)
    implementation(libs.androidx.navigation.testing)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.ui.test.junit4.android)
    implementation(libs.google.truth)
    implementation(libs.mockk.android)
    implementation(libs.hilt.android.testing)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.junit.jupiter)
    kapt(libs.hilt.android.compiler)
    androidTestImplementation(libs.androidx.rules)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.squareup.javapoet)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
//
//    implementation("androidx.compose.ui:ui:1.5.1")
//    implementation("androidx.compose.material:material:1.5.1")
//    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")
//    implementation("androidx.activity:activity-compose:1.7.2")
//    implementation("io.coil-kt:coil-compose:1.4.0")
//    implementation("androidx.compose.runtime:runtime-livedata:1.5.1")
//
//    implementation ("com.google.android.gms:play-services-location:18.0.0")
//    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
//
//    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
//    implementation("com.squareup.okhttp3:okhttp:4.10.0")
//    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
//    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//
//    implementation("com.squareup.moshi:moshi:1.14.0")
//    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
//    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
//
//
//    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
//

}

kapt {
    correctErrorTypes = true
}
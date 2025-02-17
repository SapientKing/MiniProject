plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.mini_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mini_project"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    // Other dependencies
    implementation(libs.volley)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.google.places)
    implementation(libs.gson)


}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }


}

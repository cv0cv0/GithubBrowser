import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
}

android {
    compileSdkVersion(28)
    buildToolsVersion("28.0.3")
    dataBinding { isEnabled = true }
    defaultConfig {
        applicationId = "me.gr.githubbrowser"
        minSdkVersion(19)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        setSourceCompatibility(JavaVersion.VERSION_1_8)
        setTargetCompatibility(JavaVersion.VERSION_1_8)
    }
}

val supportLibraryVersion = "28.0.0"
val lifecycleVersion = "1.1.1"
val roomVersion = "1.1.1"
val navigationVersion: String by rootProject.extra
val daggerVersion = "2.15"
val retrofitVersion = "2.3.0"
val glideVersion = "4.7.1"
val timberVersion = "4.5.1"

dependencies {
    kapt("android.arch.lifecycle:compiler:$lifecycleVersion")
    kapt("android.arch.persistence.room:compiler:$roomVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    kapt("com.google.dagger:dagger-android-processor:$daggerVersion")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation("com.android.support:appcompat-v7:$supportLibraryVersion")
    implementation("com.android.support:recyclerview-v7:$supportLibraryVersion")
    implementation("com.android.support:cardview-v7:$supportLibraryVersion")
    implementation("com.android.support:design:$supportLibraryVersion")
    implementation("com.android.support.constraint:constraint-layout:1.1.3")
    implementation("android.arch.lifecycle:runtime:$lifecycleVersion")
    implementation("android.arch.lifecycle:extensions:$lifecycleVersion")
    implementation("android.arch.lifecycle:common-java8:$lifecycleVersion")
    implementation("android.arch.persistence.room:runtime:$roomVersion")
    implementation("android.arch.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("com.google.dagger:dagger:$daggerVersion")
    implementation("com.google.dagger:dagger-android:$daggerVersion")
    implementation("com.google.dagger:dagger-android-support:$daggerVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    implementation("com.jakewharton.timber:timber:$timberVersion")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
}

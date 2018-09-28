// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val navigationVersion by extra("1.0.0-alpha06")

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.2.0")
        classpath(kotlin("gradle-plugin", "1.2.71"))
        classpath("android.arch.navigation:navigation-safe-args-gradle-plugin:$navigationVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
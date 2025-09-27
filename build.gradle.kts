buildscript {
    repositories {
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.0")
        classpath(kotlin("gradle-plugin", version = "2.2.20"))
        classpath("com.github.ben-manes:gradle-versions-plugin:0.53.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

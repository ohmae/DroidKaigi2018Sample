buildscript {
    repositories {
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.9.1")
        classpath(kotlin("gradle-plugin", version = "2.1.20"))
        classpath("com.github.ben-manes:gradle-versions-plugin:0.52.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
        classpath(kotlin("gradle-plugin", version = "2.1.0"))
        classpath("com.github.ben-manes:gradle-versions-plugin:0.51.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

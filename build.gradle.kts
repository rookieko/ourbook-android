import org.apache.tools.ant.util.JavaEnvUtils.VERSION_11

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.13.2" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}
val targetCompatibility by extra(VERSION_11)

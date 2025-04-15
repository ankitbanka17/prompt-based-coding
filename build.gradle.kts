plugins {
    id("org.jetbrains.intellij") version "1.17.2"
    kotlin("jvm") version "1.9.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.json:json:20231013")
    implementation("com.microsoft.onnxruntime:onnxruntime:1.17.0")
}

intellij {
    version.set("2022.3")
    type.set("IC") // IntelliJ Community Edition
}

tasks {
    patchPluginXml {
        changeNotes.set("Initial version of the plugin.")
    }
}

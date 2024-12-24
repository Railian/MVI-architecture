import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_11
    explicitApi()
}

dependencies {
    api(projects.library.contract)
    api(projects.library.core.api)
    api(projects.library.config)
    api(libs.androidx.lifecycle.viewmodel)
    implementation(projects.library.impl.simple)
    implementation(projects.library.impl.embedded)
}

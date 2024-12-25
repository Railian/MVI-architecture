import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.signing)
    alias(libs.plugins.dokka)
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
    api(projects.library.core)
    api(projects.library.sharedConfig)
    api(libs.androidx.lifecycle.viewmodel)
    implementation(projects.library.implementation.simple)
    implementation(projects.library.implementation.embedded)
}

publishing {
    repositories {
        mavenBuildDir(project)
        mavenSonatype(project)
    }
    publications {
        createLibraryPublication(
            project = project,
            artifactId = "mvi-constructed",
            description = "A lightweight Android MVI library.",
        )
    }
}

signAllPublications()
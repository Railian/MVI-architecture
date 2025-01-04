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
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(projects.library.core.implementation)
}

publishing {
    repositories {
        mavenBuildDir(project)
        mavenSonatype(project)
    }
    publications {
        createLibraryPublication(
            project = project,
            artifactId = "mvi-mock",
            description = """
                A Kotlin library designed for mocking MviModels within the MVI architecture.
                
                This artifact provides tools to easily create and manage mock implementations 
                of MviModels to facilitate testing and development MVI-based ui components,
                and improve Composable Preview experience.
            """.trimIndent().trimNewLines(),
        )
    }
}

signAllPublications()

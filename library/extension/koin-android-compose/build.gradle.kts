import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.signing)
    alias(libs.plugins.dokka)
}

android {
    namespace = "ua.railian.mvi.koin.compose"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JvmTarget.JVM_11.target
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    explicitApi()
}

dependencies {
    api(projects.library.contract)
    api(libs.koin.core)
    implementation(libs.koin.androidx.compose)
    implementation(platform(libs.koin.bom))
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
}

publishing {
    repositories {
        mavenBuildDir(project)
        mavenSonatype(project)
    }
    publications {
        createLibraryPublication(
            project = project,
            components = arrayOf("release"),
            artifactId = "mvi-koin-android-compose",
            description = """
                Koin integration for MVI Architecture library in Android Compose environment.
                
                This library offers functionalities for creating and injecting MVI view models 
                within Compose using Koin and simplifying composable preview creation.
            """.trimIndent().trimNewLines(),
        )
    }
}

signAllPublications()

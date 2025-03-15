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
    namespace = "ua.railian.mvi.compose"
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
    api(libs.androidx.activity.compose)
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
            artifactId = "mvi-android-compose",
            description = """
                Android Compose extensions to improve the MviModel management,
                simplifying the rendering of states and the collecting of events.
            """.trimIndent().trimNewLines(),
        )
    }
}

signAllPublications()

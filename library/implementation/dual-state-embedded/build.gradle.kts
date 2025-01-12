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
            artifactId = "mvi-dual-state-embedded",
            description = """
                Dual-state implementation for MVI, where state is split into DOMAIN and UI.
                DOMAIN state represents the business logic state, while UI state represents the UI state.
                This allows for more flexibility in handling different types of data and events.
            """.trimIndent().trimNewLines(),
        )
    }
}

signAllPublications()
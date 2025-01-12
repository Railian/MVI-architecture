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
            artifactId = "mvi-constructed",
            description = """
                A Kotlin library that provides the AbstractMviModel implementation 
                to seamlessly integrate MVI architecture patterns into your project. 
                
                This artifact differs from `mvi-embedded` by offering a more constructed approach. 
                It introduces an `AbstractMviModel` where the IntentProcessor, StateReducer, and
                EventEmitter are injecting within the model as properties. This simplifies usage
                by handling internal state transitions and event emissions automatically, 
                making it easier to build complex MVI interactions within your MviModels.
            """.trimIndent().trimNewLines(),
        )
    }
}

signAllPublications()
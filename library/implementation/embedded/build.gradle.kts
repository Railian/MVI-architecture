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
    implementation(projects.library.implementation.simple)
}

publishing {
    repositories {
        mavenBuildDir(project)
        mavenSonatype(project)
    }
    publications {
        createLibraryPublication(
            project = project,
            artifactId = "mvi-embedded",
            description = """
                A Kotlin library that provides the AbstractMviModel implementation 
                to seamlessly integrate MVI architecture patterns into your project. 
                
                This artifact offers an implementation where embedded intent processor 
                produce results that can be handled by embedded state reducer or
                embedded action emitter to redux new state or emit new actions.
            """.trimIndent(),
        )
    }
}

signAllPublications()
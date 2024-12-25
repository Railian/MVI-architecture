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
            artifactId = "mvi-shared-config",
            description = """
                Glogal and Shared configuration of the MVI architecture library.
                
                Add it to your dependencies to create the custom implementation 
                of the MVI architecture pattern or to extend the functionality 
                of the existing implementations.
            """.trimIndent().trimNewLines(),
        )
    }
}

signAllPublications()

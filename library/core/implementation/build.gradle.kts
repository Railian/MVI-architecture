import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.atomicfu)
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
    api(projects.library.core)
    implementation(projects.library.contract)
    implementation(libs.kmlogging)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

publishing {
    repositories {
        mavenBuildDir(project)
        mavenSonatype(project)
    }
    publications {
        createLibraryPublication(
            project = project,
            artifactId = "mvi-core-implementation",
            description = """
                A Kotlin library that provides the core implementation 
                for building the MVI architecture pattern.
                
                Add it to your dependencies to create the custom implementation 
                of the MVI architecture pattern or to extend the functionality 
                of the existing implementations.
            """.trimIndent().trimNewLines(),
        )
    }
}

signAllPublications()
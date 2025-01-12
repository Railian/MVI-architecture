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
            artifactId = "mvi-simple",
            description = """
                A Kotlin library that provides the AbstractMviModel implementation 
                to seamlessly integrate MVI architecture patterns into your project.
                
                It is as simple as possible and requires just to provide the process 
                fun implementation, where you will have access to the mutable state 
                (mutable events, etc.) through the PipelineScope.
            """.trimIndent().trimNewLines(),
        )
    }
}

signAllPublications()
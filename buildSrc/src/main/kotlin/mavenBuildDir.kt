import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.getValue

fun RepositoryHandler.mavenBuildDir(project: Project) {
    maven {
        name = "buildDir"
        val buildDir by project.rootProject.layout.buildDirectory
        url = buildDir.dir("maven-repository").let(project::uri)
    }
}

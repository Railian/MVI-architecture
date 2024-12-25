import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

fun RepositoryHandler.mavenSonatype(project: Project) {
    mavenCentral {
        name = "Sonatype"
        url = when (project.version.toString().endsWith("-SNAPSHOT")) {
            true -> "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            else -> "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
        }.let(project::uri)
        credentials {
            username = System.getenv("sonatypeUsername")
            password = System.getenv("sonatypePassword")
        }
    }
}

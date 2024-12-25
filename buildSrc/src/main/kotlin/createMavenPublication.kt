import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

fun PublicationContainer.createLibraryPublication(
    project: Project,
    components: Array<String> = arrayOf("kotlin"),
    artifactId: String,
    description: String,
) {
    val githubRepositoryName = "MVI"
    create<MavenPublication>("library") {
        this.artifactId = artifactId
        artifact(project.javadocJar)
        project.afterEvaluate {
            components.forEach {
                from(project.components[it])
            }
        }
        pom {
            name.set("$groupId:$artifactId")
            url.set("https://github.com/Railian/$githubRepositoryName")
            this.description.set(description)
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            scm {
                connection.set("https://github.com/Railian/$githubRepositoryName.git")
                developerConnection.set("git@github.com:Railian/$githubRepositoryName.git")
                url.set("https://github.com/Railian/$githubRepositoryName")
            }
            developers {
                developer {
                    id.set("YevhenRailian")
                    name.set("Yevhen Railian")
                    email.set("yevhen.railian.v@gmail.com")
                }
            }
        }
    }
}

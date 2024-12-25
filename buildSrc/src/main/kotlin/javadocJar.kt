import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register

// Use Dokka's generated HTML for the Javadoc JAR
internal val Project.javadocJar: TaskProvider<Jar>
    get() = tasks.register<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        from(tasks["dokkaHtml"])
    }

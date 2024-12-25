import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.internal.extensions.core.extra
import org.gradle.plugins.signing.SigningExtension

fun Project.signAllPublications() {
    project.extra["signing.keyId"] = System.getenv("signingKeyId")
    project.extra["signing.password"] = System.getenv("signingPassword")
    project.extra["signing.secretKeyRingFile"] = System.getenv("signingSecretKeyRingFile")
    extensions.configure(SigningExtension::class.java) {
        val publishing = extensions.getByType(PublishingExtension::class.java)
        sign(publishing.publications)
    }
}

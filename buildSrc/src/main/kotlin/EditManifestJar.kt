import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.jvm.tasks.Jar
import javax.inject.Inject

abstract class EditManifestJar : Jar() {
    @get:Inject
    abstract val archiveOps: ArchiveOperations

    @get:InputFile
    abstract val inputJar: RegularFileProperty

    override fun copy() {
        // copy all from the JAR
        from(inputJar.map { archiveOps.zipTree(it) })

        // find and put the manifest
        val manifestFile = archiveOps.zipTree(inputJar).find { it.name == "MANIFEST.MF" }
        if (manifestFile != null) {
            val existingAttributes = java.util.jar.Manifest(manifestFile.inputStream()).mainAttributes
            manifest {
                attributes(
                    existingAttributes.entries.associate { it.key.toString() to it.value.toString() }
                )
            }
        }

        super.copy()
    }
}

plugins {
    id("com.github.ElytraServers.elytra-conventions") version "v1.1.2"
    id("com.gtnewhorizons.gtnhconvention")
}

elytraModpackVersion {
    gtnhVersion = "2.8.4"
}

dependencies {
    devOnlyNonPublishable(elytraModpackVersion.gtnhdev("GT5-Unofficial"))

    // lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // jspecify
    compileOnly(libs.jspecify)

    // TODO: shadowing or something else
    shadowImplementation(libs.mojang.dfu) { isTransitive = false }
    shadowImplementation(libs.mojang.brigadier) { isTransitive = false }
}

configurations.configureEach {
    resolutionStrategy {
        force("com.github.GTNewHorizons:GTNHLib:0.7.10")
    }
}

idea {
    module {
        excludeDirs.add(file("run"))
    }
}

// Generate Standalone JAR
val standaloneJar = tasks.register<EditManifestJar>("makeStandaloneJar") {
    inputJar = tasks.reobfJar.flatMap { it.archiveFile }
    archiveClassifier = "standalone"

    manifest {
        attributes(
            "Unconfined-Standalone" to "true",
            "Unconfined-Standalone-Vendor" to "Folia",
        )
    }
}

tasks.assemble {
    dependsOn(standaloneJar)
}

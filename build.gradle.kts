import com.gtnewhorizons.retrofuturagradle.mcp.ReobfuscatedJar
import org.gradle.jvm.tasks.Jar

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
val optInJar: TaskProvider<Jar> = tasks.register<Jar>("optInJar") {
    archiveClassifier = "standalone-dev"
    from(sourceSets.main.get().output)
    manifest {
        attributes("Unconfined-Opt-In" to "true")
    }
}

val reobfOptInJar = tasks.register<ReobfuscatedJar>("reobfOptInJar") {
    archiveClassifier = "standalone"
    setInputJarFromTask(optInJar)
    mcVersion = minecraft.mcVersion
    srg = tasks.generateForgeSrgMappings.flatMap { it.mcpToSrg }
    fieldCsv = tasks.generateForgeSrgMappings.flatMap { it.fieldsCsv }
    methodCsv = tasks.generateForgeSrgMappings.flatMap { it.methodsCsv }
    exceptorCfg = tasks.generateForgeSrgMappings.flatMap { it.srgExc }
    recompMcJar = tasks.packagePatchedMc.flatMap { it.archiveFile }
    referenceClasspath.from(
        configurations.runtimeClasspath,
        tasks.packageMcLauncher,
        tasks.packagePatchedMc,
        configurations.patchedMinecraft,
        configurations.runtimeClasspath,
        configurations.compileClasspath,
    )
}

tasks.build {
    dependsOn(reobfOptInJar)
}

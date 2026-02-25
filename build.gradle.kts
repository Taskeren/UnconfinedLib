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

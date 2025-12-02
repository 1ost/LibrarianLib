import org.gradle.api.tasks.compile.JavaCompile

val mixin_version: String by project

plugins {
    id("minecraft-conventions")
    id("org.spongepowered.mixin")
}

val refmapName = "ll-${project.name}.refmap.json"
val mixinConfigName = "ll-${project.name}.mixins.json"
val generatedRefmap = layout.buildDirectory.dir("generated/mixin-refmap")

mixin {
    add(sourceSets.main.get(), refmapName)
    config(mixinConfigName)
}

sourceSets.main {
    resources.srcDir(generatedRefmap)
}

tasks.withType<JavaCompile>().configureEach {
    doFirst {
        generatedRefmap.get().asFile.mkdirs()
    }
    options.compilerArgs.add("-AoutRefMapFile=${generatedRefmap.get().file(refmapName).asFile}")
}

dependencies {
    annotationProcessor("org.spongepowered:mixin:$mixin_version:processor")
    testAnnotationProcessor("org.spongepowered:mixin:$mixin_version:processor")
}

plugins {
    id("minecraft-conventions")
    id("org.spongepowered.mixin")
}

mixin {
    add(sourceSets.main.get(), "ll-%s.refmap.json".format(project.getName()))
    add(sourceSets.test.get(), "ll-%s.refmap.json".format(project.getName()))
    config("ll-%s.mixins.json".format(project.getName()))
}

val mixin_version: String by project

dependencies {
    annotationProcessor("org.spongepowered:mixin:$mixin_version:processor")
    testAnnotationProcessor("org.spongepowered:mixin:$mixin_version:processor")
}
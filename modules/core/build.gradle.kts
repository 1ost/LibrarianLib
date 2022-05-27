

plugins {
    `module-conventions`
    `mixin-conventions`
}

module {
    shadow("dev.thecodewarrior.mirror")
}
val commonConfig = rootProject.the<CommonConfigExtension>()

dependencies {
    shade("dev.thecodewarrior.mirror:mirror:1.0.0b1")
    testApi(project(":testcore"))
}

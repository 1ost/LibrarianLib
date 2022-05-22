import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.ProjectLocalConfigurations

plugins {
    `module-conventions`
    `mixin-conventions`
}


//import org.gradle.internal.os.OperatingSystem
//switch (OperatingSystem.current()) {
//    case OperatingSystem.LINUX:
//        ext.lwjglNatives = "natives-linux"
//        break
//    case OperatingSystem.MAC_OS:
//        ext.lwjglNatives = "natives-macos"
//        break
//    case OperatingSystem.WINDOWS:
//        ext.lwjglNatives = "natives-windows"
//        break
//}

module {
    shadow("com.ibm.icu")
    shadow("dev.thecodewarrior.bitfont")
    shadow("org.msgpack")
}

dependencies {
    liblib(project(":core"))
    liblib(project(":mosaic"))
    liblib(project(":albedo"))
    liblib(project(":etcetera"))
    liblib(project(":scribe"))
    liblib(project(":courier"))
    testApi(project(":testcore"))
    shade("dev.thecodewarrior:bitfont:0.4")
    shade("com.ibm.icu:icu4j:63.1")
    shade("org.msgpack:msgpack-core:0.8.16")

    devClasspath("com.ibm.icu:icu4j:63.1")

    compileOnly("mezz.jei:jei-1.16.5:7.7.1.152:api")
    testCompileOnly("mezz.jei:jei-1.16.5:7.7.1.152")
    mod(fg.deobf("mezz.jei:jei-1.16.5:7.7.1.152"))
    compileOnly("curse.maven:mouse-tweaks-60089:3035782")
    clientMod(fg.deobf("curse.maven:mouse-tweaks-60089:3035780")) // mouse tweaks crashes datagen
    serverMod(fg.deobf("curse.maven:mouse-tweaks-60089:3035780"))
    compileOnly("curse.maven:inventory-sorter-240633:3077903")
}

plugins {
    `module-conventions`
    `mixin-conventions`
}

mixin {
    add(sourceSets.main.get(), "abc") //Idk why, but i can't have it be ll-facade
    add(sourceSets.test.get(), "abc")
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

    // we have to include ICU in the classpath so it overrides Mojang"s stripped down copy
    devClasspath("com.ibm.icu:icu4j:63.1")

    // Compatibility:
    // `fg.deobf` breaks source jars (MinecraftForge/ForgeGradle#736,727,637), so some dependencies aren"t deobfuscated

    // The API doesn"t seem to have anything that actually gets obfuscated and we want sources, so no `fg.deobf`
    compileOnly("mezz.jei:jei-1.16.5:7.7.1.152:api")
    // include the implementation somewhere so we can see the source code in the IDE. (We put it in test to avoid
    // accidental coupling with the module.)
    // sure, without `fg.deobf` the jar and sources won"t line up, but that shouldn"t really matter since this code
    // should never actually get used.
    testCompileOnly("mezz.jei:jei-1.16.5:7.7.1.152")
    // this is the part that actually gets used at runtime, so deobfuscate it
    mod(fg.deobf("mezz.jei:jei-1.16.5:7.7.1.152"))

    compileOnly("curse.maven:mouse-tweaks-60089:3035782")
    clientMod(fg.deobf("curse.maven:mouse-tweaks-60089:3035780")) // mouse tweaks crashes datagen
    serverMod(fg.deobf("curse.maven:mouse-tweaks-60089:3035780"))
    mod(fg.deobf("curse.maven:inventory-sorter-240633:3077903"))
}


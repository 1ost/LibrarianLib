plugins {
    `module-conventions`
    `mixin-conventions`
}

mixin {
    add(sourceSets.main.get(), "ll-lieutenant.refmap.json")
    add(sourceSets.test.get(), "ll-lieutenant-test.refmap.json")
}

dependencies {
    liblib(project(":core"))
    testApi(project(":testcore"))
}

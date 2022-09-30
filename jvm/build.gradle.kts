plugins {
    id("com.github.minigdx.jvm")
}

dependencies {
    implementation(project(":common"))
}

minigdx {
    mainClass.set("showcase.Main")
}

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

intellij {
    pluginName = providers.gradleProperty("pluginName")
    version = providers.gradleProperty("platformVersion")
    type = providers.gradleProperty("platformType")
}

tasks {
    patchPluginXml {
        sinceBuild = providers.gradleProperty("pluginSinceBuild")
        untilBuild = providers.gradleProperty("pluginUntilBuild")
    }

    wrapper {
        gradleVersion = "8.6"
    }
}

import io.papermc.paperweight.util.defaultJavaLauncher

plugins {
    java
    id("io.papermc.paperweight.source-generator")
}

paperweight {
    atFile.set(layout.projectDirectory.file("wideners.at"))
}

repositories {
    mavenLocal() // todo publish typewriter somewhere
}

dependencies {
    minecraftJar(project(":paper-server", "mappedJarOutgoing"))
    implementation(project(":paper-server", "macheMinecraftLibraries"))

    implementation("com.squareup:javapoet:1.13.0")
    implementation(project(":paper-api"))
    implementation("io.papermc.typewriter:typewriter:1.0-SNAPSHOT") {
        isTransitive = false // paper-api already have everything
    }
    implementation("io.github.classgraph:classgraph:4.8.47")
    implementation("org.jetbrains:annotations:26.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.registerGenerationTask("generate") {
    description = "Generate and rewrite boilerplate content of API and its implementation"
    dependsOn(tasks.check)
    mainClass.set("io.papermc.generator.Main")
    classpath(sourceSets.main.map { it.runtimeClasspath })
    systemProperty("typewriter.lexer.ignoreMarkdownDocComments", true)
}

tasks.registerGenerationTask("scanOldGeneratedSourceCode") {
    description = "Scan source code to detect outdated generated code"
    mainClass.set("io.papermc.generator.rewriter.OldGeneratedCodeTest")
    classpath(sourceSets.test.map { it.runtimeClasspath })
}

fun TaskContainer.registerGenerationTask(
    name: String,
    block: JavaExec.() -> Unit
): TaskProvider<JavaExec> = register<JavaExec>(name) {
    group = "generation"
    javaLauncher = project.javaToolchains.defaultJavaLauncher(project)
    args(project(":paper-api").projectDir.toString(), project(":paper-server").projectDir.toString())

    block(this)
}

tasks.test {
    useJUnitPlatform()
}

group = "io.papermc.paper"
version = "1.0-SNAPSHOT"

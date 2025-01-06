import io.papermc.paperweight.util.capitalized
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

val rewriteApi = tasks.registerGenerationTask("rewriteApi", "paper-api") {
    description = "Rewrite boilerplate content of API"
    dependsOn("testRewriteApi")
    mainClass.set("io.papermc.generator.Main\$Rewriter")
    classpath(sourceSets.main.map { it.runtimeClasspath })
    systemProperty("typewriter.lexer.ignoreMarkdownDocComments", true)
}

val rewriteImpl = tasks.registerGenerationTask("rewriteImpl", "paper-server") {
    description = "Rewrite boilerplate content of API implementation"
    dependsOn("testRewriteImpl")
    mainClass.set("io.papermc.generator.Main\$Rewriter")
    classpath(sourceSets.main.map { it.runtimeClasspath })
    systemProperty("typewriter.lexer.ignoreMarkdownDocComments", true)
}

tasks.register("rewrite") {
    group = "generation"
    description = "Rewrite boilerplate content of API and its implementation"
    dependsOn(rewriteApi, rewriteImpl)
}


val generateApi = tasks.registerGenerationTask("generateApi", "paper-api") {
    description = "Generate boilerplate content of API"
    dependsOn("testGenerateApi")
    mainClass.set("io.papermc.generator.Main\$Generator")
    classpath(sourceSets.main.map { it.runtimeClasspath })
}

val generateImpl = tasks.registerGenerationTask("generateImpl", "paper-server") {
    description = "Generate boilerplate content of API implementation"
    dependsOn("testGenerateImpl")
    mainClass.set("io.papermc.generator.Main\$Generator")
    classpath(sourceSets.main.map { it.runtimeClasspath })
}

tasks.register("generate") {
    group = "generation"
    description = "Generate boilerplate content of API and its implementation"
    dependsOn(generateApi, generateImpl)
}

tasks.register<JavaExec>("scanOldGeneratedSourceCode") {
    group = "verification"
    javaLauncher = project.javaToolchains.defaultJavaLauncher(project)
    description = "Scan source code to detect outdated generated code"
    args(project(":paper-api").projectDir.toString(), project(":paper-server").projectDir.toString())
    mainClass.set("io.papermc.generator.rewriter.OldGeneratedCodeTest")
    classpath(sourceSets.test.map { it.runtimeClasspath })
}

fun TaskContainer.registerGenerationTask(
    name: String,
    vararg args: String,
    block: JavaExec.() -> Unit
): TaskProvider<JavaExec> = register<JavaExec>(name) {
    group = "generation"
    javaLauncher = project.javaToolchains.defaultJavaLauncher(project)
    if (args.isNotEmpty()) {
        val projectDirs = args.map { project.rootProject.findProject(it)?.projectDir }
        args(projectDirs.map { it.toString() })
        inputs.files(projectDirs)
    }

    block(this)
}

sequenceOf("api", "impl").forEach { side ->
    sequenceOf("generate", "rewrite").forEach { type ->
        val task = tasks.register<Test>("test${type.capitalized()}${side.capitalized()}") {
            group = "verification"
            javaLauncher = project.javaToolchains.defaultJavaLauncher(project)
            useJUnitPlatform {
                includeTags.add("${type}-${side}")
            }
        }
        tasks.check {
            dependsOn(task)
        }
    }
}

group = "io.papermc.paper"
version = "1.0-SNAPSHOT"

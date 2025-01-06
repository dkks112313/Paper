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

val gameVersion = providers.gradleProperty("mcVersion")

val rewriteApi = tasks.registerGenerationTask("rewriteApi", true, "paper-api") {
    description = "Rewrite existing API classes"
    mainClass.set("io.papermc.generator.Main\$Rewriter")
    classpath(sourceSets.main.map { it.runtimeClasspath })
}

val rewriteImpl = tasks.registerGenerationTask("rewriteImpl", true, "paper-server") {
    description = "Rewrite existing implementation classes"
    mainClass.set("io.papermc.generator.Main\$Rewriter")
    classpath(sourceSets.main.map { it.runtimeClasspath })
}

tasks.register("rewrite") {
    group = "generation"
    description = "Rewrite existing API classes and its implementation"
    dependsOn(rewriteApi, rewriteImpl)
}


val generateApi = tasks.registerGenerationTask("generateApi", false, "paper-api") {
    description = "Generate new API classes"
    mainClass.set("io.papermc.generator.Main\$Generator")
    classpath(sourceSets.main.map { it.runtimeClasspath })
}

val generateImpl = tasks.registerGenerationTask("generateImpl", false, "paper-server") {
    description = "Generate new implementation classes"
    mainClass.set("io.papermc.generator.Main\$Generator")
    classpath(sourceSets.main.map { it.runtimeClasspath })
}

tasks.register("generate") {
    group = "generation"
    description = "Generate new API classes and its implementation"
    dependsOn(generateApi, generateImpl)
}

if (providers.gradleProperty("updatingMinecraft").getOrElse("false").toBoolean()) {
    val scanOldGeneratedSourceCode by tasks.registering(JavaExec::class) {
        group = "verification"
        description = "Scan source code to detect outdated generated code"
        javaLauncher = javaToolchains.defaultJavaLauncher(project)
        mainClass.set("io.papermc.generator.rewriter.utils.ScanOldGeneratedSourceCode")
        classpath(sourceSets.main.map { it.runtimeClasspath })

        val projectDirs = listOf("paper-api", "paper-server").mapNotNull { project.rootProject.findProject(it)?.projectDir }
        args(projectDirs.map { it.toString() })
        val workDirs = projectDirs.map { it.resolve("src/main/java") }

        inputs.files(workDirs)
        inputs.property("gameVersion", gameVersion)
        outputs.dirs(workDirs)
    }
    tasks.check {
        dependsOn(scanOldGeneratedSourceCode)
    }
}

fun TaskContainer.registerGenerationTask(
    name: String,
    rewrite: Boolean,
    vararg args: String,
    block: JavaExec.() -> Unit
): TaskProvider<JavaExec> = register<JavaExec>(name) {
    group = "generation"
    dependsOn("checkModuleFor${name.capitalized()}")
    javaLauncher = project.javaToolchains.defaultJavaLauncher(project)
    inputs.property("gameVersion", gameVersion)
    inputs.dir(layout.projectDirectory.dir("src/main/java")).withPathSensitivity(PathSensitivity.RELATIVE)
    val projectDirs = args.mapNotNull { project.rootProject.findProject(it)?.projectDir }
    if (projectDirs.isNotEmpty()) {
        args(projectDirs)
        if (rewrite) {
            systemProperty("typewriter.lexer.ignoreMarkdownDocComments", true)
            inputs.files(projectDirs.map { it.resolve("src/main/java") })
            outputs.dirs(projectDirs.map { it.resolve("src/main/java") })
        } else {
            outputs.dirs(projectDirs.map { it.resolve("src/main/generated") })
        }
    } else {
        error("Projects $args unavailable during configuration phase")
    }

    block(this)
}

tasks.test {
    useJUnitPlatform()
}

val test by testing.suites.existing(JvmTestSuite::class)
sequenceOf("api", "impl").forEach { side ->
    sequenceOf("generate", "rewrite").forEach { type ->
        val task = tasks.register<Test>("checkModuleFor${type.capitalized()}${side.capitalized()}") {
            group = "verification"
            javaLauncher = project.javaToolchains.defaultJavaLauncher(project)
            useJUnitPlatform {
                includeTags("$type-$side") // todo skip when no test found
            }
            testClassesDirs = files(test.map { it.sources.output.classesDirs })
            classpath = files(test.map { it.sources.runtimeClasspath })
        }
        tasks.check {
            dependsOn(task)
        }
    }
}

group = "io.papermc.paper"
version = "1.0-SNAPSHOT"

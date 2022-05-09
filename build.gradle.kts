import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.6.20"
    id("net.bitsandbobs.kradle") version "2.3.1"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.netty:netty5-all:5.0.0.Alpha1")

    implementation(files("lib/jfr-analytics-1.0.0-SNAPSHOT.jar"))

    implementation("org.apache.calcite:calcite-core:1.30.0")
    implementation("org.apache.calcite:calcite-csv:1.30.0")

    implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
}

group = "com.github.gavinray97"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


kradle {
    jvm {
        kotlin {
            useCoroutines()
            lint {
                ktlint.enable()
            }
            test {
                //useKotest()
                useMockk()
            }
        }

        application {
            mainClass("Netty5PostgresWireServerKt")
        }

        //dependencies.enable()
        //vulnerabilityScan.enable()
        lint.enable()
        codeAnalysis.enable()
        developmentMode.enable()

        test {
            junitJupiter.enable {
                version("5.8.2")
            }
            prettyPrint(true)
            showStandardStreams(true)
            withIntegrationTests()
            withFunctionalTests()
        }

        codeCoverage.enable()
        benchmark.enable()
        packaging.enable()

        docker {
            withJvmKill()
        }

        documentation.enable()
    }
}

tasks {
    val ENABLE_PREVIEW = "--enable-preview"

    withType<JavaCompile> {
        options.compilerArgs.add(ENABLE_PREVIEW)
        options.compilerArgs.add("-Xlint:preview")
    }

    withType<Test> {
        useJUnitPlatform()
        jvmArgs!!.add(ENABLE_PREVIEW)
    }

    withType<JavaExec> {
        jvmArgs!!.add(ENABLE_PREVIEW)
    }

    withType<KotlinCompile> {
        kotlinOptions.javaParameters = true
        kotlinOptions.freeCompilerArgs =
            listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlin.Experimental",

                // Enables automatic inference of generic type parameters
                // when calling buildMap { ... }, buildList { ... }, etc.
                "-Xenable-builder-inference",

                // [Kotlin 1.6.20]
                // Parallelize compilation of source files to number of cores
                "-Xbackend-threads=0",

                // [Kotlin 1.6.20]
                // Enables experimental support for Context Receivers
                "-Xcontext-receivers",
            )
    }
}

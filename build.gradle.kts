import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build", "gradle", "3.4.0")
        classpath("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.3.31")
        classpath("org.jlleitschuh.gradle", "ktlint-gradle", "7.4.0")
        classpath("org.jetbrains.kotlin", "kotlin-serialization", "1.3.31")
    }
}

apply(plugin = "org.jlleitschuh.gradle.ktlint")

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://jitpack.io")
        mavenCentral()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs += listOf(
                "-Xuse-experimental=kotlin.Experimental"
            )
            jvmTarget = "1.8"
        }
    }

    tasks.withType<Test> {
        testLogging {
            // set options for log level LIFECYCLE
            events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_OUT
            )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = false
            showStandardStreams = true
        }
    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}

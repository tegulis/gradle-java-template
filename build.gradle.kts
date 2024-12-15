import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.nio.file.Paths

plugins {
	application
	id("com.gradleup.shadow") version "9.0.0-beta4"
}

group = "net.tegulis.template"
version = "1.1.0"

application {
	mainClass = "${project.group}.Main"
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenLocal()
	mavenCentral()
}

dependencies {
	// TEST
	testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.10.3")
	testRuntimeOnly(group = "org.junit.platform", name = "junit-platform-launcher")
	// Google Truth
	testImplementation(group = "com.google.truth", name = "truth", version = "1.4.2")
}

tasks.register("replaceVersion") {
	group = "other"
	doFirst {
		val mainClassPath = application.mainClass
			                    .get()
			                    .replace(".", "/") + ".java"
		for (dir in sourceSets.main.get().java.srcDirs) {
			val mainClassPathInSourceSet = "${dir}/${mainClassPath}"
			val mainClassFile = Paths
				.get(mainClassPathInSourceSet)
				.toFile()
			if (mainClassFile.exists()) {
				val contents = mainClassFile.readText()
				val modifiedContents = contents.replace(Regex("VERSION\\s*=\\s*\"(.*?)\""), "VERSION = \"${version}\"")
				mainClassFile.writeText(modifiedContents)
			}
		}
	}
}

tasks.withType<JavaCompile> {
	dependsOn("replaceVersion")
}

tasks.withType<Test> {
	group = "verification"
	useJUnitPlatform()
	enableAssertions = true
	// Extra settings for very verbose testing
	testLogging {
		events = TestLogEvent.values().filter { it != TestLogEvent.STARTED }.toSet()
		exceptionFormat = TestExceptionFormat.FULL
		showExceptions = true
		showCauses = true
		showStackTraces = true
	}
	// Google Truth: don't clean stack traces
	systemProperty("com.google.common.truth.disable_stack_trace_cleaning", "true")
	// Don't generate reports
	reports.all { required = false }
}

tasks.named("shadowJar") {
	group = "distribution"
}

// Pack the shadowJar and asset files into a zip
tasks.register("packZip", Zip::class) {
	group = "distribution"
	dependsOn("shadowJar")
	archiveFileName = "${project.name}-${project.version}.zip"
	destinationDirectory = file("build/distributions")
	from("build/libs") {
		include("${project.name}-${project.version}-all.jar")
	}
	// Add assets here
	// from("assets") {
	// 	include("**/*")
	// }
}

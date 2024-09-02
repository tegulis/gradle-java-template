import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	application
}

group = "net.tegulis.template"
version = "0.1.0"

application {
	mainClass = "net.tegulis.template.main"
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

tasks.withType<Test> {
	group = "verification"
	useJUnitPlatform()
	enableAssertions = true
	// Extra settings for very verbose testing
	testLogging {
		events = setOf(
			TestLogEvent.FAILED,
			TestLogEvent.PASSED,
			TestLogEvent.SKIPPED,
			TestLogEvent.STANDARD_ERROR,
			TestLogEvent.STANDARD_OUT
		)
		exceptionFormat = TestExceptionFormat.FULL
		showExceptions = true
		showCauses = true
		showStackTraces = true
	}
	systemProperty("com.google.common.truth.disable_stack_trace_cleaning", "true")
	reports.all { required = false }
}

// Task to pack the application in a flat jar
tasks.register("flatJar", Jar::class) {
	group = "distribution"
	dependsOn(configurations.runtimeClasspath)
	manifest {
		attributes["Main-Class"] = application.mainClass
	}
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
	with(tasks.jar.get())
	destinationDirectory = file("build/distributions/flat")
	// Copy other files next to the flat jar
//	doLast {
//		copy {
//			from(".")
//			into(destinationDirectory)
//			include("assets/**")
//		}
//	}
}

// Pack the flatJar and surrounding files into a zip
tasks.register("packZip", Zip::class) {
	group = "distribution"
	dependsOn("flatJar")
	archiveFileName = "${project.name}-${project.version}.zip"
	destinationDirectory = file("build/distributions")
	from("build/distributions/flat") {
		include("**/*")
	}
}

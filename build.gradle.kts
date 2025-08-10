plugins {
	java
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.flexflow"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Core Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// Spring State Machine
	implementation("org.springframework.statemachine:spring-statemachine-starter:3.2.1")

	// Database (H2 for POC, can be swapped for PostgreSQL, etc.)
	runtimeOnly("com.h2database:h2")

	// Utility to reduce boilerplate code (getters, setters, builders)
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

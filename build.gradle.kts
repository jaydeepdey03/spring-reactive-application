plugins {
	java
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(22)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	// implementation("org.springframework.boot:spring-boot-starter-data-redis")
	// implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("org.springframework.boot:spring-boot-starter-kafka")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	annotationProcessor("org.projectlombok:lombok")
	// testImplementation("org.springframework.boot:spring-boot-starter-data-redis-reactive-test")
	// testImplementation("org.springframework.boot:spring-boot-starter-data-redis-test")
	testImplementation("org.springframework.boot:spring-boot-starter-kafka-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
	implementation("io.jsonwebtoken:jjwt-api:0.12.7")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.7")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.7")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  	testImplementation("org.springframework.boot:spring-boot-starter-data-r2dbc-test")
	runtimeOnly("org.postgresql:r2dbc-postgresql")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
	implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val migrateDb by tasks.registering(JavaExec::class) {
	group = "database"
	description = "Run Flyway migrations without starting the application"
	classpath = sourceSets.main.get().runtimeClasspath
	mainClass.set("com.example.tracker.DatabaseMigrateCommand")
	args("migrate")
}

val resetDb by tasks.registering(JavaExec::class) {
	group = "database"
	description = "Drop the public schema and re-run Flyway migrations plus seed data"
	classpath = sourceSets.main.get().runtimeClasspath
	mainClass.set("com.example.tracker.DatabaseMigrateCommand")
	args("reset")
}

val repairDb by tasks.registering(JavaExec::class) {
	group = "database"
	description = "Repair Flyway schema history after editing an applied migration"
	classpath = sourceSets.main.get().runtimeClasspath
	mainClass.set("com.example.tracker.DatabaseMigrateCommand")
	args("repair")
}

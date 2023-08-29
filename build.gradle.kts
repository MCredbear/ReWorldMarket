import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val exposedVersion: String by project
val hikariVersion: String by project
val mysqlDriverVersion: String by project
val h2DriverVersion: String by project
val postgresqlDriverVersion: String by project
val kotlinVersion: String by project
val kotlinSerializationVersion: String by project
val detectKtVersion: String by project

plugins {
    kotlin("jvm") version "1.9.0"
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.4.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
}

group = "dev.krysztal"
version = "1.0-SNAPSHOT"

val targetJavaVersion = 17
val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks

val paperVersion = "1.19.2-R0.1-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo1.maven.org/maven2/")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")
    paperDevBundle(paperVersion)

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation(kotlin("test"))
    compileOnly(kotlin("stdlib"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    shadow("org.bstats:bstats-bukkit:3.0.2")

    // Database
    compileOnly("org.jetbrains.exposed:exposed-core:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-core:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    compileOnly("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

    compileOnly("com.zaxxer:HikariCP:$hikariVersion")

    compileOnly("mysql:mysql-connector-java:$mysqlDriverVersion")
    compileOnly("com.h2database:h2:$h2DriverVersion")
    compileOnly("org.postgresql:postgresql:$postgresqlDriverVersion")

    // test Database
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")

    testImplementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    testImplementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    testImplementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    testImplementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    testImplementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    testImplementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    testImplementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

    testImplementation("com.zaxxer:HikariCP:$hikariVersion")

    testImplementation("mysql:mysql-connector-java:$mysqlDriverVersion")
    testImplementation("com.h2database:h2:$h2DriverVersion")
    testImplementation("org.postgresql:postgresql:$postgresqlDriverVersion")
}

tasks.shadowJar {
    relocate("org.bstats", "dev.krysztal.relocate.bstats")
    minimize()
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.processResources {
    inputs.property("version", version)
    filesMatching("plugin.yml") {
        expand(
            mutableMapOf(
                "version" to version,
                "exposedVersion" to exposedVersion,
                "hikariVersion" to hikariVersion,
                "mysqlDriverVersion" to mysqlDriverVersion,
                "h2DriverVersion" to h2DriverVersion,
                "postgresqlDriverVersion" to postgresqlDriverVersion,
                "kotlinVersion" to kotlinVersion,
                "kotlinSerializationVersion" to kotlinSerializationVersion
            )
        )
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    autoCorrect = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        md.required.set(true)
    }
    jvmTarget = "17"
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "17"
}

compileKotlin.kotlinOptions {
    jvmTarget = "17"
}

compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}

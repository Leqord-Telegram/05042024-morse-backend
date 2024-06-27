import java.util.*

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project

val aws_sdk_version = "1.2.10"
val smithy_version = "1.2.3"
val tcnative_version = "2.0.65.Final"
val postgres_version = "42.7.3"
val shapeshift_version = "0.8.0"
val hikaricp_version = "4.0.3"
val fuzzywuzzy_version = "1.4.0"
var guava_version = "33.2.0-jre"

val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
val tcnative_classifier = when {
    osName.contains("win") -> "windows-x86_64"
    osName.contains("linux") -> "linux-x86_64"
    osName.contains("mac") -> "osx-x86_64"
    else -> null
}

plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("io.ktor.plugin") version "2.3.10"
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
}

group = "ru.morsianin_shop"
version = "0.0.1"

application {
    mainClass.set("ru.morsianin_shop.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-resources:$ktor_version")
    implementation("io.ktor:ktor-server-auto-head-response:$ktor_version")
    implementation("io.ktor:ktor-server-rate-limit:$ktor_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("commons-codec:commons-codec:1.17.0")
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("com.zaxxer:HikariCP:$hikaricp_version")
    implementation("aws.sdk.kotlin:s3:$aws_sdk_version")
    implementation("aws.sdk.kotlin:s3control:$aws_sdk_version")
    implementation("aws.sdk.kotlin:sts:$aws_sdk_version")
    implementation("aws.sdk.kotlin:secretsmanager:$aws_sdk_version")
    implementation("aws.smithy.kotlin:http-client-engine-okhttp:$smithy_version")
    implementation("aws.smithy.kotlin:http-client-engine-crt:$smithy_version")
    implementation("aws.smithy.kotlin:aws-signing-crt:$smithy_version")
    implementation("aws.smithy.kotlin:http-auth-aws:$smithy_version")
    implementation("me.xdrop:fuzzywuzzy:$fuzzywuzzy_version")
    implementation("com.google.guava:guava:$guava_version")
    implementation("eu.vendeli:telegram-bot:6.1.1")
    ksp("eu.vendeli:ksp:6.1.1")


    if (tcnative_classifier != null) {
        implementation("io.netty:netty-tcnative-boringssl-static:$tcnative_version:$tcnative_classifier")
    } else {
        implementation("io.netty:netty-tcnative-boringssl-static:$tcnative_version")
    }

    testImplementation("io.ktor:ktor-server-tests")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

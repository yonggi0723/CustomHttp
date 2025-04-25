plugins {
    id("java-library")
    id("jacoco")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

afterEvaluate {
    tasks.withType<Jar> {
        from("consumer-rules.pro")
    }
}

dependencies {
    testImplementation(libs.junit.junit)
    testImplementation(libs.mockito.core.v481)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(kotlin("test"))
}

java {
    withSourcesJar()
    withJavadocJar()
}


tasks.withType<Jar> {
    manifest {
        attributes["Implementation-Title"] = "CustomHttp"
        attributes["Implementation-Version"] = "1.0.0"
    }
}

tasks.build {
    dependsOn(tasks.jar)
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.test {
    useJUnit()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true) // 이게 true여야 HTML 생김
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}
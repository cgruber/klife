import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    id("org.jetbrains.compose") version "0.3.2"
}

group = "com.geekinasuit.demos"
version = "HEAD"

repositories {
  jcenter()
  mavenCentral()
  maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}
configurations.all {
  resolutionStrategy.force("junit:junit:4.13")
}

dependencies {
  implementation(compose.desktop.currentOs)
  implementation("com.google.flogger:flogger:0.6")

  runtimeOnly("com.google.flogger:flogger-system-backend:0.6")

  testImplementation(kotlin("test-junit"))
  testImplementation("com.google.truth:truth:1.1.3")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "com.geekinasuit.demo.klife.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KLife"
            packageVersion = "1.0.0"
        }
    }
}

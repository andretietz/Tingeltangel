import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("org.jetbrains.compose")
}

val applicationVersion by extra("1.0.0")

group = "com.andretietz.tingeltangel"
version = applicationVersion

dependencies {
  implementation(compose.desktop.currentOs)
  implementation(compose.materialIconsExtended)

  implementation(project(":application"))
  implementation(project(":audiopen-bookii"))
  implementation(project(":audiopen-ting"))

  implementation("net.samuelcampos:usbdrivedetector:2.1.1")

  implementation(Dependencies.inject.dagger2.dagger)
  kapt(Dependencies.inject.dagger2.compiler)

  // logging
  implementation("org.apache.logging.log4j:log4j:2.13.0")
  implementation("org.slf4j:slf4j-log4j12:1.7.30")
  implementation("log4j:apache-log4j-extras:1.2.17")

  // testing
  testImplementation(Dependencies.okhttp.mockwebserver)
  testImplementation(Dependencies.test.coroutinesTest)
  testImplementation(Dependencies.test.junit)
  testImplementation(Dependencies.test.assertj)
  testImplementation(Dependencies.test.mockk)
}

compose.desktop {
  application {
    mainClass = "com.andretietz.tingeltangel.Application"

    nativeDistributions {
      targetFormats(TargetFormat.Msi, TargetFormat.Dmg)
      packageName = "tingeltangel"
      packageVersion = project.version as String
      description =
        "an application that downloads several sources of audiopen books and can transfer them to several targets"

      windows {
        menu = true
        shortcut = true
        iconFile.value { project.file("src/main/resources/images/icon.ico") }
        // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
        // see https://www.guidgen.com/
        upgradeUuid = "1859e70d-619d-4f34-98ee-5d2f3fce2de1"
      }

      macOS {
        iconFile.value { project.file("src/main/resources/images/icon.icns") }
      }

      linux {
        iconFile.value { project.file("src/main/resources/images/icon.png") }
      }
    }
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "16"
  kotlinOptions.freeCompilerArgs += "-Xuse-experimental=androidx.compose.ui.ExperimentalComposeUiApi"
//  kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.coroutines.FlowPreview"
//  kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutinesApi"
//  kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
//  kotlinOptions.freeCompilerArgs += "-Xuse-experimental=androidx.compose.animation.ExperimentalAnimationApi"
//  kotlinOptions.freeCompilerArgs += "-Xuse-experimental=androidx.compose.animation.ExperimentalCoroutinesApi"
}

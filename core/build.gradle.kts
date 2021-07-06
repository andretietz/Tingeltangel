plugins {
  kotlin("jvm")
}

dependencies {
  api(Dependencies.kotlin.coroutines)
  api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
  api(kotlin("reflect"))
  api(Dependencies.logging.slf4j.api)

  api("ch.qos.logback:logback-classic:1.2.3")
  api("ch.qos.logback:logback-core:1.2.3")


  testImplementation(Dependencies.test.junit)
  testImplementation(Dependencies.test.assertj)
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions.jvmTarget = "16"
}



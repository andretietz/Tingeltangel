plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":core"))
  api(Dependencies.kotlin.coroutines)
  api(kotlin("reflect"))

  api(Dependencies.okhttp.okhttp)

  testImplementation(Dependencies.okhttp.mockwebserver)
  testImplementation(Dependencies.test.coroutinesTest)

  testImplementation(Dependencies.test.junit)
  testImplementation(Dependencies.test.assertj)

}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions.jvmTarget = "16"
}



plugins {
  kotlin("jvm")
  kotlin("kapt")
}

dependencies {
  api(project(":core"))

  implementation(Dependencies.moshi.moshi)
  implementation(Dependencies.moshi.adapters)
  kapt(Dependencies.moshi.moshiCodegen)
  implementation("dev.zacsweers.moshix:moshi-sealed-runtime:0.8.0")
  kapt("dev.zacsweers.moshix:moshi-sealed-codegen:0.8.0")

  testImplementation(Dependencies.okhttp.mockwebserver)
  testImplementation(Dependencies.test.coroutinesTest)

  testImplementation(Dependencies.test.junit)
  testImplementation(Dependencies.test.assertj)

}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions.jvmTarget = "16"
}



plugins {
  kotlin("jvm")
  kotlin("kapt")
}

dependencies {
  api(project(":core"))
  api(Dependencies.kotlin.coroutines)
  api(kotlin("reflect"))

  implementation(Dependencies.retrofit.retrofit)
  implementation(Dependencies.retrofit.moshiConverter)
  implementation(Dependencies.moshi.moshi)
  kapt(Dependencies.moshi.moshiCodegen)


  api(Dependencies.okhttp.okhttp)
  api(Dependencies.okhttp.loggingInterceptor)

  testImplementation(Dependencies.okhttp.mockwebserver)
  testImplementation(Dependencies.test.coroutinesTest)

  testImplementation(Dependencies.test.junit)
  testImplementation(Dependencies.test.assertj)

}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions.jvmTarget = "16"
}



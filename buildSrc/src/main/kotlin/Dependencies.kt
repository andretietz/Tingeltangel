object Versions {
  const val kotlin = "1.5.10"
  const val coroutines = "1.5.0"
  const val junit = "4.13.2"
  const val retrofit = "2.9.0"
  const val okhttp = "4.9.1"
  const val moshi = "1.12.0"

  object inject {
    const val dagger = "2.37"
  }

  object test {
    const val mockito = "3.7.7"
    const val mockitoKotlin = "2.2.0"
    const val junit = "4.13.2"
    const val assertj = "3.19.0"
  }

  object logging {
    const val slf4j = "1.7.30"
  }

  object gradle {
    object plugin {
      const val android = "4.2.1"
      const val compose = "0.4.0"
      const val dokka = "1.4.32"
      const val mavenPublish = "0.14.1"
      const val versions = "0.28.0"
    }
  }
}

object Dependencies {
  const val ucanaccess = "net.sf.ucanaccess:ucanaccess:5.0.1"

  object kotlin {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesReactive = "org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${Versions.coroutines}"
  }

  object inject {
    object dagger2 {
      const val dagger = "com.google.dagger:dagger:${Versions.inject.dagger}"
      const val compiler = "com.google.dagger:dagger-compiler:${Versions.inject.dagger}"
    }
  }

  object okhttp {
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val mockwebserver = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp}"
    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
  }

  object retrofit {
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val moshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    const val cacheExtension = "com.andretietz.retrofit:cache-extension:1.0.0"
  }

  object moshi {
    const val moshi = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    const val adapters = "com.squareup.moshi:moshi-adapters:${Versions.moshi}"
    const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
  }

  object test {
    const val junit = "junit:junit:${Versions.test.junit}"
    const val mockito = "org.mockito:mockito-core:${Versions.test.mockito}"
    const val mockitoKotlin =
      "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.test.mockitoKotlin}"
    const val assertj = "org.assertj:assertj-core:${Versions.test.assertj}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
  }

  object logging {
    object slf4j {
      const val api = "org.slf4j:slf4j-api:${Versions.logging.slf4j}"
    }
  }

  object gradle {
    object plugin {
      const val versions =
        "com.github.ben-manes:gradle-versions-plugin:${Versions.gradle.plugin.versions}"
      const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
      const val compose =
        "org.jetbrains.compose:compose-gradle-plugin:${Versions.gradle.plugin.compose}"
      const val dokka = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.gradle.plugin.dokka}"
      const val mavenPublish =
        "com.vanniktech:gradle-maven-publish-plugin:${Versions.gradle.plugin.mavenPublish}"
    }
  }
}

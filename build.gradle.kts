buildscript {
  repositories {
    google()
    maven("https://plugins.gradle.org/m2/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenCentral()
  }

  dependencies {
    // __LATEST_COMPOSE_RELEASE_VERSION__
    classpath("org.jetbrains.compose:compose-gradle-plugin:0.4.0")
    // legacy reasons? it's generating a tiptoi parser. not used atm
//    classpath("org.anarres.gradle:gradle-sablecc-plugin:1.0.5")

    // __KOTLIN_COMPOSE_VERSION__
    classpath(kotlin("gradle-plugin", version = Versions.kotlin))

  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
}

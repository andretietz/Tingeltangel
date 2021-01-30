package com.andretietz.tingeltangel

import org.kodein.di.Kodein

object DIHelper {
  var kodein: Kodein? = null
  fun initKodein(kodein: Kodein) {
    DIHelper.kodein = kodein
  }
}

fun component(): Kodein {
  return DIHelper.kodein ?: throw IllegalStateException("Please call DIHelper.initKodein() !")
}

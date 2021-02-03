package com.andretietz.audiopen.explode

object CodePositionHelper {


  /**
   * @return File position from code.
   */
  internal fun getPositionFromCode(code: Int, n: Int): Int {
    if (((code and 0xFF) != 0) || (n < 0)) throw IllegalArgumentException()

    val co = code shr 8 // 0x00F0 -> 0x000F
    val c = co shr 3 and 1 or
      (co shr 4 and 1 shl 1) or
      (co shr 5 and 1 shl 2) or
      (co shr 7 and 1 shl 3) or
      (co shr 9 and 1 shl 4)
    val a = co - (((n - 1) * 26) - LOOKUP_TABLE[c])

    return a shl 8 // 0x000F -> 0x00F0
  }

  /**
   * @return Code from file position.
   */
  internal fun getCodeFromPosition(position: Int, n: Int): Int {
    if (((position and 0xFF) != 0) || (n < 0))
      throw IllegalArgumentException(
        "Invalid arguments: position: 0x${Integer.toHexString(position)} n: $n"
      )
    val b = (position shr 8) + (n - 1) * 26
    for (k in LOOKUP_TABLE.indices) {
      val v: Int = b - LOOKUP_TABLE[k] shl 8
      if (getPositionFromCode(v, n) == position) {
        return v
      }
    }
    throw IllegalArgumentException()
  }

  /**
   * I have no clue how this numbers come together. It just works. Thanks to Martin.
   */
  private val LOOKUP_TABLE = arrayOf(
    // @formatter:off
    578, 562, 546, 530, 514, 498, 482, 466, 322, 306, 290,
    274, 258, 242, 226, 210, -446, -462, -478, -494, -510,
    -526, -542, -558, -702, -718, -734, -750, -766, -782, -798, -814
    // @formatter:on
  )
}

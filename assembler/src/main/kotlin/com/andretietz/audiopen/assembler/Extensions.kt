package com.andretietz.audiopen.assembler

import java.io.ByteArrayInputStream

/**
 * Reads an 2 Bytes from a [ByteArrayInputStream] and transforms it into an integer.
 *
 * @return Integer created out of 2 bytes in the stream.
 */
@SuppressWarnings("Detekt.MagicNumber", "Detekt.UnnecessaryParentheses")
internal fun ByteArrayInputStream.readPair() = (read() and 0xFF shl 8) or (read() and 0xFF)

package com.andretietz.audiopen.assembler.script

import java.io.ByteArrayInputStream

class OufScriptDisassembler {
  /**
   * decodes a file into a script.
   */
  fun disassembleScript(input: ByteArrayInputStream): String {
    input.use { stream -> return createScript(stream, collectLabels(stream)) }
  }

  private fun collectLabels(stream: ByteArrayInputStream): Map<Int, Int> {
    val labels = mutableMapOf<Int, Int>()
    var currentLabel = 0
    do {
      val code = stream.readPair()
      val command = Command.find(code)
        ?: throw IllegalStateException("Unknown OpCode 0x${Integer.toHexString(code)}")
      if (command.isJump) {
        labels.putIfAbsent(stream.readPair(), ++currentLabel)
      } else {
        stream.skip(command.argumentCount * 2L) // skipping argument bytes (2 bytes per argument)
      }
    } while (stream.available() > 1)
    if (stream.available() != 1 || stream.read() != 0) {
      throw IllegalStateException("Missing end operator 0x0!")
    }
    stream.reset()
    return labels
  }

  private fun createScript(stream: ByteArrayInputStream, labels: Map<Int, Int>): String {

    val length = stream.available()
    val sb = StringBuilder()
    while (stream.available() > 1) {
      labels[length - stream.available()]?.let {
        sb.appendLine("\n:l${it}")
      }
      val command = Command.find(stream.readPair())!! // has been checked in collectJumpTargets already
      sb.append(command.asm)
      sb.append(command.disassemble(stream, labels))
    }
    return sb.toString()
  }
}

@SuppressWarnings("Detekt.MagicNumber", "Detekt.UnnecessaryParentheses")
internal fun ByteArrayInputStream.readPair() = (read() and 0xFF shl 8) or (read() and 0xFF)

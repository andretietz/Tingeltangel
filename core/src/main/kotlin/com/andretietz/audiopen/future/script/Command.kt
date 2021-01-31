package com.andretietz.audiopen.future.script

import java.io.ByteArrayInputStream

/**
 * All known commands.
 */
data class Command(
  val code: Int,
  val method: String, // not sure what to use this for?
  val asm: String,
  val sourcePrefix: String? = null,
  val targetPrefix: String? = null
) {

  companion object {
    private const val LABEL = " l"
    private const val REGISTER = " v"
    private const val VALUE = " "

    private val ALL = arrayOf(
      // end of program
      Command(0x0000, "end", "end"),
      // returns from a subroutine
      Command(0x1400, "return", "return"),
      // clear all variables
      Command(0x0100, "clearver", "clearver"),

      // jump to label
      Command(0x0800, "jmp", "jmp", LABEL),
      // jump if equal
      Command(0x0900, "je", "je", LABEL),
      // jump if not equal
      Command(0x0A00, "jne", "jne", LABEL),
      // jump if greater
      Command(0x0B00, "jg", "jg", LABEL),
      // jump if greater or equal
      Command(0x0C00, "jge", "jge", LABEL),
      // jump if lower
      Command(0x0D00, "jb", "jb", LABEL),
      // jump if lower or equal
      Command(0x0E00, "jbe", "jbe", LABEL),

      // selects an id, where id is a value
      Command(0x1501, "callidV", "callid", VALUE),
      // calls a subroutine
      Command(0xFFFF, "call", "call", VALUE),
      // plays an object id, where id is a value
      Command(0x1601, "playoidV", "playoid", VALUE),
      // pauses for x tenth of a second, where x is a numeric value
      Command(0x1701, "pauseV", "pause", VALUE),

      // plays an object id, where id is a register
      Command(0x1602, "playoidR", "playoid", REGISTER),
      // selects an id, where id is a register
      Command(0x1502, "callidR", "callid", REGISTER),
      // pauses for x tenth of a second, where x is a register containing a numeric value
      Command(0x1702, "pauseR", "pause", REGISTER),

      // sets a register to a given value
      Command(0x0201, "setV", "set", REGISTER, VALUE),
      // compares a register with a value
      Command(0x0301, "cmpV", "cmp", REGISTER, VALUE),
      // and-operation on register and value
      Command(0x0401, "andV", "and", REGISTER, VALUE),
      // or-operation on register and value
      Command(0x0501, "orV", "or", REGISTER, VALUE),
      // adds a value to a register
      Command(0x0F01, "addV", "add", REGISTER, VALUE),
      // subtracts a value from a register
      Command(0x1001, "subV", "sub", REGISTER, VALUE),

      // copies a register into another one
      Command(0x0202, "setR", "set", REGISTER, REGISTER),
      // compares 2 registers
      Command(0x0302, "cmpR", "cmp", REGISTER, REGISTER),
      // and-operation on 2 registers
      Command(0x0401, "andR", "and", REGISTER, REGISTER),
      // or-operation on 2 registers
      Command(0x0502, "orR", "or", REGISTER, REGISTER),

      // negation of a register. Note for some weird reason the second register will be ignored
      Command(0x0602, "not", "not", REGISTER, REGISTER),
      // adds a register to another one
      Command(0x0F02, "addR", "add", REGISTER, REGISTER),
      // subtracts a register from another one
      Command(0x1002, "subR", "sub", REGISTER, REGISTER)
    )

    fun find(opcode: Int) = ALL.firstOrNull { it.code == opcode }
  }

  /**
   * number of arguments of this command.
   */
  // @formatter:off
    val argumentCount: Int = if (targetPrefix != null) { 1 } else { 0 } + if (sourcePrefix != null) { 1 } else { 0 }
    // @formatter:on

  /**
   * returns [true] if this command is a jump command.
   */
  val isJump: Boolean = sourcePrefix == LABEL

  fun disassemble(stream: ByteArrayInputStream, labels: Map<Int, Int>): String {
    return when (argumentCount) {
      1 -> if (LABEL == sourcePrefix) {
        "$sourcePrefix${labels[stream.readPair()]}\n"
      } else {
        "$sourcePrefix${stream.readPair()}\n"
      }
      2 -> "${sourcePrefix}${stream.readPair()},${targetPrefix!!}${stream.readPair()}\n"
      else -> '\n'.toString()
    }
  }
}

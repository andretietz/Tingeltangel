package com.andretietz.audiopen.explode

import com.andretietz.audiopen.LoggerDelegate
import com.andretietz.audiopen.data.BookData
import com.andretietz.audiopen.data.BookDataItem
import com.andretietz.audiopen.explode.script.Command

class OufAssembler(private val bookData: BookData) {

  private var labelCount = 0

  fun assemble() {
    labelCount = 0
  }

  fun collectRegisters(script: String): Set<Int> {
    script.reader().useLines { sequence ->
      return sequence
        // trim every line
        .map { it.trim().toLowerCase() }
        // remove inline comments
        .map { line ->
          val idx = line.indexOf(SCRIPT_COMMENT, 1)
          if (idx >= 0) line.substring(0, idx) else line
        }
        .mapNotNull { line ->
          val p: Int = line.indexOf(' ')
          if (p != -1) {
            val args = line.substring(p).trim()
            return@mapNotNull args.split(',').toTypedArray()
              .map { it.trim().toLowerCase() }
              .filter { it.startsWith('v') }
              .map { arg -> arg.substring(1).toInt() }
          }
          null
        }.flatten().toSet()
    }
  }

  fun mergeOnCall(script: String, subCall: Boolean): String {
    labelCount++
    val returnLabel = "return_$labelCount"
    val labelPrefix = "sub_${labelCount}_"
    val sb = StringBuilder()
    script.reader().useLines { sequence ->
      sequence
        // trim every line
        .map { it.trim().toLowerCase() }
        // remove inline comments
        .map { line ->
          val idx = line.indexOf(SCRIPT_COMMENT, 1)
          if (idx >= 0) line.substring(0, idx) else line
        }
        .forEachIndexed { _, row ->
          // skip line: This cannot be filtered before, due to the row count
          if (row.isEmpty() && row.startsWith(SCRIPT_COMMENT)) return@forEachIndexed
          var p = row.indexOf(" ")
          var jump = false
          if (p != -1) {
            jump = Command.find(row.substring(0, p))?.isJump ?: false
          }
          when {
            row.startsWith(':') -> sb.appendLine(":$labelPrefix${row.substring(1)}")
            jump -> {
              p = row.indexOf(" ")
              val cmd: String = row.substring(0, p)
              sb.appendLine("$cmd $labelPrefix${row.substring(p + 1)}")
            }
            row.startsWith("$SCRIPT_CALL ") -> {
              val oid: Int = row.substring(SCRIPT_CALL.length).trim().toInt()
              val subItem = bookData.data
                .filterIsInstance<BookDataItem.Script>()
                .firstOrNull { it.code == oid }
                ?: throw IllegalStateException()
              val subCode = mergeOnCall(subItem.script, true)
              sb.append(subCode)
            }
            row == SCRIPT_RETURN -> sb.appendLine(if (subCall) "jmp $returnLabel" else SCRIPT_RETURN)
            else -> sb.appendLine(row)
          }
        }
    }
    sb.appendLine(":$returnLabel")
    return sb.toString()
  }

  companion object {
    private val logger by LoggerDelegate()

    private const val SCRIPT_RETURN = "return"
    private const val SCRIPT_CALL = "call"
    private const val SCRIPT_COMMENT = "//"
  }
}

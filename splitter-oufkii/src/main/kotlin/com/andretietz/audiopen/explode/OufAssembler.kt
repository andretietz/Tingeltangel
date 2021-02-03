package com.andretietz.audiopen.explode

import com.andretietz.audiopen.LoggerDelegate
import com.andretietz.audiopen.data.BookData
import com.andretietz.audiopen.data.BookDataItem
import com.andretietz.audiopen.explode.script.Command
import java.io.File
import java.util.Date
import kotlin.math.max

class OufAssembler(private val bookData: BookData) {

  private var labelCount = 0

  fun assemble(target: File) {
    if (target.exists())
      throw IllegalArgumentException("File: $target exists already!")
    if (!target.parentFile.exists() || !target.parentFile.isDirectory)
      throw IllegalArgumentException("Directory: ${target.parentFile} doesn't exist")
    if (!target.createNewFile()) throw IllegalArgumentException("Cannot create file: $target")

    labelCount = 0
    target.outputStream().use { out ->
      val lastID: Int = bookData.data.maxByOrNull { it.code }?.code ?: throw IllegalArgumentException()
//      val size = lastID - 15000
      val size = bookData.data.size

      // write header
      out.write(INDEX_TABLE_START)
      out.write(MAGIC_VALUE1_START)
      out.write(OID_COUNT_START)
      out.write(lastID)
      out.write(size)
      out.write(bookData.id)
      out.write(MAGIC_VALUE2_START)
      out.write(Date().time.toInt()) // TODO
      out.write(HEADER_ENDING_ONE)
      out.write(HEADER_ENDING_TWO)

      // pad with zeros
      out.write(ByteArray(64) { 0x00 })

      // write index table
      var pos: Int = INDEX_TABLE_START + 12 * size
      bookData.data
//        .sortedBy { it.code }
        .forEachIndexed { index, entry ->
//        if(entry == null) // TODO: figure out how this can happen? Book.java@698
//        out.write(ByteArray(6) { 0x00 }) // TODO: wtf???

          pos = nextAddress(pos)
          val code = CodePositionHelper.getCodeFromPosition(pos, index)
          out.write(code)
          out.write(max(entry.size, 0))
          logger.debug(
            "${(index + INDEX_TABLE_START)} " +
              "@0x${Integer.toHexString(pos)} " +
              "code=0x${Integer.toHexString(code)} " +
              "size=${entry.size}"
          )
          when (entry) {
            is BookDataItem.MP3 -> out.write(BookDataItem.TYPE_AUDIO)
            is BookDataItem.Script -> out.write(BookDataItem.TYPE_SCRIPT)
          }
          pos += max(entry.size, 0)
        }

      pos = INDEX_TABLE_START + 12 * size

      bookData.data // TBD: check if it's always in order
        .forEach { entry ->
          val pad = nextAddress(pos)
          pos += pad
          out.write(ByteArray(pad) { 0x00 })
          when (entry) {
            is BookDataItem.MP3 -> entry.file.inputStream().use { input -> input.copyTo(out) }
            is BookDataItem.Script -> out.write(compileScript(entry.script))
          }
          pos += entry.size
        }
    }
  }

  private fun nextAddress(r: Int): Int {
    var x = r + 0x100 - (r and 0xff)
    while (x % 0x200 != 0) {
      x += 0x100
    }
    return x
  }

  private fun compileScript(code: String): ByteArray {
    val finalCode = replaceTemplatesAndResolveNames(mergeOnCall(code, false))
    return ByteArray(1)// TODO
  }

  private fun collectRegisters(script: String): Set<Int> {
    script.reader().useLines { sequence ->
      return sequence
        .mapNotNull { line ->
          val pureLine = line.removeCommentsAndTrim() ?: return@mapNotNull null
          val p: Int = pureLine.indexOf(' ')
          if (p != -1) {
            val args = pureLine.substring(p).trim()
            return@mapNotNull args.split(',').toTypedArray()
              .map { it.trim().toLowerCase() }
              .filter { it.startsWith('v') }
              .map { arg -> arg.substring(1).toInt() }
          }
          null
        }.flatten().toSet()
    }
  }

  private fun mergeOnCall(script: String, subCall: Boolean): String {
    labelCount++
    val returnLabel = "return_$labelCount"
    val labelPrefix = "sub_${labelCount}_"
    val sb = StringBuilder()
    script.reader().useLines { sequence ->
      sequence.forEach { raw ->
        logger.info("processing line: $raw")
        val line = raw.removeCommentsAndTrim() ?: return@forEach
        // check if command is a jump
        val cmd = line.substringBefore(' ', "")
        val jump = Command.find(cmd)?.isJump ?: false
        when {

          line.startsWith(':') -> sb.appendLine(":$labelPrefix${line.substring(1)}")

          jump -> sb.appendLine("$cmd $labelPrefix${line.substringAfter(" ")}")

          line.startsWith("$SCRIPT_CALL ") -> {
            val oid: Int = line.substring(SCRIPT_CALL.length).trim().toInt()
            val subItem = bookData.data
              .filterIsInstance<BookDataItem.Script>()
              .firstOrNull { it.code == oid }
              ?: throw IllegalStateException()
            val subCode = mergeOnCall(subItem.script, true)
            sb.append(subCode)
          }

          line == SCRIPT_RETURN -> sb.appendLine(if (subCall) "jmp $returnLabel" else SCRIPT_RETURN)

          else -> sb.appendLine(line)
        }
      }
    }
    sb.appendLine(":$returnLabel")
    return sb.toString()
  }

  private fun replaceTemplatesAndResolveNames(script: String) : String {
    val usedRegs = bookData.data
      .filterIsInstance<BookDataItem.Script>()
      .map { collectRegisters(it.script) }
      .flatten().toSet()
    return ""
  }

  /*
      private String replaceTemplatesAndResolveNames(String code) throws IOException, SyntaxError {
        BufferedReader in = new BufferedReader(new StringReader(code));
        StringBuilder out = new StringBuilder();

        Book book = entry.getBook();
        HashSet<Integer> usedRegs = new HashSet<Integer>();
        if(book != null) {
            usedRegs = book.getAllUsedRegisters();
        }

        HashSet<Integer> registersUsedByTemplate = new HashSet<Integer>();

        String row;
        Pattern pattern = Pattern.compile("\\s");
        while((row = in.readLine()) != null) {
            row = row.trim().toLowerCase();
            String args = "";
            Matcher matcher = pattern.matcher(row);
            if(matcher.find()) {
            	int p = matcher.start();
                args = row.substring(p + 1).trim();
                row = row.substring(0, p);
            }

            // resolve names
            String[] _as = args.split(",");
            args = "";
            for(int i = 0; i < _as.length; i++) {
                _as[i] = _as[i].trim();
                if(_as[i].startsWith("@")) {
                    Entry e = book.getEntryByName(_as[i].substring(1));
                    if(e == null) {
                        throw new SyntaxError("OID Name '" + _as[i].substring(1) + "' nicht gefunden");
                    }
                    args += "," + Integer.toString(e.getTingID());
                } else {
                    args += "," + _as[i];
                }
            }
            args = args.substring(1);

            Template t = Template.getTemplate(row);
            if(t != null) {
                LinkedList<String> as = new LinkedList<String>();
                if(!args.isEmpty()) {
                    _as = args.split(",");
                    for(int i = 0; i < _as.length; i++) {
                        as.add(_as[i].toLowerCase().trim());
                    }
                }

                for(int r = 0; r <= Emulator.getMaxBasicRegister(); r++) {
                    if(!usedRegs.contains(r)) {
                        registersUsedByTemplate.add(r);
                    }
                }
                out.append(t.getCode(as, registersUsedByTemplate));
            } else {
                out.append(row);
                if(!args.isEmpty()) {
                    out.append(" ").append(args);
                }
                out.append("\n");
            }
        }

        return(out.toString());
    }
   */

  companion object {
    private val logger by LoggerDelegate()

    private const val SCRIPT_RETURN = "return"
    private const val SCRIPT_CALL = "call"
    internal const val SCRIPT_COMMENT = "//"

    private const val INDEX_TABLE_START = 0x0068
    private const val MAGIC_VALUE1_START = 0x0002
    private const val MAGIC_VALUE2_START = 0x000b
    private const val HEADER_ENDING_ONE = 0x0000
    private const val HEADER_ENDING_TWO = 0xffff
    private const val OID_COUNT_START = 15001

  }
}

/**
 * * Removes inline comments e.g.: "foo, bar // this is a comment."
 * * trims the string.
 * * lowercases the string.
 */
internal fun String.removeCommentsAndTrim(): String? {
  return substringBefore(OufAssembler.SCRIPT_COMMENT).trim().toLowerCase().takeIf { it.isNotEmpty() }
}

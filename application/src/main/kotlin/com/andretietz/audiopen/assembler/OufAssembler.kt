package com.andretietz.audiopen.assembler

import com.andretietz.audiopen.LoggerDelegate
import com.andretietz.audiopen.data.Book
import java.io.OutputStream

class OufAssembler(private val book: Book) {

  private var labelCount = 0

//  fun assemble(target: File) {
//    if (target.exists())
//      throw IllegalArgumentException("File: $target exists already!")
//    if (!target.parentFile.exists() || !target.parentFile.isDirectory)
//      throw IllegalArgumentException("Directory: ${target.parentFile} doesn't exist")
//    if (!target.createNewFile()) throw IllegalArgumentException("Cannot create file: $target")
//
//    labelCount = 0
//    target.outputStream().use { out ->
////      val size = lastID - 15000
//      val size = bookData.data.size
//      writeHeader(out, size, Date().time.toInt()) // TODO: date
//
//      val scripts = mutableMapOf<Int, ByteArray>()
//      // write index table
//      var pos: Int = INDEX_TABLE_START + 12 * size
//      bookData.data
////        .sortedBy { it.code }
//        .forEachIndexed { index, entry ->
////        if(entry == null) // TODO: figure out how this can happen? Book.java@698
////        out.write(ByteArray(6) { 0x00 }) // TODO: wtf???
//
//          pos = nextAddress(pos)
//          val code = CodePositionHelper.getCodeFromPosition(pos, index)
//          out.write(code)
//
////          logger.debug(
////            "${(index + INDEX_TABLE_START)} " +
////              "@0x${Integer.toHexString(pos)} " +
////              "code=0x${Integer.toHexString(code)} " +
////              "size=${entry.size}"
////          )
//          val dataSize = when (entry) {
//            is BookDataItem.MP3 -> {
//              out.write(entry.file.length().toInt())
//              out.write(BookDataItem.TYPE_AUDIO)
//              entry.file.length().toInt()
//            }
//            is BookDataItem.Script -> {
//              scripts.putIfAbsent(code, compileScript(entry.script))
//              out.write(scripts[code]!!.size)
//              out.write(BookDataItem.TYPE_SCRIPT)
//              scripts[code]!!.size
//            }
//          }
//          pos += max(dataSize, 0)
//        }
//
//      pos = INDEX_TABLE_START + 12 * size
//
//      bookData.data // TBD: check if it's always in order
//        .forEach { entry ->
//          val pad = nextAddress(pos)
//          pos += pad
//          out.write(ByteArray(pad) { 0x00 })
//          pos += when (entry) {
//            is BookDataItem.MP3 -> {
//              entry.file.inputStream().use { input -> input.copyTo(out) }
//              entry.file.length().toInt()
//
//            }
//            is BookDataItem.Script -> {
//              out.write(compileScript(entry.script))
//              scripts[entry.code]!!.size
//            }
//          }
//        }
//    }
//  }

  internal fun writeHeader(output: OutputStream, size: Int, date: Int) {
    val lastID: Int = book.data.maxByOrNull { it.code }?.code ?: throw IllegalArgumentException()
    // write header
    output.write(INDEX_TABLE_START)
    output.write(MAGIC_VALUE1_START)
    output.write(OID_COUNT_START)
    output.write(lastID)
    output.write(size)
    output.write(book.id)
    output.write(MAGIC_VALUE2_START)
    output.write(date)
    output.write(HEADER_ENDING_ONE)
    output.write(HEADER_ENDING_TWO)

    // pad with zeros
    output.write(ByteArray(64) { 0x00 })
  }

  private fun nextAddress(address: Int): Int {
    var x = address + 0x100 - (address and 0xff)
    while (x % 0x200 != 0) {
      x += 0x100
    }
    return x
  }

//  private fun compileScript(code: String): ByteArray {
//    val finalCode = replaceTemplatesAndResolveNames(mergeOnCall(code, false))
//    return ByteArray(1)// TODO
//  }

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

//  internal fun mergeOnCall(script: String, isSubCall: Boolean): String {
//    labelCount++
//    val returnLabel = "return_$labelCount"
//    val labelPrefix = "sub_${labelCount}_"
//    val sb = StringBuilder()
//    script.reader().useLines { sequence ->
//      sequence.forEach { raw ->
//        logger.info("processing line: $raw")
//        val line = raw.removeCommentsAndTrim() ?: return@forEach
//        // check if command is a jump
//        val cmd = line.substringBefore(' ', "")
//        val jump = Command.find(cmd)?.isJump ?: false
//        when {
//
//          line.startsWith(':') -> sb.appendLine(":$labelPrefix${line.substring(1)}")
//
//          jump -> sb.appendLine("$cmd $labelPrefix${line.substringAfter(" ")}")
//
//          line.startsWith("$SCRIPT_CALL ") -> {
//            val oid: Int = line.substring(SCRIPT_CALL.length).trim().toInt()
//            val subItem = bookData.data
//              .filterIsInstance<BookDataItem.Script>()
//              .firstOrNull { it.code == oid }
//              ?: throw IllegalStateException()
//            val subCode = mergeOnCall(subItem.script, true)
//            sb.append(subCode)
//          }
//
//          line == SCRIPT_RETURN -> sb.appendLine(if (isSubCall) "jmp $returnLabel" else SCRIPT_RETURN)
//
//          else -> sb.appendLine(line)
//        }
//      }
//    }
//    sb.appendLine(":$returnLabel")
//    return sb.toString()
//  }

  private fun replaceTemplatesAndResolveNames(script: String): String {
//    val usedRegs = bookData.data
//      .filterIsInstance<BookDataItem.Script>()
//      .map { collectRegisters(it.script) }
//      .flatten().toSet()

    return script.reader().buffered().useLines { sequence ->
      sequence.map { it.trim().toLowerCase() }
        .map { line ->
          val args = line.substringAfter(' ').split(",").map { it.trim() }
          val row = line.substringBefore(' ').trim()

          val targetArgs = StringBuilder()
          args.forEach { argument ->
            if (argument.startsWith('@')) {
              val item = book.data.firstOrNull {
                // it.name == argument.substring(1)
                false// TODO get item by name
              }
                ?: throw SyntaxError("OID Name: '${argument.substring(1)}' nicht gefunden")
              targetArgs.append(",${item.code}")
            } else {
              targetArgs.append(",$argument")
            }
          }
          val arguments = targetArgs.toString().substring(1)
          val out = StringBuilder()
          // TODO: template handling: if(row == template) ... else:
          out.append(row)
          if (arguments.isNotEmpty()) {
            out.append(" $arguments")
          }
          out.appendLine()
          out.toString()
        }.joinToString()
    }
  }


  /*
                  out.append(row);
                if(!args.isEmpty()) {
                    out.append(" ").append(args);
                }
                out.append("\n");

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
 * * Removes comments e.g.: "foo, bar // this is a comment." or "// foo"
 * * trims the string.
 * * lowercases the string.
 * @return the cleared string or `null` if the string was a comment only
 */
internal fun String.removeCommentsAndTrim(): String? {
  return substringBefore(OufAssembler.SCRIPT_COMMENT).trim().toLowerCase().takeIf { it.isNotEmpty() }
}

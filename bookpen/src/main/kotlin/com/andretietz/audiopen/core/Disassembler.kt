package com.andretietz.audiopen.core

import java.io.ByteArrayInputStream

class Disassembler {
    /**
     * decodes a file into a script.
     */
    fun disassemble(input: ByteArrayInputStream): String {
        input.use { stream -> return createScript(stream, collectLabels(stream)) }
    }

    internal fun collectLabels(stream: ByteArrayInputStream): Map<Int, Int> {
        val labels = mutableMapOf<Int, Int>()
        var currentLabel = 1
        var lastOpCode = -1
        do {
            val code = stream.readPair()
            val command = Command.find(code)
                ?: throw IllegalStateException("Unknown OpCode ${Integer.toHexString(code)}")
            if (command.sourcePrefix == Command.LABEL) {
                labels.putIfAbsent(stream.readPair(), currentLabel) ?: currentLabel++
            } else {
                stream.skip(command.argumentCount * 2L) // skipping argument bytes (2 bytes per argument)
            }
            lastOpCode = code
        } while (stream.available() > 1)
        if (lastOpCode != 0) {
            throw IllegalStateException("Missing end operator!")
        }
        stream.reset()
        return labels
    }

    internal fun createScript(stream: ByteArrayInputStream, labels: Map<Int, Int>): String {
        // TODO get rid of offset! seems weird
        var offset = 0
        val length = stream.available()
        val sb = StringBuilder()
        while (offset < length) {
            if (labels.containsKey(offset)) {
                sb.append("\n:l${labels[offset]}\n")
            }
            val command = Command.find(stream.readPair())!! // has been checked in collectJumpTargets already
            sb.append(command.asm)
            offset += 2
            when (command.argumentCount) {
                0 -> {
                    sb.append('\n')
                }
                1 -> command.sourcePrefix?.let {
                    if (Command.LABEL == it) {
                        sb.append(" l${labels[stream.readPair()]}\n")
                    } else {
                        sb.append("$it${stream.readPair()}\n")
                    }
                    offset += 2
                }
                2 -> {
                    if(command == Command.BinaryNegRegister) {
                        sb.append("${command.sourcePrefix!!}${stream.readPair()}}\n")
                        stream.readPair()
                    } else {
                        sb.append("${command.sourcePrefix!!}${stream.readPair()},${command.targetPrefix!!}${stream.readPair()}\n")
                    }
                    offset += 4
                }
            }
            offset += command.argumentCount * 2
        }
        return sb.toString()
    }
}

internal fun ByteArrayInputStream.readPair(): Int {
    val l = read()
    val r = read()
    val left = l and 0xFF shl 8
    val right = r and 0xFF
    return left or right
}

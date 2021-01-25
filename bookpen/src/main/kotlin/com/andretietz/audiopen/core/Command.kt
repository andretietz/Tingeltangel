package com.andretietz.audiopen.core

/**
 * All known commands.
 */
sealed class Command(
    val code: Int,
    val method: String,
    val asm: String,
    val description: String,
    val sourcePrefix: String? = null,
    val targetPrefix: String? = null
) {
    object ProgramEnd : Command(0x0000, "end", "end", "end of program")
    object Return : Command(0x1400, "return", "return", "returns from a subroutine")
    object ClearVar : Command(0x0100, "clearver", "clearver", "clear all variables")


    object SetRegister : Command(0x0201, "setV", "set", "sets a register to a given value", REGISTER, VALUE)
    object CompareRegisterValue : Command(0x0301, "cmpV", "cmp", "compares a register with a value", REGISTER, VALUE)
    object BinaryAndRegisterValue :
        Command(0x0401, "andV", "and", "and-operation on register and value", REGISTER, VALUE)

    object BinaryOrRegisterValue : Command(0x0501, "orV", "or", "or-operation on register and value", REGISTER, VALUE)
    object AddValue : Command(0x0F01, "addV", "add", "adds a value to a register", REGISTER, VALUE)
    object SubValue : Command(0x1001, "subV", "sub", "subtracts a value from a register", REGISTER, VALUE)

    object CopyRegister : Command(0x0202, "setR", "set", "copies a register into another one", REGISTER, REGISTER)
    object CompareRegisters : Command(0x0302, "cmpR", "cmp", "compares 2 registers", REGISTER, REGISTER)
    object BinaryAndRegister : Command(0x0401, "andR", "and", "and-operation on 2 registers", REGISTER, REGISTER)
    object BinaryOrRegister : Command(0x0502, "orR", "or", "or-operation on 2 registers", REGISTER, REGISTER)

    // TODO: unclear why 2 registers
    object BinaryNegRegister : Command(0x0602, "not", "not", "negation of a register", REGISTER, REGISTER)
    object AddRegister : Command(0x0F02, "addR", "add", "adds a register to another one", REGISTER, REGISTER)
    object SubRegister : Command(0x1002, "subR", "sub", "subtracts a register from another one", REGISTER, REGISTER)


    object JumpTo : Command(0x0800, "jmp", "jmp", "jump to label", LABEL)
    object JumpEqual : Command(0x0900, "je", "je", "jump if equal", LABEL)
    object JumpNEqual : Command(0x0A00, "jne", "jne", "jump if not equal", LABEL)
    object JumpGreater : Command(0x0B00, "jg", "jg", "jump if greater", LABEL)
    object JumpGreaterEqual : Command(0x0C00, "jge", "jge", "jump if greater or equal", LABEL)
    object JumpLower : Command(0x0D00, "jb", "jb", "jump if lower", LABEL)
    object JumpLowerEqual : Command(0x0E00, "jbe", "jbe", "jump if lower or equal", LABEL)


    object CallIdValue : Command(0x1501, "callidV", "callid", "selects an id, where id is a value", VALUE)
    object CallSubroutine : Command(0xFFFF, "call", "call", "calls a subroutine", VALUE)
    object PlayObjectIdValue : Command(0x1601, "playoidV", "playoid", "plays an object id, where id is a value", VALUE)
    object PauseValue :
        Command(0x1701, "pauseV", "pause", "pauses for x tenth of a second, where x is a numeric value", VALUE)

    object PlayObjectIdRegister :
        Command(0x1602, "playoidR", "playoid", "plays an object id, where id is a register", REGISTER)

    object CallIdRegister : Command(0x1502, "callidR", "callid", "selects an id, where id is a register", REGISTER)
    object PauseRegister : Command(
        0x1702,
        "pauseR",
        "pause",
        "pauses for x tenth of a second, where x is a register containing a numeric value",
        REGISTER
    )

    companion object {
        const val LABEL = " l"
        const val REGISTER = " v"
        const val VALUE = " "

        fun find(opcode: Int): Command? {
            return Command::class.sealedSubclasses
                .mapNotNull { it.objectInstance }
                .firstOrNull { it.code == opcode }
        }
    }

    val argumentCount: Int = if (targetPrefix != null) {
        1
    } else {
        0
    } + if (sourcePrefix != null) {
        1
    } else {
        0
    }


}

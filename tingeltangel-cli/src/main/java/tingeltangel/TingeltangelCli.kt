package tingeltangel

import tingeltangel.cli_ng.CLI

object TingeltangelCli {
    @JvmStatic
    fun main(args: Array<String>) {
        Tingeltangel.initialize()
        CLI.init()
        CLI.run(
            if (args.isNotEmpty()) {
                args[0]
            } else ""
        )
    }
}

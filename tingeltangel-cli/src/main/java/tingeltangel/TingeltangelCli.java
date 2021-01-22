package tingeltangel;

import tingeltangel.cli_ng.CLI;

public class TingeltangelCli {
    public static void main(String[] args) {
        Tingeltangel.initialize();
        CLI.init();
        if (args.length > 0) {
            CLI.run(args[0]);
        } else {
            CLI.run("");
        }
    }
}

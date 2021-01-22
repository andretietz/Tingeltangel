package tingeltangel;

import tingeltangel.core.Repository;
import tingeltangel.gui.ManagerFrame;

import javax.swing.*;

public class Manager {
    public static void main(String[] args) {
        Tingeltangel.initialize();
        boolean doInitialUpdate = args.length > 1 && args[1].toLowerCase().equals("disable-official-books");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if ((Repository.getIDs().length == 0) && doInitialUpdate) {
                    // do not download repository just start tool directly
                    startGUI();
                        /*
                        try {
                            Repository.initialUpdate(new Thread() {
                                @Override
                                public void run() {
                                    startGUI(_startEditor);
                                }
                            });
                        } catch (IOException ex) {
                            log.warn("initial update failed", ex);
                            startGUI(_startEditor);
                        }
                        */

                } else {
                    startGUI();
                }
            }
        });
    }

    private static void startGUI() {
        new ManagerFrame();
    }
}

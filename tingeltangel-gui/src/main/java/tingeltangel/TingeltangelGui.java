package tingeltangel;

import tingeltangel.core.Codes;
import tingeltangel.core.Properties;
import tingeltangel.core.Repository;
import tingeltangel.gui.EditorFrame;

import javax.swing.*;

import static tingeltangel.core.constants.CodePreferenceConstants.PROPERTY_RESOLUTION;


public class TingeltangelGui {
    public static void main(String[] args) {
        Tingeltangel.initialize();
        // TODO: move this out of this class!
        // set resolution
        if (Properties.getStringProperty(PROPERTY_RESOLUTION).equals("1200")) {
            Codes.setResolution(Codes.DPI1200);
        } else {
            Codes.setResolution(Codes.DPI600);
        }

        Codes.loadProperties();
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
        new EditorFrame();
    }
}

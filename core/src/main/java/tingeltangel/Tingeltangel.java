/*
    Copyright (C) 2015   Martin Dames <martin@bastionbytes.de>
<<<<<<< HEAD:tingeltangel/src/main/java/tingeltangel/Tingeltangel.java

=======

>>>>>>> feature/update:core/src/main/java/tingeltangel/Tingeltangel.java
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
<<<<<<< HEAD:tingeltangel/src/main/java/tingeltangel/Tingeltangel.java

=======

>>>>>>> feature/update:core/src/main/java/tingeltangel/Tingeltangel.java
*/
package tingeltangel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class Tingeltangel {

    public static int MAIN_FRAME_POS_X = 50;
    public static int MAIN_FRAME_POS_Y = 50;
    public static int MAIN_FRAME_WIDTH = 1200;
    public static int MAIN_FRAME_HEIGHT = 700;
    public static String MAIN_FRAME_TITLE = "Tingeltangel";
    public static String ANDERSICHT_FRAME_TITLE = "Tingeltangel (Andersicht GUI)";
    public static String MAIN_FRAME_VERSION = " v0.8.0";


    public final static String BASE_URL = "http://13.80.138.170/book-files";


    /**
     * default area code
     */
    public static final String DEFAULT_AREA_CODE = "en";

    private final static Logger log = LogManager.getLogger(Tingeltangel.class);

    public static void initialize() {
        log.info("Starting Tingeltangel" + MAIN_FRAME_VERSION);
        log.info("\tos.name     : " + System.getProperty("os.name"));
        log.info("\tos.version  : " + System.getProperty("os.version"));
        log.info("\tos.arch     : " + System.getProperty("os.arch"));
        log.info("\tjava.version: " + System.getProperty("java.version"));
        log.info("\tjava.vendor : " + System.getProperty("java.vendor"));
    }
}

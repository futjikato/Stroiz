package de.futjikato.stroiz;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.futjikato.stroiz
 */
public class StroizLogger {

    private static Logger logger;

    private static String filename;

    public static void init(String name) {
        filename = name;
    }

    public static Logger getLogger() {
        if(logger == null) {
            logger = Logger.getLogger(filename);
            try {
                logger.addHandler(new FileHandler(filename));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return logger;
    }

}

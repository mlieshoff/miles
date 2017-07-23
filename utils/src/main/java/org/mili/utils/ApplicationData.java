package org.mili.utils;

import java.io.File;

/**
 * @author Michael Lieshoff, 06.07.2017
 */
public class ApplicationData {

    public static File create(String applicationName) {
        File file = new File(System.getProperty("user.home") + "/." + applicationName);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

}

package org.mili.utils;

/**
 * @author Michael Lieshoff, 06.07.17
 */
public class DigitUtils {

    public static String formatTwoDigit(int i) {
        if (i < 0) {
            return "-/-";
        } else if (i < 10) {
            return "0" + i;
        }
        return String.valueOf(i);
    }

}

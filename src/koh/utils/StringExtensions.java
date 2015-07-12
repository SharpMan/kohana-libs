package koh.utils;

import java.util.Random;

/**
 *
 * @author Neo-Craft
 */
public class StringExtensions {

    public static String FirstLetterUpper(String source) {
        if (source.isEmpty()) {
            return "";
        }
        char[] chArray = source.toCharArray();
        chArray[0] = Character.toUpperCase(chArray[0]);
        return new String(chArray);
    }

    public static String ConcatCopy(String str, int times) {
        StringBuilder StringBuilder = new StringBuilder(str.length() * times);
        for (int index = 0; index < times; ++index) {
            StringBuilder.append(str);
        }
        return StringBuilder.toString();
    }

    public static int CountOccurences(String str, char chr, int startIndex, int count) {
        int num = 0;
        for (int index = startIndex; index < startIndex + count; ++index) {
            if ((int) str.charAt(index) == (int) chr) {
                ++num;
            }
        }
        return num;
    }

    public static String RandomString(Random random, int size) {
        StringBuilder StringBuilder = new StringBuilder();
        for (int index = 0; index < size; ++index) {
            StringBuilder.append(Character.toChars((int) (Math.floor(26.0 * random.nextDouble() + 65.0))));
        }
        return StringBuilder.toString();
    }

    public static String EscapeString(String str) {
        return str == null ? (String) null : str.replaceAll("[\\r\\n\\x00\\x1a\\\\'\"]", "\\$0");
    }

    public static String HtmlEntities(String str) {
        str = str.replace("&", "&amp;");
        str = str.replace("<", "&lt;");
        str = str.replace(">", "&gt;");
        return str;
    }

}

package koh.utils;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Neo-Craft
 */
public class Enumerable {
    
    public static byte[] Range(int min, int max) {
        byte[] toReturn = new byte[max - min + 1];
        int seq = 0;
        for (int i = min; i <= max; i++) {
            toReturn[seq] = (byte) i;
            seq++;
        }
        return toReturn;
    }
    
     public static String Join(byte[] c, char sep) {
        StringBuilder sb = new StringBuilder();
        for (byte s : c) {
            sb.append(s).append(sep);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public static String Join(int[] c, char sep) {
        StringBuilder sb = new StringBuilder();
        for (int s : c) {
            sb.append(s).append(sep);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public static String Join(String[] c, char sep) {
        StringBuilder sb = new StringBuilder();
        for (String s : c) {
            sb.append(s).append(sep);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
     public static byte[] StringToByteArray(String c) {
        if (c.isEmpty()) {
            return new byte[0];
        }
        byte[] d = new byte[c.split(",").length];
        for (int i = 0; i < c.split(",").length; i++) {
            d[i] = Byte.parseByte(c.split(",")[i]);
        }
        return d;
    }
     
    
    public static int[] StringToIntArray(String c) {
        return StringToIntArray(c,",");
    }
    
     public static int[] StringToIntArray(String c, String sep) {
        if (c.isEmpty()) {
            return new int[0];
        }
        int[] d = new int[c.split(sep).length];
        for (int i = 0; i < c.split(sep).length; i++) {
            d[i] = Integer.parseInt(c.split(sep)[i]);
        }
        return d;
    }
    
    public static String Join(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int j : array) {
            sb.append(j).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }


    public static String Join(Short[] array) {
        StringBuilder sb = new StringBuilder();
        for (short j : array) {
            sb.append(j).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String Join(int[][] array) {
        StringBuilder sb = new StringBuilder();
        for (int[] j : array) {
            sb.append(Join(j)).append(";");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public static int[][] StringToMultiArray(String c) {
        if (c.isEmpty()) {
            return new int[0][0];
        }
        int[][] array = new int[c.split(";").length][];
        for (int i = 0; i < array.length; i++) {
            array[i] = StringToIntArray(c.split(";")[i]);
        }
        return array;
    }
    
    public static byte[] DuplicatedKey(long size, byte Duplicated) {
        byte[] toReturn = new byte[(int) size];
        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = Duplicated;
        }
        return toReturn;
    }
    
    public static int[] DuplicatedKeyInt(long size, int Duplicated) {
        int[] toReturn = new int[(int) size];
        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = Duplicated;
        }
        return toReturn;
    }
    
}

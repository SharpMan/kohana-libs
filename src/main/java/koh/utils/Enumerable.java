package koh.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Neo-Craft
 */
public class Enumerable {
    
    public static byte[] range(int min, int max) {
        byte[] toReturn = new byte[max - min + 1];
        int seq = 0;
        for (int i = min; i <= max; i++) {
            toReturn[seq] = (byte) i;
            seq++;
        }
        return toReturn;
    }
    
     public static String join(byte[] c, char sep) {
        StringBuilder sb = new StringBuilder();
        for (byte s : c) {
            sb.append(s).append(sep);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static Map<Integer,Integer> stringToIntHashMap(String entry, int size){
        final Map<Integer,Integer> result = new ConcurrentHashMap<>(size);
        if (!entry.isEmpty()) {
            for(String sp : entry.split(";")){
                result.put(Integer.parseInt(sp.split(",")[0]), Integer.parseInt(sp.split(",")[1]));
            }
        }
        return result;
    }

    public static Map<Integer,Short> stringToShortHashMap(String entry, int size){
        final Map<Integer,Short> result = new ConcurrentHashMap<>(size);
        if (!entry.isEmpty()) {
            for(String sp : entry.split(";")){
                result.put(Integer.parseInt(sp.split(",")[0]), Short.parseShort(sp.split(",")[1]));
            }
        }
        return result;
    }

    public static String join(Map<Integer,Integer> entry){
        StringBuilder sb = new StringBuilder();
        entry.forEach((a,b) -> sb.append(a).append(',').append(b).append(';'));
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String join2(Map<Integer,Short> entry){
        StringBuilder sb = new StringBuilder();
        entry.forEach((a,b) -> sb.append(a).append(',').append(b).append(';'));
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public static String join(int[] c, char sep) {
        StringBuilder sb = new StringBuilder();
        for (int s : c) {
            sb.append(s).append(sep);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public static String join(String[] c, char sep) {
        StringBuilder sb = new StringBuilder();
        for (String s : c) {
            sb.append(s).append(sep);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static short[] stringToShortArray(String c) {
        if (c.isEmpty()) {
            return new short[0];
        }
        short[] d = new short[c.split(",").length];
        for (int i = 0; i < c.split(",").length; i++) {
            d[i] = Short.parseShort(c.split(",")[i]);
        }
        return d;
    }


    public static byte[] stringToByteArray(String c) {
        if (c.isEmpty()) {
            return new byte[0];
        }
        byte[] d = new byte[c.split(",").length];
        for (int i = 0; i < c.split(",").length; i++) {
            d[i] = Byte.parseByte(c.split(",")[i]);
        }
        return d;
    }
     
    
    public static int[] stringToIntArray(String c) {
        if(c.equalsIgnoreCase("-1"))
            return new int[0];
        return stringToIntArray(c,",");
    }
    
     public static int[] stringToIntArray(String c, String sep) {
        if (c.isEmpty()) {
            return new int[0];
        }
        int[] d = new int[c.split(sep).length];
        for (int i = 0; i < c.split(sep).length; i++) {
            d[i] = Integer.parseInt(c.split(sep)[i]);
        }
        return d;
    }
    
    public static String join(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int j : array) {
            sb.append(j).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }


    public static String join(Short[] array) {
        StringBuilder sb = new StringBuilder();
        for (short j : array) {
            sb.append(j).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static <T> String join(T[] array) {
        StringBuilder sb = new StringBuilder();
        for (T j : array) {
            sb.append(j).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String join(short[] array) {
        StringBuilder sb = new StringBuilder();
        for (short j : array) {
            sb.append(j).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String join(List<int[]> array) {
        StringBuilder sb = new StringBuilder();
        for (int[] j : array) {
            sb.append(join(j)).append(";");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String Join(int[][] array) {
        StringBuilder sb = new StringBuilder();
        for (int[] j : array) {
            sb.append(join(j)).append(";");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public static int[][] stringToMultiArray(String c) {
        if (c.isEmpty()) {
            return new int[0][0];
        }
        int[][] array = new int[c.split(";").length][];
        for (int i = 0; i < array.length; i++) {
            array[i] = stringToIntArray(c.split(";")[i]);
        }
        return array;
    }

    public static int[][] stringToMultiArray(String c,int len) {
        if (c.isEmpty()) {
            return new int[0][len];
        }
        int[][] array = new int[c.split(";").length][2];
        for (int i = 0; i < array.length; i++) {
            array[i] = stringToIntArray(c.split(";")[i]);
        }
        return array;
    }
    
    public static byte[] duplicatedKey(long size, byte Duplicated) {
        byte[] toReturn = new byte[(int) size];
        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = Duplicated;
        }
        return toReturn;
    }
    
    public static int[] duplicatedKeyInt(long size, int Duplicated) {
        int[] toReturn = new int[(int) size];
        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = Duplicated;
        }
        return toReturn;
    }
    
}

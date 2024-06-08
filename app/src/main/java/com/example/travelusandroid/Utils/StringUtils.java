package com.example.travelusandroid.Utils;

public class StringUtils {
    public static String trimEnd(String str) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        while (len > 0 && Character.isWhitespace(str.charAt(len - 1))) {
            len--;
        }
        return str.substring(0, len);
    }
}

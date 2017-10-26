package org.xlrnet.metadict.web.util;

import org.jetbrains.annotations.NotNull;

/**
 * Helper class with methods to convert stuff.
 */
public class ConversionUtils {

    private ConversionUtils() {

    }

    /**
     * Returns a byte representation of a given hexadecimal string representation. Note that is method is the
     * counterpart to {@link #byteArrayToHexString(byte[])}.
     *
     * @param hexString The hex string to convert.
     * @return Converted byte array.
     */
    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] ba = new byte[len / 2];

        for (int i = 0; i < ba.length; i++) {
            int j = i * 2;
            int t = Integer.parseInt(hexString.substring(j, j + 2), 16);
            byte b = (byte) (t & 0xFF);
            ba[i] = b;
        }
        return ba;
    }

    /**
     * Returns a hexadecimal formatted String representation of the given byte array.
     *
     * @param byteArray
     *         The byte array to convert.
     * @return A string representation of the given byte array.
     */
    @NotNull
    public static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : byteArray) {
            String s = Integer.toHexString((int) (b & 0xff));
            if (s.length() == 1) {
                stringBuilder.append('0');
            }
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }
}

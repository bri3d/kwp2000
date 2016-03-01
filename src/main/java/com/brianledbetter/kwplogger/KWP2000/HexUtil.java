package com.brianledbetter.kwplogger.KWP2000;

/**
 * Created by b3d on 12/6/15.
 */
public class HexUtil {
    public static String bytesToHexString(byte[] bytes) {
        // ELM takes ASCII encoded bytes
        StringBuilder sb = new StringBuilder((bytes.length * 3) + 1);
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        sb.append("\r");
        return sb.toString();
    }
    public static int bytesToInt(byte[] bytes) {
        int ret = 0;
        for (int i=0; i<4 && i<bytes.length; i++) {
            ret <<= 8;
            ret |= (int)bytes[i] & 0xFF;
        }
        return ret;
    }

    public static byte[] parseByteLine(String byteLine) {
        byteLine = byteLine.replaceAll("\\s", ""); // Remove whitespace
        if (!byteLine.matches("([0-9A-F])+")) { // Check for hexish-ness
            return new byte[0];
        }
        final int len = byteLine.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(byteLine.charAt(i), 16) << 4)
                    + Character.digit(byteLine.charAt(i+1), 16));
        }
        return data;
    }
}

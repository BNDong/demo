package com.test;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtil {
    private static final String desAlgorithm = "DESede/CBC/NoPadding";
    private static final String desKeyAlgorithm = "DESede";
    private static final char DIGITS[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final byte defaultIV[] = new byte[]{'0', '0', '0', '0', '0', '0', '0', '0'};

    public CryptUtil() {
    }

    private static SecretKey KeyGenerator(String keyStr) throws Exception {
        byte input[] = md5Hex(keyStr).substring(0, 24).getBytes("GBK");
        SecretKey triDesKey = new SecretKeySpec(input, desKeyAlgorithm);
        return triDesKey;
    }

    public static String encryptBy3DesAndBase64(String content, String keyStr) throws Exception {
        return encryptBy3DesAndBase64(content, keyStr, "UTF-8");
    }

    public static String decryptBy3DesAndBase64(String content, String keyStr) throws Exception {
        return decryptBy3DesAndBase64(content, keyStr, "UTF-8");
    }

    public static String encryptBy3DesAndBase64(String content, String keyStr, String encoding) throws Exception {
        byte output[] = null;
        byte input[] = null;
        int residue = (content.getBytes(encoding).length) % 8;
        if (0 != residue) {
            int padLen = 8 - residue;
            StringBuffer strBuf = new StringBuffer(content);
            for (int i = 0; i < padLen; i++) {
                strBuf.append(' ');
            }
            input = (new String(strBuf)).getBytes(encoding);
        } else {
            input = content.getBytes(encoding);
        }
        output = encryptBy3Des(input, keyStr);
        return Base64.encode(output).replaceAll("[\\n\\r]", "");
    }

    public static String decryptBy3DesAndBase64(String content, String keyStr, String encoding) throws Exception {
        byte output[] = null;
        byte input[] = null;
        input = Base64.decode(content);
        output = decryptBy3Des(input, keyStr);
        String retStr = new String(output, encoding);
        return (retStr.trim());
    }

    public static byte[] encryptBy3Des(byte content[], String keyStr) throws Exception {
        return cryptBy3Des(keyStr, 1, null, content);
    }

    public static byte[] decryptBy3Des(byte content[], String keyStr) throws Exception {
        return cryptBy3Des(keyStr, 2, null, content);
    }

    public static byte[] cryptBy3Des(String keyStr, int cryptModel, byte iv[], byte content[]) throws Exception {
        Cipher cipher = null;
        SecretKey key = KeyGenerator(keyStr);
        IvParameterSpec IVSpec = iv == null ? IvGenerator(defaultIV) : IvGenerator(iv);
        cipher = Cipher.getInstance(desAlgorithm);
        cipher.init(cryptModel, key, IVSpec);
        return cipher.doFinal(content);
    }

    public static String md5Hex(String content) throws Exception {
        MessageDigest md5 = null;
        md5 = MessageDigest.getInstance("MD5");
        md5.update(content.getBytes("GBK"));
        return new String(encodeHex(md5.digest()));
    }

    public static char[] encodeHex(byte data[]) {
        int len = data.length;
        char out[] = new char[len << 1];
        int i = 0;
        int j = 0;
        for (; i < len; i++) {
            out[j++] = DIGITS[(0xf0 & data[i]) >>> 4];
            out[j++] = DIGITS[0xf & data[i]];
        }
        return out;
    }

    private static IvParameterSpec IvGenerator(byte b[]) {
        IvParameterSpec IV = new IvParameterSpec(b);
        return IV;
    }

    public static class Base64 {
        private static char[] base64EncodeChars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
        private static byte[] base64DecodeChars = new byte[]{
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
                52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
                -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
                -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

        private Base64() {
        }

        public static String encode(byte[] data) {
            StringBuffer sb = new StringBuffer();
            int len = data.length;
            int i = 0;
            int b1, b2, b3;
            while (i < len) {
                b1 = data[i++] & 0xff;
                if (i == len) {
                    sb.append(base64EncodeChars[b1 >>> 2]);
                    sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                    sb.append("==");
                    break;
                }
                b2 = data[i++] & 0xff;
                if (i == len) {
                    sb.append(base64EncodeChars[b1 >>> 2]);
                    sb.append(
                            base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                    sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                    sb.append("=");
                    break;
                }
                b3 = data[i++] & 0xff;
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(
                        base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(
                        base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
                sb.append(base64EncodeChars[b3 & 0x3f]);
            }
            return sb.toString();
        }

        public static byte[] decode(String str) {
            byte[] data = str.getBytes();
            int len = data.length;
            ByteArrayOutputStream buf = new ByteArrayOutputStream(len);
            int i = 0;
            int b1, b2, b3, b4;
            while (i < len) {
                /* b1 */
                do {
                    b1 = base64DecodeChars[data[i++]];
                } while (i < len && b1 == -1);
                if (b1 == -1) {
                    break;
                }
                /* b2 */
                do {
                    b2 = base64DecodeChars[data[i++]];
                } while (i < len && b2 == -1);
                if (b2 == -1) {
                    break;
                }
                buf.write((int) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
                /* b3 */
                do {
                    b3 = data[i++];
                    if (b3 == 61) {
                        return buf.toByteArray();
                    }
                    b3 = base64DecodeChars[b3];
                } while (i < len && b3 == -1);
                if (b3 == -1) {
                    break;
                }
                buf.write((int) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
                /* b4 */
                do {
                    b4 = data[i++];
                    if (b4 == 61) {
                        return buf.toByteArray();
                    }
                    b4 = base64DecodeChars[b4];
                } while (i < len && b4 == -1);
                if (b4 == -1) {
                    break;
                }
                buf.write((int) (((b3 & 0x03) << 6) | b4));
            }
            return buf.toByteArray();
        }
    }
}

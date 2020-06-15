package com.localhost;

import org.apache.shiro.codec.Base64;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA工具类
 * Class RSAUtils
 * @author dbn
 */
class RSAUtils {

    /**
     * 签名算法
     */
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 数据编码
     */
    private static final String ENCODING = "UTF-8";

    /**
     * 生成密钥对(公钥和私钥)
     * @return 密钥对 Map 对象
     * @throws Exception
     */
    static Map<String, Object> resetGenKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);

        KeyPair keyPair            = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey     = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey   = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);

        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 获取公钥
     * @param keyMap 密钥对 Map 对象
     * @return 公钥
     * @throws Exception
     */
    static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encodeToString(key.getEncoded());
    }

    /**
     * 获取私钥
     * @param keyMap 密钥对 Map 对象
     * @return 私钥
     * @throws Exception
     */
    static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encodeToString(key.getEncoded());
    }

    /**
     * 私钥加签
     * @param data 原始数据
     * @param privateKey 私钥
     * @return 加密数据
     * @throws Exception
     */
    static String sign(String data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK   = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature   = Signature.getInstance(SIGNATURE_ALGORITHM);

        signature.initSign(privateK);
        signature.update(data.getBytes(ENCODING));
        return Base64.encodeToString(signature.sign());
    }

    /**
     * 公钥验签
     * @param data 原始数据
     * @param publicKey 公钥
     * @param sign 数据签名
     * @return 验签结果
     * @throws Exception
     */
    static boolean verifySign(String data, String publicKey, String sign) throws Exception {
        try {
            byte[] keyBytes = Base64.decode(publicKey);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory      = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicK          = keyFactory.generatePublic(keySpec);
            Signature signature        = Signature.getInstance(SIGNATURE_ALGORITHM);

            signature.initVerify(publicK);
            signature.update(data.getBytes(ENCODING));

            return signature.verify(Base64.decode(sign));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 私钥加密
     * @param dataStr 原始数据
     * @param privateKey 私钥
     * @return 加密数据
     * @throws Exception
     */
    static String encryptByPrivateKey(String dataStr, String privateKey) throws Exception {
        byte[] data     = dataStr.getBytes(ENCODING);
        byte[] keyBytes = Base64.decode(privateKey);

        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK          = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher         = Cipher.getInstance(keyFactory.getAlgorithm());

        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int offSet = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return Base64.encodeToString(encryptedData);
    }

    /**
     * 公钥解密
     * @param encryptedDataStr 加密数据
     * @param publicKey 公钥
     * @return 解密后数据
     * @throws Exception
     */
    static String decryptByPublicKey(String encryptedDataStr, String publicKey) throws Exception {
        byte[] encryptedData = Base64.decode(encryptedDataStr);
        byte[] keyBytes      = Base64.decode(publicKey);

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int offSet = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData, ENCODING);
    }

    /**
     * 公钥加密
     * @param dataStr 原始数据
     * @param publicKey 公钥
     * @return 加密数据
     * @throws Exception
     */
    static String encryptByPublicKey(String dataStr, String publicKey) throws Exception {
        byte[] data     = dataStr.getBytes(ENCODING);
        byte[] keyBytes = Base64.decode(publicKey);

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK           = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher         = Cipher.getInstance(keyFactory.getAlgorithm());

        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int offSet = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return Base64.encodeToString(encryptedData);
    }

    /**
     * 私钥解密
     * @param encryptedDataStr 加密数据
     * @param privateKey 私钥
     * @return 解密后数据
     * @throws Exception
     */
    static String decryptByPrivateKey(String encryptedDataStr, String privateKey) throws Exception {
        byte[] encryptedData = Base64.decode(encryptedDataStr);
        byte[] keyBytes      = Base64.decode(privateKey);

        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK          = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher         = Cipher.getInstance(keyFactory.getAlgorithm());

        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int offSet = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData, ENCODING);
    }

}

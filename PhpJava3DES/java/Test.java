package com.test;

public class Test {

    public static void main(String[] args) throws Exception {

        // 加密字符串
        String str = "123afd!@#asfghj";
        System.out.println("------- 加密字符串 -------");
        System.out.println(str);

        // 密钥字符串
        String key = "JAkJLORVds3LC8zv7eaJ3MCWkIzEuwnrlzs7y7zNXYZ8TXRXGJ";
        System.out.println("------- 密钥字符串 -------");
        System.out.println(key);

        // 加密
        String enStr = CryptUtil.encryptBy3DesAndBase64(str, key);
        System.out.println("------- 加密数据 -------");
        System.out.println(enStr);

        // 解密
        String deStr = CryptUtil.decryptBy3DesAndBase64(enStr, key);
        System.out.println("------- 解密数据 -------");
        System.out.println(deStr);

        // PHP加密数据
        String phpEnStr = "Y1hS/nvqtL+XLM/ZR7Iqdw==";
        System.out.println("------- PHP加密数据 -------");
        System.out.println(phpEnStr);

        // PHP解密数据
        String phpDeStr = CryptUtil.decryptBy3DesAndBase64(phpEnStr, key);
        System.out.println("------- PHP解密数据 -------");
        System.out.println(phpDeStr);

    }
}

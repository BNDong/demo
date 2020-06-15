package com.localhost;

import java.util.Map;

public class Test {

	public static void main(String[] args) throws Exception {

		// 生成新的公私钥
		Map<String, Object> genKeyPair = RSAUtils.resetGenKeyPair();

		// 获取公钥
		String publicKey = RSAUtils.getPublicKey(genKeyPair);
		System.out.println("------- 公钥 -------");
		System.out.println(publicKey);

		// 获取私钥
		String privateKey = RSAUtils.getPrivateKey(genKeyPair);
		System.out.println("------- 私钥 -------");
		System.out.println(privateKey);

//		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyJn4YYj+Ng/xjOPvGrt3MVLZ+xk3yjqBUu5U5tO1xC1Q/Wae8gWpIC3ipS9UVMCNUOKm2GHdQFI1EXaAu0bRQhBb4aE5IdXrT9Xoo4Zeuv/ut9UvKEpjBB7Geiy6OvAoA0ROBRLA9/j9W8jZraWiirXRAxI7ZyqgZSr5lHgJWdwIDAQAB";
//		String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALImfhhiP42D/GM4+8au3cxUtn7GTfKOoFS7lTm07XELVD9Zp7yBakgLeKlL1RUwI1Q4qbYYd1AUjURdoC7RtFCEFvhoTkh1etP1eijhl66/+631S8oSmMEHsZ6LLo68CgDRE4FEsD3+P1byNmtpaKKtdEDEjtnKqBlKvmUeAlZ3AgMBAAECgYB0/En5gSrypyVpktXjFpmXwFlGzroI+hfXDIdlqaXygdoE777yTpmYTdAifCWlEENi3wKzDUXsVFKf/ktd819QF4gHCby8Hlk39Gj1itUJ90V7XZVWgJJYMaudGg4rKa3w+VYhw2fut5EXJ9R6o7rQtp9M9mlq6NuQIK30GYZEoQJBAN0hh3YYhPmeKLDwGsh/Ys+SbVWsSPkVnu6O9wcMk3JH4bs/n2+hIPvPTYTHixcO/iMwILKdVWSdn5XtKnXR3zMCQQDOPfM7VJbyZVoe0f8iAJfRAZ0d0oq/qTL30ypx3P7VltkcfszQH6M1+gapcZ8mCXpeV510V2U3AlsU+vunS3utAkAE2GM7dzYSsiB6IAi2M/RaT/8NTYUb0Bl3aLKI+QGSE3kivTYlIAa0/cnZCvZFPxLaeod84m2okruYcWXoxvx5AkEAqM5J7FDjL8lHBxzoj2Me38JLYCJ40EDj57Yd8o5owleyosEiUGLkyoQ3ua63DYIKd3eM97GktW6nMDfxjE+bDQJBANcwDRf4CRW646Y/7kizjVOM4MUiJqAoBC4IEawrk25wfo0gBpBbcl24IOTERLUVtMZvqoc6mbNoMUny2LuWvVI=";

		// 加密数据
		String data = "{\"success\":\"success\",\"msg\":\"\\u67e5\\u8be2\\u6210\\u529f\",\"result\":{\"list\":[{\"id\":\"19\",\"no\":\"\\u5180D123654\",\"name\":\"\\u674e\\u5fd7\\u8d85\",\"gps_no\":\"8800003333\",\"gps_type\":\"1\",\"mobile\":\"15631600442\",\"lat\":\"39.32840\",\"lng\":\"115.99546\"},{\"id\":\"169\",\"no\":\"\\u5180D666444\",\"name\":\"\\u674e\\u5fd7\\u8d85\",\"gps_no\":\"8800003248\",\"gps_type\":\"1\",\"mobile\":\"15631600442\",\"lat\":\"40.21509\",\"lng\":\"116.09031\"}]}}";
		System.out.println("------- 加密字符串 -------");
		System.out.println(data);

		// 私钥加签
		String sign = RSAUtils.sign(data, privateKey);
//		String sign = "JAkJLORVds3LC8zv7eaJ3MCWkIzEuwnrlzs7y7zNXYZ8TXRXGJVFBuJ6bhYK5lZMvbHOANCTQSUBQgDUN9Rra38YJW+MG16DxdDvRZlZ8FszySH+5hPuQNGgKkwzdDhabH7/7VhemXWq9VRpkyHxPSnn69I3vhcmba+lGbq2HM4=";
		System.out.println("------- 数据签名 -------");
		System.out.println(sign);

		// 验证签名
		boolean verifySign = RSAUtils.verifySign(data, publicKey, sign);
		System.out.println("------- 验证签名 -------");
		System.out.println(verifySign);

		// 私钥加密
		String priEncrypt = RSAUtils.encryptByPrivateKey(data, privateKey);
		System.out.println("------- 私钥加密 -------");
		System.out.println(priEncrypt);

		// 公钥解密
		String pubDecrypt = RSAUtils.decryptByPublicKey(priEncrypt, publicKey);
		System.out.println("------- 公钥解密 -------");
		System.out.println(pubDecrypt);

		// 公钥加密
		String pubEncrypt = RSAUtils.encryptByPublicKey(data, publicKey);
		System.out.println("------- 公钥加密 -------");
		System.out.println(pubEncrypt);

		// 私钥解密
		String priDecrypt = RSAUtils.decryptByPrivateKey(pubEncrypt, privateKey);
		System.out.println("------- 私钥解密 -------");
		System.out.println(priDecrypt);
	}
}

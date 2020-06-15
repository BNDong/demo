<?php

require_once dirname(__FILE__) . '/RSAUtils.php';

$keyList = RSAUtils::resetGenKeyPair();

echo '<h3>生成新的公私钥</h3>';

//$publicKeyStr  = $keyList['publicKeyStr'];
//$privateKeyStr = $keyList['privateKeyStr'];
//$publicKey     = $keyList['publicKey'];
//$privateKey    = $keyList['privateKey'];

$publicKeyStr  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyJn4YYj+Ng/xjOPvGrt3MVLZ+xk3yjqBUu5U5tO1xC1Q/Wae8gWpIC3ipS9UVMCNUOKm2GHdQFI1EXaAu0bRQhBb4aE5IdXrT9Xoo4Zeuv/ut9UvKEpjBB7Geiy6OvAoA0ROBRLA9/j9W8jZraWiirXRAxI7ZyqgZSr5lHgJWdwIDAQAB";
$privateKeyStr = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALImfhhiP42D/GM4+8au3cxUtn7GTfKOoFS7lTm07XELVD9Zp7yBakgLeKlL1RUwI1Q4qbYYd1AUjURdoC7RtFCEFvhoTkh1etP1eijhl66/+631S8oSmMEHsZ6LLo68CgDRE4FEsD3+P1byNmtpaKKtdEDEjtnKqBlKvmUeAlZ3AgMBAAECgYB0/En5gSrypyVpktXjFpmXwFlGzroI+hfXDIdlqaXygdoE777yTpmYTdAifCWlEENi3wKzDUXsVFKf/ktd819QF4gHCby8Hlk39Gj1itUJ90V7XZVWgJJYMaudGg4rKa3w+VYhw2fut5EXJ9R6o7rQtp9M9mlq6NuQIK30GYZEoQJBAN0hh3YYhPmeKLDwGsh/Ys+SbVWsSPkVnu6O9wcMk3JH4bs/n2+hIPvPTYTHixcO/iMwILKdVWSdn5XtKnXR3zMCQQDOPfM7VJbyZVoe0f8iAJfRAZ0d0oq/qTL30ypx3P7VltkcfszQH6M1+gapcZ8mCXpeV510V2U3AlsU+vunS3utAkAE2GM7dzYSsiB6IAi2M/RaT/8NTYUb0Bl3aLKI+QGSE3kivTYlIAa0/cnZCvZFPxLaeod84m2okruYcWXoxvx5AkEAqM5J7FDjL8lHBxzoj2Me38JLYCJ40EDj57Yd8o5owleyosEiUGLkyoQ3ua63DYIKd3eM97GktW6nMDfxjE+bDQJBANcwDRf4CRW646Y/7kizjVOM4MUiJqAoBC4IEawrk25wfo0gBpBbcl24IOTERLUVtMZvqoc6mbNoMUny2LuWvVI=";
$publicKey     = RSAUtils::str2key($publicKeyStr, RSAUtils::PUBLIC_TYPE);
$privateKey    = RSAUtils::str2key($privateKeyStr, RSAUtils::PRIVATE_TYPE);

echo "公钥字符串：" . '<br>' . $publicKeyStr . '<br>';
echo "私钥字符串：" . '<br>' . $privateKeyStr . '<br>';
echo "公钥：" . '<br>' . $publicKey . '<br>';
echo "私钥：" . '<br>' . $privateKey . '<br>';

echo '<h3>加密字符串</h3>';
$data = "{\"success\":\"success\",\"msg\":\"\\u67e5\\u8be2\\u6210\\u529f\",\"result\":{\"list\":[{\"id\":\"19\",\"no\":\"\\u5180D123654\",\"name\":\"\\u674e\\u5fd7\\u8d85\",\"gps_no\":\"8800003333\",\"gps_type\":\"1\",\"mobile\":\"15631600442\",\"lat\":\"39.32840\",\"lng\":\"115.99546\"},{\"id\":\"169\",\"no\":\"\\u5180D666444\",\"name\":\"\\u674e\\u5fd7\\u8d85\",\"gps_no\":\"8800003248\",\"gps_type\":\"1\",\"mobile\":\"15631600442\",\"lat\":\"40.21509\",\"lng\":\"116.09031\"}]}}";
echo $data . '<br>';

echo '<h3>数据签名</h3>';
$sign = RSAUtils::sign($data, $privateKey);
echo $sign . '<br>';

echo '<h3>验证签名</h3>';
$verifySign = RSAUtils::verifySign($data, $publicKey, $sign);
echo $verifySign . '<br>';

echo '<h3>私钥加密</h3>';
$priEncrypt = RSAUtils::encryptByPrivateKey($data, $privateKey);
echo $priEncrypt . '<br>';

echo '<h3>公钥解密</h3>';
$pubDecrypt = RSAUtils::decryptByPublicKey($priEncrypt, $publicKey);
echo $pubDecrypt . '<br>';

echo '<h3>公钥加密</h3>';
$pubEncrypt = RSAUtils::encryptByPublicKey($data, $publicKey);
echo $pubEncrypt . '<br>';

echo '<h3>私钥解密</h3>';
$priDecrypt = RSAUtils::decryptByPrivateKey($pubEncrypt, $privateKey);
echo $priDecrypt . '<br>';


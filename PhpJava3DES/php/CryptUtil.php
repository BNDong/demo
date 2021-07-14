<?php

class CryptUtil{

    const CIPHER_ALGO = 'des-ede3-cbc';
    const OPTIONS     = OPENSSL_RAW_DATA | OPENSSL_NO_PADDING;
    const IV          = '00000000';

    /**
     * 加密数据
     * @param string $data 加密数据
     * @param string $key 密钥
     * @return string
     */
    public static function encrypt($data, $key)
    {
        $strPadded = $data;
        if (strlen($strPadded) % 8) {
            $strPadded = str_pad($strPadded,strlen($strPadded) + 8 - strlen($strPadded) % 8, "\0");
        }
        $key = self::getKey($key);
        $encData = openssl_encrypt($strPadded, self::CIPHER_ALGO, $key, self::OPTIONS, self::IV);
        return base64_encode($encData);
    }

    /**
     * 解密数据
     * @param string $data 加密数据
     * @param string $key 密钥
     * @return string
     */
    public static function decrypt($data, $key)
    {
        $key     = self::getKey($key);
        $data    = base64_decode($data);
        $decData = openssl_decrypt($data, self::CIPHER_ALGO, $key, self::OPTIONS, self::IV);
        return trim($decData);
    }

    /**
     * 处理加解密Key
     * @param string $key 密钥
     * @return string
     */
    private static function getKey($key) {
        $key = md5($key);
        return substr($key,0,24);
    }
}
<?php

require_once dirname(__FILE__) . '/CryptUtil.php';

$str = "123afd!@#asfghj";
echo "加密字符串：" . $str . "<br>";

$key = "JAkJLORVds3LC8zv7eaJ3MCWkIzEuwnrlzs7y7zNXYZ8TXRXGJ";
echo "密钥字符串：" . $key . "<br>";

$enStr = CryptUtil::encrypt($str, $key);
echo "加密数据：" . $enStr . "<br>";

$deStr = CryptUtil::decrypt($enStr, $key);
echo "解密数据：" . $deStr . "<br>";

$javaEnStr = 'Y1hS/nvqtL9tCYfw85ZEDw==';
echo "JAVA加密数据：" . $javaEnStr . "<br>";

$javaDeStr = CryptUtil::decrypt($javaEnStr, $key);
echo "JAVA解密数据：" . $javaDeStr . "<br>";
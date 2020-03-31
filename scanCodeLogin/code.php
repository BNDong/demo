<?php

require_once dirname(__FILE__) . '/vendor/autoload.php';
require_once dirname(__FILE__) . '/Config.php';
require_once dirname(__FILE__) . '/lib/RedisUtile.php';
require_once dirname(__FILE__) . '/lib/Common.php';

use Endroid\QrCode\QrCode;

$qid   = \lib\Common::getQid();
$redis = \lib\RedisUtile::getInstance();
$url   = 'http://localhost/qrcode/login.php?qid=' . $qid;

$qrCode = new QrCode($url);
$qrCode->setSize(200);
$redis->setex(\lib\Common::getQidKey($qid), \Config::QRCODE_TTL, $url);
$contentType = $qrCode->getContentType();
$fileType    = array_pop(explode('/', $contentType));

// 合成图片的base64编码
$imgBase64 = 'data:image/' . $fileType . ';base64,' . chunk_split(base64_encode($qrCode->writeString()));

echo json_encode(array(
    'img' => $imgBase64,
    'qid' => $qid
));die;
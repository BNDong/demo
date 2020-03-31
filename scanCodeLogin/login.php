<?php

require_once dirname(__FILE__) . '/lib/RedisUtile.php';
require_once dirname(__FILE__) . '/lib/Common.php';

/**
 * -------  登录逻辑模拟 --------
 * 请根据实际编写登录逻辑并处理安全验证
 */

$qid = $_GET['qid'];
$uid = $_GET['uid'];

$data = array();
switch ($uid)
{
    case '1':
        $data['uid']  = 1;
        $data['name'] = '张三';
        break;

    case '2':
        $data['uid']  = 2;
        $data['name'] = '李四';
        break;
}

$data  = json_encode($data);
$redis = \lib\RedisUtile::getInstance();
$redis->setex(\lib\Common::getQidLoginKey($qid), 1800, $data);
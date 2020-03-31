<?php

namespace lib;

require_once dirname(dirname(__FILE__)) . '/Config.php';

/**
 * Class Common
 * @package lib
 */
class Common
{
    /**
     * 获取 qid
     * @return string
     */
    public static function getQid()
    {
        return uniqid();
    }

    /**
     * 获取 qidKey
     * @param string $qid
     * @return string
     */
    public static function getQidKey($qid)
    {
        return 'ws_' . $qid;
    }

    /**
     * 获取 qidLoginKey
     * @param string $qid
     * @return string
     */
    public static function getQidLoginKey($qid)
    {
        return 'ws_login_' . $qid;
    }
}
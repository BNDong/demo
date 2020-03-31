<?php

namespace lib;

require_once dirname(dirname(__FILE__)) . '/Config.php';

/**
 * Class RedisUtile
 * @package lib
 */
final class RedisUtile {

    private static $_redis = null;

    /**
     * RedisUtile constructor.
     */
    private function __construct() {}

    public static function getInstance()
    {
        if (!self::$_redis instanceof \Redis) {
            self::$_redis = new \Redis();
            self::$_redis->connect(\Config::REDIS_HOST, \Config::REDIS_PROT);
        }
        return self::$_redis;
    }
}
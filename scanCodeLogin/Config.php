<?php

/**
 * Class Config
 */
class Config {
    const REDIS_HOST    = 'redis';      // redis 主机地址
    const REDIS_PROT    = '6379';       // redis 主机端口
    const QRSERVER_HOST = '10.0.0.11';  // 二维码监听服务器地址
    const QRSERVER_PROT = '8095';       // 二维码监听服务器端口
    const QRCODE_TTL    = 5 * 60;       // 二维码过期时间，单位秒
}
<?php

require_once dirname(dirname(__FILE__)) . '/Config.php';
require_once dirname(dirname(__FILE__)) . '/lib/RedisUtile.php';
require_once dirname(dirname(__FILE__)) . '/lib/Common.php';

/**
 * 扫码登陆服务端
 * Class QRServer
 * @author BNDong
 */
class QRServer {

    private $_sock;
    private $_redis;
    private $_clients = array();

    /**
     * socketServer constructor.
     */
    public function __construct()
    {
        // 设置 timeout
        set_time_limit(0);

        // 创建一个套接字（通讯节点）
        $this->_sock = socket_create(AF_INET, SOCK_STREAM, SOL_TCP) or die("Could not create socket" . PHP_EOL);
        socket_set_option($this->_sock, SOL_SOCKET, SO_REUSEADDR, 1);

        // 绑定地址
        socket_bind($this->_sock, \Config::QRSERVER_HOST, \Config::QRSERVER_PROT) or die("Could not bind to socket" . PHP_EOL);

        // 监听套接字上的连接
        socket_listen($this->_sock, 4) or die("Could not set up socket listener" . PHP_EOL);

        $this->_redis  = \lib\RedisUtile::getInstance();
    }

    /**
     * 启动服务
     */
    public function run()
    {
        $this->_clients = array();
        $this->_clients[uniqid()] = $this->_sock;

        while (true){
            $changes = $this->_clients;
            $write   = NULL;
            $except  = NULL;
            socket_select($changes,  $write,  $except, NULL);
            foreach ($changes as $key => $_sock) {

                if($this->_sock == $_sock){ // 判断是不是新接入的 socket

                    if(($newClient = socket_accept($_sock))  === false){
                        die('failed to accept socket: '.socket_strerror($_sock)."\n");
                    }

                    $buffer   = trim(socket_read($newClient, 1024)); // 读取请求
                    $response = $this->handShake($buffer);
                    socket_write($newClient, $response, strlen($response)); // 发送响应
                    socket_getpeername($newClient, $ip); // 获取 ip 地址

                    $qid = $this->getHandQid($buffer);
                    $this->log("new clinet: ". $qid);

                    if ($qid) { // 验证是否存在 qid
                        if (isset($this->_clients[$qid])) $this->close($qid, $this->_clients[$qid]);
                        $this->_clients[$qid] = $newClient;
                    } else {
                        $this->close($qid, $newClient);
                    }

                } else {

                    // 判断二维码是否过期
                    if ($this->_redis->exists(\lib\Common::getQidKey($key))) {

                        $loginKey = \lib\Common::getQidLoginKey($key);
                        if ($this->_redis->exists($loginKey)) { // 判断用户是否扫码
                            $this->send($key, $this->_redis->get($loginKey));
                            $this->close($key, $_sock);
                        }

                        $res = socket_recv($_sock, $buffer,  2048, 0);
                        if (false === $res) {
                            $this->close($key, $_sock);
                        } else {
                            $res && $this->log("{$key} clinet msg: " . $this->message($buffer));
                        }
                    } else {
                        $this->close($key, $this->_clients[$key]);
                    }

                }
            }
            sleep(1);
        }
    }

    /**
     * 构建响应
     * @param string $buf
     * @return string
     */
    private function handShake($buf){
        $buf    = substr($buf,strpos($buf,'Sec-WebSocket-Key:') + 18);
        $key    = trim(substr($buf, 0, strpos($buf,"\r\n")));
        $newKey = base64_encode(sha1($key."258EAFA5-E914-47DA-95CA-C5AB0DC85B11",true));
        $newMessage = "HTTP/1.1 101 Switching Protocols\r\n";
        $newMessage .= "Upgrade: websocket\r\n";
        $newMessage .= "Sec-WebSocket-Version: 13\r\n";
        $newMessage .= "Connection: Upgrade\r\n";
        $newMessage .= "Sec-WebSocket-Accept: " . $newKey . "\r\n\r\n";
        return $newMessage;
    }

    /**
     * 获取 qid
     * @param string $buf
     * @return mixed|string
     */
    private function getHandQid($buf) {
        preg_match("/^[\s\n]?GET\s+\/\?qid\=([a-z0-9]+)\s+HTTP.*/", $buf, $matches);
        $qid = isset($matches[1]) ? $matches[1] : '';
        return $qid;
    }

    /**
     * 编译发送数据
     * @param string $s
     * @return string
     */
    private function frame($s) {
        $a = str_split($s, 125);
        if (count($a) == 1) {
            return "\x81" . chr(strlen($a[0])) . $a[0];
        }
        $ns = "";
        foreach ($a as $o) {
            $ns .= "\x81" . chr(strlen($o)) . $o;
        }
        return $ns;
    }

    /**
     * 解析接收数据
     * @param resource $buffer
     * @return null|string
     */
    private function message($buffer){
        $masks = $data = $decoded = null;
        $len = ord($buffer[1]) & 127;
        if ($len === 126)  {
            $masks = substr($buffer, 4, 4);
            $data = substr($buffer, 8);
        } else if ($len === 127)  {
            $masks = substr($buffer, 10, 4);
            $data = substr($buffer, 14);
        } else  {
            $masks = substr($buffer, 2, 4);
            $data = substr($buffer, 6);
        }
        for ($index = 0; $index < strlen($data); $index++) {
            $decoded .= $data[$index] ^ $masks[$index % 4];
        }
        return $decoded;
    }

    /**
     * 发送消息
     * @param string $qid
     * @param string $msg
     */
    private function send($qid, $msg)
    {
        $frameMsg = $this->frame($msg);
        socket_write($this->_clients[$qid], $frameMsg, strlen($frameMsg));
        $this->log("{$qid} clinet send: " . $msg);
    }

    /**
     * 关闭 socket
     * @param string $qid
     * @param resource $socket
     */
    private function close($qid, $socket)
    {
        socket_close($socket);
        if (array_key_exists($qid, $this->_clients)) unset($this->_clients[$qid]);
        $this->_redis->del(\lib\Common::getQidKey($qid));
        $this->_redis->del(\lib\Common::getQidLoginKey($qid));
        $this->log("{$qid} clinet close");
    }

    /**
     * 日志记录
     * @param string $msg
     */
    private function log($msg)
    {
        echo '['. date('Y-m-d H:i:s') .'] ' . $msg . "\n";
    }
}

$server = new QRServer();
$server->run();
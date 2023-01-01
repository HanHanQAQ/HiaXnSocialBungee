package team.hiaxn.hanhan.hiaxnsocialbungee.Redis;

import redis.clients.jedis.Jedis;

public class JedisPublisher {
    Jedis jedis;
    public JedisPublisher() {
        RedisUtil.getJedis();
    }

    /**
     *
     * @param channel 频道
     * @param message 信息
     */
    public void sendMessage(String channel,String message) {
        Long countSubscribe = jedis.publish(channel,message); // 发送消息顺便返回订阅者数量
    }
}
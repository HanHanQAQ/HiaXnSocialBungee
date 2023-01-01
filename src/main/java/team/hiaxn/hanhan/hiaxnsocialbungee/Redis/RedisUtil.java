package team.hiaxn.hanhan.hiaxnsocialbungee.Redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import team.hiaxn.hanhan.hiaxnsocialbungee.HiaXnSocialBungee;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class RedisUtil {
    private static  JedisPool jedisPool = null;
    public final static Lock lock = new ReentrantLock();
    private static final String host = HiaXnSocialBungee.getInstance().getConfig().getString("Redis.ip");
    private static final Integer port = HiaXnSocialBungee.getInstance().getConfig().getInt("Redis.port");
    private static final Integer timeOut = 5 * 1000;
    private static String password = null;

    private RedisUtil(){

    }
    public static Jedis getJedis(){
        lock.lock();
        if(jedisPool == null){
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(20);
            jedisPoolConfig.setMaxTotal(100);
            jedisPoolConfig.setTestOnBorrow(true);
            jedisPool = new JedisPool(jedisPoolConfig,host,port,timeOut,password);
        }
        lock.unlock();
        return jedisPool.getResource();
    }
}
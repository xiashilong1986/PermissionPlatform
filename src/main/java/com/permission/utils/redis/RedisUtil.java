package com.permission.utils.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-06-20   *
 * * Time: 10:37        *
 * * to: lz&xm          *
 * **********************
 **/
@Component
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    // 维护一个本类的静态变量
    private static RedisUtil redisUtil;

    @Autowired
    public RedisUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        redisUtil = this;
    }


    /**
     * 将参数中的字符串值设置为键的值，设置过期时间
     *
     * @param key     主键
     * @param value   必须要实现 Serializable 接口
     * @param timeout 超时时间 (毫秒)
     */
    public static void set(String key, String value, Long timeout) {
        redisUtil.redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取与指定键相关的值
     *
     * @param key 主键
     * @return Object
     */
    public static Object get(String key) {
        return redisUtil.redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取指定key的过期时间
     *
     * @param key 主键
     * @return 过期时间 毫秒
     */
    public static Long getExpire(String key) {
        return redisUtil.redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }

    /**
     * 删除指定的键
     *
     * @param key 主键
     * @return boolean
     */
    public static Boolean delete(String key) {
        return redisUtil.redisTemplate.delete(key);
    }

    /**
     * 批量删除指定键
     *
     * @param keys 主键集合
     * @return Long 删除的数量
     */
    public static Long delete(Collection<String> keys) {
        return redisUtil.redisTemplate.delete(keys);
    }

    /**
     * 判断某个键是否存在
     *
     * @param key 主键
     */
    public static Boolean hasKey(String key) {
        return redisUtil.redisTemplate.hasKey(key);
    }

    @Bean
    public void stringSerializerRedisTemplate() {
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
    }
}

package com.permission.utils.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
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
     * @param timeout 超时时间 (毫秒) time要大于0 如果time小于等于0 将设置无限期
     */
    public static void set(String key, String value, long timeout) {
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
     * @return 过期时间 毫秒 返回0代表为永久有效
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

    /**
     * 设置过期时间
     *
     * @param key     主键
     * @param timeout 过期时间 (毫秒)
     */
    public static void expire(String key, long timeout) {
        redisUtil.redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }

    //集合操作

    /**
     * list 获取数据
     *
     * @param key   主键
     * @param start 下标开始
     * @param end   下标结束 0 到 -1代表所有值
     * @return List
     */
    public static List<String> listGet(String key, long start, long end) {
        return redisUtil.redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * list 获取指定下标数据
     *
     * @param key   主键
     * @param index 下标
     * @return String
     */
    public static String listGet(String key, long index) {
        return redisUtil.redisTemplate.opsForList().index(key, index);
    }

    /**
     * list 插入数据
     *
     * @param key   主键
     * @param value 数据
     */
    public static void listSet(String key, String value) {
        redisUtil.redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * list 批量插入
     *
     * @param key    主键
     * @param values 数据集合
     */
    public static void listSet(String key, Collection<String> values) {
        redisUtil.redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * list 长度
     *
     * @param key 主键
     * @return 长度
     */
    public static Long listSize(String key) {
        return redisUtil.redisTemplate.opsForList().size(key);
    }

    /**
     * list 删除集合中的指定数据
     *
     * @param key   主键
     * @param value 数据
     */
    public static void listRemove(String key, String value) {
        redisUtil.redisTemplate.opsForList().remove(key, 1, value);
    }

    /**
     * list 删除集合中的指定数据
     *
     * @param key    主键
     * @param values 数据集合
     */
    public static void listRemove(String key, Collection<String> values) {
        for (String value : values) {
            listRemove(key, value);
        }
    }

    /**
     * list 删除下标之前的数据
     *
     * @param key   主键
     * @param index 集合下标
     */
    public static void listRemove(String key, long index) {
        Long size = listSize(key);
        if (index > size) {
            throw new RuntimeException("redis index out of bounds");
        }
        redisUtil.redisTemplate.opsForList().trim(key, index, size);
    }


    @Bean
    public void stringSerializerRedisTemplate() {
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
    }
}

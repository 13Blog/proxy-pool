package com.spider.proxypool.db.repositor;

import com.spider.proxypool.db.config.RedisConfiguration;
import com.spider.proxypool.entity.ProxyEntity;
import com.google.common.base.Strings;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

/**
 * Created by 13 on 17-10-11.
 */
public class ProxyRepository {

    private static ProxyRepository REPOSITORY = new ProxyRepository();

    public static ProxyRepository getInstance() {
        return REPOSITORY;
    }

    private ProxyRepository() {
        this.deleteAll();
    }

    private RedisTemplate<String, ProxyEntity> redisTemplate = RedisConfiguration.getRedisTemplate();

    public void save(ProxyEntity proxy) {
        proxy.setUsable(true);
        proxy.setLastValidateTime(new Date());
        redisTemplate.opsForValue().set(getKey(proxy), proxy);
    }


    private ProxyEntity getByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public ProxyEntity getRandomly() {
        //优先获取高匿ip
        if (getAnonymousCount() > 0) {
            List<ProxyEntity> proxys = new ArrayList<>();
            proxys = getAnonymousList(1);
            if (proxys.size() > 0) {
                return getAnonymousList(1).get(0);
            }
        }
        String key = redisTemplate.randomKey();
        return Strings.isNullOrEmpty(key) ? null : getByKey(key);
    }

    /**
     * 获取全部ip列表
     *
     * @param num
     * @return
     */
    public List<ProxyEntity> getList(int num) {
        return getPatternProxyEntities(num, "*");
    }

    /**
     * 获取高匿ip列表
     *
     * @param num
     * @return
     */
    public List<ProxyEntity> getAnonymousList(int num) {
        return getPatternProxyEntities(num, "匿");
    }

    private List<ProxyEntity> getPatternProxyEntities(int num, String pattern) {
        List<ProxyEntity> proxys = new ArrayList<>();
        Set<String> keys = redisTemplate.keys(pattern);
        Iterator<String> it = keys.iterator();
        if (num < 0) {
            num = keys.size();
        }
        while (it.hasNext() && num-- > 0) {
            proxys.add(getByKey(it.next()));
        }
        return proxys;
    }

    public List<ProxyEntity> getAll() {
        return getList(-1);
    }

    /**
     * 获取全部ip数量
     *
     * @return
     */
    public int getCount() {
        return redisTemplate.keys("*").size();
    }

    /**
     * 获取高匿ip数量
     *
     * @param
     * @return
     */
    public int getAnonymousCount() {
        return redisTemplate.keys("匿").size();
    }

    public void delete(ProxyEntity b) {
        deleteByKey(getKey(b));
    }

    public void deleteByKey(String key) {
        if (!Strings.isNullOrEmpty(key))
            redisTemplate.opsForValue().getOperations().delete(key);
    }

    public void deleteAll() {
        Set<String> keys = redisTemplate.keys("*");
        Iterator<String> it = keys.iterator();

        while (it.hasNext()) {
            redisTemplate.opsForValue().getOperations().delete(it.next());
        }
    }


    private static String getKey(ProxyEntity proxy) {
        return proxy.getIp() + ":" + proxy.getPort() + "type" + proxy.getAgentType();
    }

}

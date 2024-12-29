package vttp.miniproject.hodlscout.runner;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Repository
public class CoinListRepository {

    @Autowired
    @Qualifier("redis-string")
    RedisTemplate<String, String> redisTemplate;

    // RPUSH REDIS_KEY_COINS_ID_NAME_LIST_CACHE coin_list
    public void cacheCoinNameAndIdList(List<String> coinList) {
        redisTemplate.opsForList().rightPushAll(REDIS_KEY_COINS_ID_NAME_LIST_CACHE, coinList);
        redisTemplate.expire(REDIS_KEY_COINS_ID_NAME_LIST_CACHE, Duration.ofDays(1));
    }

    // EXISTS REDIS_KEY_COINS_ID_NAME_LIST_CACHE
    public boolean coinNameAndIdListExist() {
        return redisTemplate.hasKey(REDIS_KEY_COINS_ID_NAME_LIST_CACHE);
    }

    // LRANGE REDIS_KEY_COINS_ID_NAME_LIST_CACHE 0 -1
    public List<String> getCoinNameAndIdList() {
        return redisTemplate.opsForList().range(REDIS_KEY_COINS_ID_NAME_LIST_CACHE, 0, -1);
    }

    // DEL REDIS_KEY_COINS_ID_NAME_LIST_CACHE
    public void deleteCoinNameAndIdList() {
        redisTemplate.delete(REDIS_KEY_COINS_ID_NAME_LIST_CACHE);
    }

    // RPUSH REDIS_KEY_COINS_ID_LIST_CACHE coinIdList
    public void cacheCoinIdList(List<String> coinIdList) {
        redisTemplate.opsForList().rightPushAll(REDIS_KEY_COINS_ID_LIST_CACHE, coinIdList);
        redisTemplate.expire(REDIS_KEY_COINS_ID_LIST_CACHE, Duration.ofDays(1));
    }

    // LRANGE REDIS_KEY_COINS_ID_LIST_CACHE 0 -1
    public List<String> getCoinIdList() {
        return redisTemplate.opsForList().range(REDIS_KEY_COINS_ID_LIST_CACHE, 0, -1);
    }

    // DEL REDIS_KEY_COINS_ID_LIST_CACHE
    public void deleteCoinIdList() {
        redisTemplate.delete(REDIS_KEY_COINS_ID_LIST_CACHE);
    }

}

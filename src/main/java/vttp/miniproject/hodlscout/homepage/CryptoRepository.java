package vttp.miniproject.hodlscout.homepage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Repository
public class CryptoRepository {

    @Autowired
    @Qualifier("redis-string")
    private RedisTemplate<String, String> redisTemplate;

    // RPUSH REDIS_KEY_COINS_MARKET_CACHE coin_data
    public void cacheCoinsMarketData(List<String> coins) {
        redisTemplate.opsForList().rightPushAll(REDIS_KEY_COINS_MARKET_CACHE, coins);
        // redisTemplate.expire(REDIS_KEY_COINS_MARKET_CACHE, Duration.ofMinutes(10));
    }

    // LRANGE REDIS_KEY_COINS_MARKET_CACHE start end
    public List<String> getCoinsMarketData(int start, int end) {
        return redisTemplate.opsForList().range(REDIS_KEY_COINS_MARKET_CACHE, start, end);
    }

    // EXISTS key
    public boolean keyExist(String key) {
        return redisTemplate.hasKey(key);
    }

    // LLEN key
    public long getListSize(String key) {
        if (keyExist(key))
            return redisTemplate.opsForList().size(key);
        else
            return -1;
    }
    
}

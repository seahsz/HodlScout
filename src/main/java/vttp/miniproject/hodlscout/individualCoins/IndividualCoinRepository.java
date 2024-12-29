package vttp.miniproject.hodlscout.individualCoins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Repository
public class IndividualCoinRepository {

    @Autowired
    @Qualifier("redis-string")
    private RedisTemplate<String, String> redisTemplate;

    public boolean doesCoinExist(String coinId) {
        return redisTemplate.opsForHash().hasKey(REDIS_KEY_INDIVIDUAL_COIN_CACHE, coinId);
    }

    public void cacheIndividualCoin(String coinId, String payload) {
        redisTemplate.opsForHash().put(REDIS_KEY_INDIVIDUAL_COIN_CACHE, coinId, payload);
    }

    public String getIndividualCoin(String coinId) {
        return (String) redisTemplate.opsForHash().get(REDIS_KEY_INDIVIDUAL_COIN_CACHE, coinId);
    }
    
}

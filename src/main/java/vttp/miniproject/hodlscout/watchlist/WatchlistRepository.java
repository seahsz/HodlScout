package vttp.miniproject.hodlscout.watchlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WatchlistRepository {

    @Autowired
    @Qualifier("redis-string")
    private RedisTemplate<String, String> redisTemplate;

    // hset username "watchlist" watchlistsAsJsonArray
    public void saveWatchlists(String username, String watchlists) {
        redisTemplate.opsForHash().put(username, "watchlist", watchlists);
    }

    // hexists username watchlist
    public boolean watchlistHashKeyExist(String username) {
        return redisTemplate.opsForHash().hasKey(username, "watchlist");
    }

    // hget username watchlist
    public String getWatchlists(String username) {
        return (String) redisTemplate.opsForHash().get(username, "watchlist");
    }

    // hdel username watchlist
    public void deleteWatchlistHashkey(String username) {
        redisTemplate.opsForHash().delete(username, "watchlist");
    }

}

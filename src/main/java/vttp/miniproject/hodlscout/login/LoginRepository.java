package vttp.miniproject.hodlscout.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Repository
public class LoginRepository {

    @Autowired
    @Qualifier("redis-string")
    private RedisTemplate<String, String> redisTemplate;

    // hset "user" (username) (password)
    public void saveUser(UserModel newUser) {
        redisTemplate.opsForHash().put(REDIS_KEY_USER, newUser.getUsername(), newUser.getPassword());
    }

    // hexists "user" (username)
    public boolean hasUser(UserModel user) {
        return redisTemplate.opsForHash().hasKey(REDIS_KEY_USER, user.getUsername());
    }

    // hget "user" (username)
    public String getPassword(UserModel user) {
        return (String) redisTemplate.opsForHash().get(REDIS_KEY_USER, user.getUsername());
    } 
    
}

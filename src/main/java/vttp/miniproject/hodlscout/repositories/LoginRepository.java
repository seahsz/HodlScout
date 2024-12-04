package vttp.miniproject.hodlscout.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import vttp.miniproject.hodlscout.models.User;

import static vttp.miniproject.hodlscout.utilities.Constants.*;

@Repository
public class LoginRepository {

    @Autowired
    @Qualifier("redis-string")
    private RedisTemplate<String, String> redisTemplate;

    // hset "user" (username) (password)
    public void saveUser(User newUser) {

        redisTemplate.opsForHash().put(REDIS_KEY_USER, newUser.getUsername(), newUser.getPassword());
    }
    
}

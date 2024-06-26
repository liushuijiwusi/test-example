package armory.checkalive;

import org.redisson.api.RedissonClient;

public interface CheckAliveConfig {

    RedissonClient checkAliveRedissonClient();
}

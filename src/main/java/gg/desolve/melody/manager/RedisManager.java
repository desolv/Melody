package gg.desolve.melody.manager;

import com.mongodb.Function;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CompletableFuture;

import static gg.desolve.melody.Melody.instance;

@Getter
public class RedisManager {

    private static JedisPool jedisPool;

    public RedisManager(String url) {
        try {
            long start = System.currentTimeMillis();

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(50);
            config.setMaxIdle(25);
            config.setMinIdle(5);
            config.setTestOnBorrow(true);

            jedisPool = new JedisPool(config, url);

            instance.getLogger().info("Connected to Redis in " + (System.currentTimeMillis() - start) + "ms.");
        } catch (Exception e) {
            instance.getLogger().info("There was a problem connecting to Redis.");
            e.printStackTrace();
        }
    }

    public <T> CompletableFuture<T> withRedisAsync(Function<Jedis, T> action) {
        return CompletableFuture.supplyAsync(() -> {
            if (jedisPool == null) return null;
            try (Jedis j = jedisPool.getResource()) {
                return action.apply(j);
            } catch (Exception e) {
                instance.getLogger().warning("An error occurred during a Redis query.");
                e.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<Long> publishAsync(String channel, String payload) {
        return withRedisAsync(j -> j.publish(channel, payload));
    }

    public AutoCloseable subscribeAsync(JedisPubSub listener, String... channels) {
        Thread t = new Thread(() -> {
            try (Jedis j = jedisPool.getResource()) {
                j.subscribe(listener, channels);
            } catch (Exception e) {
                instance.getLogger().warning("An error occurred during a Redis sub.");
                e.printStackTrace();
            }
        }, "melody-redis-sub");
        t.setDaemon(true);
        t.start();
        return listener::unsubscribe;
    }
}

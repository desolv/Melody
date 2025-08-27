package gg.desolve.melody;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.standard.StandardSerdes;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import gg.desolve.melody.config.MelodyConfig;
import gg.desolve.melody.manager.MongoManager;
import gg.desolve.melody.manager.RedisManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Melody extends JavaPlugin {

    @Getter
    public static Melody instance;

    @Getter
    public MelodyConfig melodyConfig;

    @Getter
    public MongoManager mongoManager;

    @Getter
    public RedisManager redisManager;

    @Override
    public void onEnable() {
        instance = this;

        loadConfigs();

        mongoManager = new MongoManager(
                melodyConfig.mongo.url,
                melodyConfig.mongo.database
        );

        redisManager = new RedisManager(melodyConfig.redis.url);
    }

    private void loadConfigs() {
        melodyConfig = ConfigManager.create(MelodyConfig.class, it -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer(), new StandardSerdes());
            it.withBindFile(getDataFolder().toPath().resolve("config.yml"));
            it.saveDefaults();
            it.load(true);
        });

        this.getLogger().info("Successfully loaded configs.");
    }

}

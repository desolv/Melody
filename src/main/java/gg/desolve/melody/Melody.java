package gg.desolve.melody;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.standard.StandardSerdes;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import gg.desolve.melody.command.param.OnlinePlayerType;
import gg.desolve.melody.command.ReportCommand;
import gg.desolve.melody.config.MelodyConfig;
import gg.desolve.melody.manager.MongoManager;
import gg.desolve.melody.manager.RedisManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.BukkitLampConfig;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.util.List;

public final class Melody extends JavaPlugin {

    @Getter
    public static Melody instance;

    @Getter
    public MelodyConfig melodyConfig;

    @Getter
    public MongoManager mongoManager;

    @Getter
    public RedisManager redisManager;

    @Getter
    public Lamp<BukkitCommandActor> lamp;

    @Override
    public void onEnable() {
        instance = this;

        loadConfigs();

        mongoManager = new MongoManager(
                melodyConfig.mongo.url,
                melodyConfig.mongo.database
        );

        redisManager = new RedisManager(melodyConfig.redis.url);

        loadCommands();
    }

    private void loadConfigs() {
        melodyConfig = ConfigManager.create(MelodyConfig.class, it -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer(), new StandardSerdes());
            it.withBindFile(getDataFolder().toPath().resolve("config.yml"));
            it.saveDefaults();
            it.load(true);
        });
    }

    private void loadCommands() {
        BukkitLampConfig<BukkitCommandActor> config = BukkitLampConfig.builder(this)
                .disableBrigadier()
                .build();

        lamp = BukkitLamp.builder(config).build();

        this.getLogger().info("Hooked into Lamp.");

        List.of(
                //
        ).forEach(lamp::register);
    }

}

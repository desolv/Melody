package gg.desolve.melody.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.util.Arrays;
import java.util.List;

public class MelodyConfig extends OkaeriConfig {

    public Mongo mongo = new Mongo();

    @Comment("")
    public Redis redis = new Redis();

    @Comment("")
    public String server_name = "melody";

    @Comment("")
    public ReportConfig report = new ReportConfig();

    public static class Mongo extends OkaeriConfig {
        public String url = "mongodb://localhost:27017";
        public String database = "melody";
    }

    public static class Redis extends OkaeriConfig {
        public String url = "redis://127.0.0.1:6379";
    }

    public static class ReportConfig extends OkaeriConfig {
        public String title = "<yellow><bold>Reporting</bold> <gray>›› target%";
        public List<ReportCategory> categories = Arrays.asList(
                new ReportCategory(
                        "Combat Hacks",
                        "<yellow><bold>Combat Hacks",
                        "IRON_SWORD",
                        List.of("<gray>KillAura, Aimbot, etc")
                ),
                new ReportCategory(
                        "Chat Abuse",
                        "<yellow><bold>Chat Abuse",
                        "PAPER",
                        List.of("<gray>Profanity, harassment, spam")
                )
        );
    }

    public static class ReportCategory extends OkaeriConfig {
        public String name;
        public String display;
        public String material;
        public List<String> description;

        public ReportCategory(String name, String display, String material, List<String> description) {
            this.name = name;
            this.display = display;
            this.material = material;
            this.description = description;
        }
    }
}

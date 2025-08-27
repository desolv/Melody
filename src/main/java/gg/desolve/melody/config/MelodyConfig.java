package gg.desolve.melody.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class MelodyConfig extends OkaeriConfig {

    public Mongo mongo = new Mongo();

    @Comment("")
    public Redis redis = new Redis();

    public static class Mongo extends OkaeriConfig {
        public String url = "mongodb://localhost:27017";
        public String database = "melody";
    }

    public static class Redis extends OkaeriConfig {
        public String url = "redis://127.0.0.1:6379";
    }

}

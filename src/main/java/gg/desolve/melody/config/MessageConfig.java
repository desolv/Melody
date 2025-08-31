package gg.desolve.melody.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.util.Arrays;
import java.util.List;

public class MessageConfig extends OkaeriConfig {

    public ReportConfig report_gui = new ReportConfig();

    @Comment("")
    public String report_created = "<green>A report has been submitted to staff.";
    public String report_created_staff = "<yellow>target% <red>has been reported by <yellow>reporter% <red>for <gold>reason%<red>.";
    public String report_resolved = "<green>A report against a player you reported has been resolved.";

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

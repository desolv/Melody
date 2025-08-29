package gg.desolve.melody.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class Message {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    public static Component translate(String message) {
        return MM.deserialize(message);
    }

    public static String translateLegacy(String message) {
        return LEGACY.serialize(MM.deserialize(message));
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(translateLegacy(message));
    }
}

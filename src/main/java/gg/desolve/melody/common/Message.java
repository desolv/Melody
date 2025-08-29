package gg.desolve.melody.common;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class Message {

    public static void sendMessage(Player player, String message) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }
}

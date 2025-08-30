package gg.desolve.melody.common;

import org.bukkit.Bukkit;

import java.util.UUID;

public class Converter {

    public static String generateId() {
        String randomId = UUID.randomUUID().toString()
                .replaceAll("-", "")
                .toLowerCase();
        return randomId.substring(0, Math.min(12, randomId.length()));
    }

    public static String getBestName(UUID uuid) {
        var online = Bukkit.getPlayer(uuid);

        if (online != null)
            return online.getName();

        var offline = Bukkit.getOfflinePlayer(uuid);

        return offline.getName() != null ? offline.getName() : uuid.toString();
    }
}

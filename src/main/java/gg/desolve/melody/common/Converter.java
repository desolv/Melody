package gg.desolve.melody.common;

import java.util.UUID;

public class Converter {

    public static String generateId() {
        String randomId = UUID.randomUUID().toString()
                .replaceAll("-", "")
                .toLowerCase();
        return randomId.substring(0, Math.min(12, randomId.length()));
    }

}

package gg.desolve.melody.common;

public final class EnumCycle {

    public static <T extends Enum<T>> T next(Class<T> enumClass, T current) {
        T[] values = enumClass.getEnumConstants();

        if (current == null) return values[0];

        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}

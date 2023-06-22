package me.kallix.profilefetcher.utils;

public final class Validate {

    public static void isTrue(boolean condition, String error) {
        if (!condition) {
            throw new IllegalArgumentException(error);
        }
    }

    public static void notNull(Object object, String error) {
        if (object == null) {
            throw new IllegalArgumentException(error);
        }
    }
}

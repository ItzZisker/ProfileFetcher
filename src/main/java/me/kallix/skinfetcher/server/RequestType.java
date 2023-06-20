package me.kallix.skinfetcher.server;

import me.kallix.skinfetcher.textures.Textures;

import java.net.URL;
import java.util.UUID;

public enum RequestType {

    USERNAME_TO_UUID,
    USERNAME_TO_PROFILE,
    UUID_TO_PROFILE,
    URL_TO_PROFILE;

    public static <T, R> RequestType byGeneric(Class<T> from, Class<R> to) {
        if (from.equals(String.class) && to.equals(UUID.class)) {
            return USERNAME_TO_UUID;
        } else if (to.equals(Textures.class)) {
            if (from.equals(String.class)) {
                return USERNAME_TO_PROFILE;
            } else if (from.equals(UUID.class)) {
                return UUID_TO_PROFILE;
            } else if (from.equals(URL.class)) {
                return URL_TO_PROFILE;
            }
        }
        throw new IllegalArgumentException();
    }
}

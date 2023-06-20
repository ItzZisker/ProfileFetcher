package me.kallix.skinfetcher.textures;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public final class Textures {

    @Getter
    private final Texture[] all;

    public Textures(Texture... all) {
        this.all = all;
    }

    public Textures(int size) {
        this.all = new Texture[size];
    }

    public Texture getMain() {
        return all[0];
    }

    @RequiredArgsConstructor
    public static class Texture {
        public final String signature;
        public final String value;
    }
}

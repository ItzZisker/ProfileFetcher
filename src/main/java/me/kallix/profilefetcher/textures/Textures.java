package me.kallix.profilefetcher.textures;

import lombok.RequiredArgsConstructor;

public final class Textures {

    public final Texture[] data;

    public Textures(Texture... data) {
        this.data = data;
    }

    public Textures(int size) {
        this.data = new Texture[size];
    }

    public Texture getMain() {
        return data[0];
    }

    @RequiredArgsConstructor
    public static class Texture {
        public final String signature;
        public final String value;
    }
}

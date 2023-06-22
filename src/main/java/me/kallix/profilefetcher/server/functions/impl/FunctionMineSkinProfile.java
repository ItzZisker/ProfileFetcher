package me.kallix.profilefetcher.server.functions.impl;

import lombok.RequiredArgsConstructor;
import me.kallix.profilefetcher.server.functions.RequestFunction;
import me.kallix.profilefetcher.textures.Textures;
import org.mineskin.MineskinClient;
import org.mineskin.data.Texture;

import java.net.URL;

@RequiredArgsConstructor
public final class FunctionMineSkinProfile implements RequestFunction<URL, Textures> {

    private final MineskinClient client;

    @Override
    public Textures apply(URL url) throws Exception {
        Texture texture = client.generateUrl(url.toString()).get().data.texture;
        return new Textures(new Textures.Texture(texture.signature, texture.value));
    }
}

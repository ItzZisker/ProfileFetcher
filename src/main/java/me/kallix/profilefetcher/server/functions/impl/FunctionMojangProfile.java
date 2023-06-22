package me.kallix.profilefetcher.server.functions.impl;

import lombok.RequiredArgsConstructor;
import me.kallix.profilefetcher.server.functions.RequestFunction;
import me.kallix.profilefetcher.server.functions.exceptions.MojangErrorResponse;
import me.kallix.profilefetcher.textures.Textures;
import me.kallix.profilefetcher.utils.NetUtils;
import org.mineskin.com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class FunctionMojangProfile implements RequestFunction<UUID, Textures> {

    private final String url;
    private final long timeOut;

    @Override
    @SuppressWarnings("unchecked")
    public Textures apply(UUID uuid) throws Exception {

        byte[] body = NetUtils.httpGET(String.format(url, uuid.toString()), (int) timeOut);
        Map<?, ?> map = new Gson().fromJson(new String(body), Map.class);

        if (map.get("error") != null) {
            throw new MojangErrorResponse((String) map.get("error"));
        }

        Map<String, String> properties = ((List<Map<String, String>>) map.get("properties")).get(0);

        if (properties != null && !properties.isEmpty()) {
            return new Textures(new Textures.Texture(properties.get("signature"), properties.get("value")));
        } else {
            throw new NullPointerException();
        }
    }
}

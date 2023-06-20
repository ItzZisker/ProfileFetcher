package me.kallix.skinfetcher.server.functions.impl;

import lombok.RequiredArgsConstructor;
import me.kallix.skinfetcher.server.functions.RequestFunction;
import me.kallix.skinfetcher.server.functions.exceptions.MineToolsErrorResponse;
import me.kallix.skinfetcher.textures.Textures;
import me.kallix.skinfetcher.utils.NetUtils;
import org.mineskin.com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class FunctionMineToolsProfile implements RequestFunction<UUID, Textures> {

    private final String url;
    private final long timeOut;

    @Override
    @SuppressWarnings("unchecked")
    public Textures apply(UUID uuid) throws Exception {

        byte[] body = NetUtils.httpGET(String.format(url, uuid.toString()), (int) timeOut);

        Map<?, ?> map = new Gson().fromJson(new String(body), Map.class);

        if (map.get("status") != null && map.get("status").equals("ERR")) {
            throw new MineToolsErrorResponse("ERR");
        }

        Map<String, String> profile = ((List<Map<String, String>>) ((Map<?, ?>) map.get("raw")).get("properties")).get(0);

        return new Textures(new Textures.Texture(profile.get("signature"), profile.get("value")));
    }
}

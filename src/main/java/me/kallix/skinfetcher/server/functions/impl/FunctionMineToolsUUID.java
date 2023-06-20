package me.kallix.skinfetcher.server.functions.impl;

import lombok.RequiredArgsConstructor;
import me.kallix.skinfetcher.server.functions.RequestFunction;
import me.kallix.skinfetcher.utils.NetUtils;
import org.mineskin.com.google.gson.Gson;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class FunctionMineToolsUUID implements RequestFunction<String, UUID> {

    private final String url;
    private final long timeOut;

    @Override
    public UUID apply(String username) throws Exception {

        byte[] body = NetUtils.httpGET(String.format(url, username), (int) timeOut);
        Map<?, ?> map = new Gson().fromJson(new String(body), Map.class);

        if (map.get("id") != null) {
            return UUID.fromString((String) map.get("id"));
        } else {
            throw new NullPointerException();
        }
    }
}
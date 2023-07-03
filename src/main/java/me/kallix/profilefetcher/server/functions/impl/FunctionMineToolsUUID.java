package me.kallix.profilefetcher.server.functions.impl;

import lombok.RequiredArgsConstructor;
import me.kallix.profilefetcher.server.functions.RequestFunction;
import me.kallix.profilefetcher.utils.NetUtils;
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
            return UUID.fromString(((String) map.get("id")).replaceAll(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                    "$1-$2-$3-$4-$5"));
        } else {
            throw new NullPointerException();
        }
    }
}

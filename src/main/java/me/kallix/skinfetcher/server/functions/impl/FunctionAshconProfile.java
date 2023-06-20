package me.kallix.skinfetcher.server.functions.impl;

import lombok.RequiredArgsConstructor;
import me.kallix.skinfetcher.server.functions.RequestFunction;
import me.kallix.skinfetcher.server.functions.exceptions.AshconErrorResponse;
import me.kallix.skinfetcher.textures.Textures;
import me.kallix.skinfetcher.utils.NetUtils;
import org.mineskin.com.google.gson.Gson;

import java.util.Map;

@RequiredArgsConstructor
public class FunctionAshconProfile implements RequestFunction<Object, Textures> {

    private final String url;
    private final long timeOut;

    @Override
    @SuppressWarnings("unchecked")
    public Textures apply(Object uuidOrName) throws Exception {

        byte[] body = NetUtils.httpGET(String.format(url, uuidOrName.toString()), (int) timeOut);
        Map<?, ?> data = new Gson().fromJson(new String(body), Map.class);

        if (data.get("error") != null) {
            throw new AshconErrorResponse((String) data.get("error"));
        }

        Map<String, String> raw = (Map<String, String>) ((Map<?, ?>) data.get("textures")).get("raw");

        return new Textures(new Textures.Texture(raw.get("signature"), raw.get("value")));
    }
}

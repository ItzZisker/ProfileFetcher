package me.kallix.profilefetcher.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.kallix.profilefetcher.server.functions.RequestFunction;

@RequiredArgsConstructor
@Getter
public final class RequestHandler<T, R> {

    private final RequestType type;
    private final RequestFunction<T, R> function;
    private final int weight;
}

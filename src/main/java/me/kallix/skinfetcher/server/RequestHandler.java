package me.kallix.skinfetcher.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.kallix.skinfetcher.server.functions.RequestFunction;

@RequiredArgsConstructor
@Getter
public final class RequestHandler<T, R> {

    private final RequestType type;
    private final RequestFunction<T, R> function;
    private final int weight;
}

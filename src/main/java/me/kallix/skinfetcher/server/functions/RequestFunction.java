package me.kallix.skinfetcher.server.functions;

@FunctionalInterface
public interface RequestFunction<T, R> {

    R apply(T from) throws Exception;
}

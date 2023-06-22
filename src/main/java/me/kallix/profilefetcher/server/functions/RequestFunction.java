package me.kallix.profilefetcher.server.functions;

@FunctionalInterface
public interface RequestFunction<T, R> {

    R apply(T from) throws Exception;
}

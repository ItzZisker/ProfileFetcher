package me.kallix.profilefetcher.server;

import org.mineskin.com.google.common.collect.Lists;

import java.util.*;

public final class APIRegistry {

    private final Map<RequestType, List<RequestHandler<?, ?>>> byType = new EnumMap<>(RequestType.class);

    public void registerAll(Collection<RequestHandler<?, ?>> all) {
        all.forEach(this::register);
    }

    public <T, R> void register(RequestHandler<T, R> server) {
        List<RequestHandler<?, ?>> servers = byType.computeIfAbsent(server.getType(), k -> Lists.newArrayList());

        servers.add(server);
        servers.sort((a1, a2) -> a2.getWeight() - a1.getWeight());
    }

    @SuppressWarnings("unchecked")
    public <T, R> Optional<RequestHandler<T, R>> get(RequestType type, int index) {
        List<RequestHandler<?, ?>> servers = byType.getOrDefault(type, Collections.emptyList());
        return index < servers.size() ? Optional.of((RequestHandler<T, R>) servers.get(index)) : Optional.empty();
    }
}

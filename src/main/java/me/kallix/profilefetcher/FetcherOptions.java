package me.kallix.profilefetcher;

import lombok.Getter;
import me.kallix.profilefetcher.server.RequestHandler;
import me.kallix.profilefetcher.server.RequestType;
import me.kallix.profilefetcher.server.functions.impl.*;
import org.mineskin.MineskinClient;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
public final class FetcherOptions {

    private final Set<RequestHandler<?, ?>> requesters = new HashSet<>();

    private Executor requestExecutor;
    private boolean ignoreErrors;

    public static FetcherOptions create() {
        return new FetcherOptions();
    }

    public static FetcherOptions defaults() {
        return FetcherOptions.create()
                .setRequestExecutor(Executors.newSingleThreadExecutor())
                .withDefaultRequesters(Duration.ofSeconds(5))
                .shouldIgnoreErrors(true);
    }

    public FetcherOptions withDefaultRequesters(Duration timeout) {
        long timeoutMillis = timeout.toMillis();

        register(new RequestHandler<>(RequestType.USERNAME_TO_UUID, new FunctionMojangUUID("https://api.mojang.com/users/profiles/minecraft/%s", timeoutMillis), 1000));
        register(new RequestHandler<>(RequestType.USERNAME_TO_UUID, new FunctionMineToolsUUID("https://api.mojang.com/users/profiles/minecraft/%s", timeoutMillis), 900));
        register(new RequestHandler<>(RequestType.UUID_TO_PROFILE, new FunctionMojangProfile("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", timeoutMillis), 1000));
        register(new RequestHandler<>(RequestType.UUID_TO_PROFILE, new FunctionMineToolsProfile("https://api.minetools.eu/profile/%s", timeoutMillis), 900));
        register(new RequestHandler<>(RequestType.UUID_TO_PROFILE, new FunctionAshconProfile("https://api.ashcon.app/mojang/v2/user/%s", timeoutMillis), 800));
        register(new RequestHandler<>(RequestType.USERNAME_TO_PROFILE, new FunctionAshconProfile("https://api.ashcon.app/mojang/v2/user/%s", timeoutMillis), 1000));
        register(new RequestHandler<>(RequestType.URL_TO_PROFILE, new FunctionMineSkinProfile(new MineskinClient("ProfileFetcher")), 1000));
        return this;
    }

    public <T, R> FetcherOptions register(RequestHandler<T, R> requester) {
        this.requesters.add(requester);
        return this;
    }

    public FetcherOptions setRequestExecutor(Executor executor) {
        this.requestExecutor = executor;
        return this;
    }
    
    public FetcherOptions shouldIgnoreErrors(boolean value) {
        this.ignoreErrors = value;
        return this;
    }
}

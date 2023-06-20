package me.kallix.skinfetcher;

import me.kallix.skinfetcher.server.APIRegistry;
import me.kallix.skinfetcher.server.RequestHandler;
import me.kallix.skinfetcher.server.RequestType;
import me.kallix.skinfetcher.server.functions.impl.*;
import me.kallix.skinfetcher.textures.Textures;
import me.kallix.skinfetcher.utils.Validate;
import org.mineskin.MineskinClient;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ProfileFetcher {

    private final Executor requestExecutor;
    private final APIRegistry apiRegistry;
    private final boolean ignoreErrors;

    public ProfileFetcher(FetcherOptions options) {
        long timeout = options.getTimeout().toMillis();

        this.requestExecutor = options.getRequestExecutor();
        this.ignoreErrors = options.isIgnoreErrors();
        this.apiRegistry = new APIRegistry();
        this.apiRegistry.registerAll(options.getServers());
        this.apiRegistry.register(new RequestHandler<>(RequestType.USERNAME_TO_UUID, new FunctionMojangUUID("https://api.mojang.com/users/profiles/minecraft/%s", timeout), 1000));
        this.apiRegistry.register(new RequestHandler<>(RequestType.USERNAME_TO_UUID, new FunctionMineToolsUUID("https://api.mojang.com/users/profiles/minecraft/%s", timeout), 900));
        this.apiRegistry.register(new RequestHandler<>(RequestType.UUID_TO_PROFILE, new FunctionMojangProfile("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", timeout), 1000));
        this.apiRegistry.register(new RequestHandler<>(RequestType.UUID_TO_PROFILE, new FunctionMineToolsProfile("https://api.minetools.eu/profile/%s", timeout), 900));
        this.apiRegistry.register(new RequestHandler<>(RequestType.UUID_TO_PROFILE, new FunctionAshconProfile("https://api.ashcon.app/mojang/v2/user/%s", timeout), 800));
        this.apiRegistry.register(new RequestHandler<>(RequestType.USERNAME_TO_PROFILE, new FunctionAshconProfile("https://api.ashcon.app/mojang/v2/user/%s", timeout), 1000));
        this.apiRegistry.register(new RequestHandler<>(RequestType.URL_TO_PROFILE, new FunctionMineSkinProfile(new MineskinClient(options.getMineSkinAgent())), 1000));
    }

    public static ProfileFetcher API() {
        return API(FetcherOptions.defaults());
    }

    public static ProfileFetcher API(FetcherOptions options) {
        validate(options);
        return new ProfileFetcher(options);
    }

    private static void validate(FetcherOptions options) {
        Validate.notNull(options, "Options cannot be null");
        Validate.notNull(options.getMineSkinAgent(), "Agent cannot be null");
        Validate.notNull(options.getTimeout(), "Timeout cannot be null");
        Validate.notNull(options.getRequestExecutor(), "Request executor cannot be null");
        Validate.isTrue(!options.getMineSkinAgent().isEmpty(), "Agent identifier cannot be empty");
        Validate.isTrue(options.getTimeout().toMillis() > 0, "Timeout must be greater than 0");
    }

    public CompletableFuture<Textures> fetchTexturesAsync(URL customURL) {
        return CompletableFuture.supplyAsync(() -> fetchTexturesSync(customURL), requestExecutor);
    }

    public CompletableFuture<Textures> fetchTexturesAsync(String name) {
        return CompletableFuture.supplyAsync(() -> fetchTexturesSync(name), requestExecutor);
    }

    public CompletableFuture<Textures> fetchTexturesAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> fetchTexturesSync(uuid), requestExecutor);
    }

    public CompletableFuture<UUID> fetchUUIDAsync(String username) {
        return CompletableFuture.supplyAsync(() -> fetchUUIDSync(username), requestExecutor);
    }

    public Textures fetchTexturesSync(String username) {
        Textures textures = fetchSync(username, Textures.class);
        return textures != null ? textures : fetchTexturesSync(fetchUUIDSync(username));
    }

    public Textures fetchTexturesSync(URL url) {
        return fetchSync(url, Textures.class);
    }

    public Textures fetchTexturesSync(UUID uuid) {
        return fetchSync(uuid, Textures.class);
    }

    public UUID fetchUUIDSync(String username) {
        return fetchSync(username, UUID.class);
    }

    private <T, R> R fetchSync(T from, Class<R> retType) {
        R result = null;

        for (int i = 0;; i++) {
            RequestHandler<T, R> server = apiRegistry.<T, R>get(RequestType.byGeneric(from.getClass(), retType), i).orElse(null);
            try {
                if (server == null || (result = server.getFunction().apply(from)) != null) {
                    break;
                }
            } catch (Throwable e) {
                if (!ignoreErrors) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }
}
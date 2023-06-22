package me.kallix.profilefetcher;

import me.kallix.profilefetcher.server.APIRegistry;
import me.kallix.profilefetcher.server.RequestHandler;
import me.kallix.profilefetcher.server.RequestType;
import me.kallix.profilefetcher.textures.Textures;
import me.kallix.profilefetcher.utils.Validate;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ProfileFetcher {

    private final Executor requestExecutor;
    private final APIRegistry apiRegistry;
    private final boolean ignoreErrors;

    public ProfileFetcher(FetcherOptions options) {
        this.requestExecutor = options.getRequestExecutor();
        this.ignoreErrors = options.isIgnoreErrors();
        this.apiRegistry = new APIRegistry();
        this.apiRegistry.registerAll(options.getRequesters());
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
        Validate.notNull(options.getRequestExecutor(), "Request executor cannot be null");
        Validate.isTrue(!options.getRequesters().isEmpty(), "Request handlers cannot be empty");
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
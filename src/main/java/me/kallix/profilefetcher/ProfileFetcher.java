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
        return fetchAsync(customURL, Textures.class);
    }

    public CompletableFuture<Textures> fetchTexturesAsync(String name) {
        return fetchAsync(name, Textures.class);
    }

    public CompletableFuture<Textures> fetchTexturesAsync(UUID uuid) {
        return fetchAsync(uuid, Textures.class);
    }

    public CompletableFuture<UUID> fetchUUIDAsync(String username) {
        return fetchAsync(username, UUID.class);
    }

    public Textures fetchTexturesSync(String username) throws Exception {
        return fetchSync(username, Textures.class);
    }

    public Textures fetchTexturesSync(URL url) throws Exception {
        return fetchSync(url, Textures.class);
    }

    public Textures fetchTexturesSync(UUID uuid) throws Exception {
        return fetchSync(uuid, Textures.class);
    }

    public UUID fetchUUIDSync(String username) throws Exception {
        return fetchSync(username, UUID.class);
    }

    private <T, R> CompletableFuture<R> fetchAsync(T from, Class<R> retType) {

        CompletableFuture<R> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                future.complete(fetchSync(from, retType));
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, requestExecutor);

        return future;
    }

    private <T, R> R fetchSync(T from, Class<R> retType) throws Exception {
        R result = null;

        for (int i = 0; ; i++) {
            RequestHandler<T, R> server = apiRegistry.<T, R>get(RequestType.byGeneric(from.getClass(), retType), i).orElse(null);
            try {
                if (server == null || (result = server.getFunction().apply(from)) != null) {
                    break;
                }
            } catch (Throwable e) {
                if (!ignoreErrors) {
                    throw e;
                }
            }
        }
        return result;
    }
}
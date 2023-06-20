package me.kallix.skinfetcher;

import lombok.Getter;
import me.kallix.skinfetcher.server.RequestHandler;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
public final class FetcherOptions {

    private final Set<RequestHandler<?, ?>> servers = new HashSet<>();

    private Executor requestExecutor;
    private Duration timeout;
    private String mineSkinAgent;
    private boolean ignoreErrors;

    public static FetcherOptions create() {
        return new FetcherOptions();
    }

    public static FetcherOptions defaults() {
        Duration timeOut = Duration.ofSeconds(5);
        return FetcherOptions.create()
                .setMineSkinAgent("ProfileFetcher Agent")
                .setRequestExecutor(Executors.newSingleThreadExecutor())
                .setTimeOut(timeOut);
    }

    public <T, R> FetcherOptions addRequester(RequestHandler<T, R> server) {
        this.servers.add(server);
        return this;
    }

    public FetcherOptions setRequestExecutor(Executor executor) {
        this.requestExecutor = executor;
        return this;
    }

    public FetcherOptions setMineSkinAgent(String agent) {
        this.mineSkinAgent = agent;
        return this;
    }
    
    public FetcherOptions setTimeOut(Duration duration) {
        this.timeout = duration;
        return this;
    }

    public FetcherOptions shouldIgnoreErrors(boolean value) {
        this.ignoreErrors = value;
        return this;
    }
}

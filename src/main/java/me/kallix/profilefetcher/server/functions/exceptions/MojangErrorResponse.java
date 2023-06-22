package me.kallix.profilefetcher.server.functions.exceptions;

public final class MojangErrorResponse extends Exception {
    public MojangErrorResponse(String reason) {
        super(reason);
    }
}

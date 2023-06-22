package me.kallix.profilefetcher.server.functions.exceptions;

public final class AshconErrorResponse extends Exception {
    public AshconErrorResponse(String reason) {
        super(reason);
    }
}

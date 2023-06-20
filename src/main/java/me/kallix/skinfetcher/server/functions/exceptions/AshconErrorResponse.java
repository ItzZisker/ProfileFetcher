package me.kallix.skinfetcher.server.functions.exceptions;

public final class AshconErrorResponse extends Exception {
    public AshconErrorResponse(String reason) {
        super(reason);
    }
}

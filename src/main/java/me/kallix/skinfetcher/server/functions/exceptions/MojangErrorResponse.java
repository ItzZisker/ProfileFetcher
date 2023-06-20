package me.kallix.skinfetcher.server.functions.exceptions;

public final class MojangErrorResponse extends Exception {
    public MojangErrorResponse(String reason) {
        super(reason);
    }
}

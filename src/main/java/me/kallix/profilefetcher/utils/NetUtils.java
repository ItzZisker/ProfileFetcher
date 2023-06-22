package me.kallix.profilefetcher.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public final class NetUtils {

    private static final int[] FINE_RESPONSE = new int[]{200, 201, 202, 302};
    private static final int CHUNK_SIZE = 1 << 12;

    public static byte[] httpGET(String url, int timeout) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

        conn.setRequestMethod("GET");
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        conn.setRequestProperty("User-Agent", "ProfileFetcher");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        validateResponse(conn.getResponseCode());

        return read(conn);
    }

    public static byte[] read(URLConnection url) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try (InputStream is = url.getInputStream()) {
            byte[] chunk = new byte[CHUNK_SIZE];
            int n;

            while ((n = is.read(chunk)) > 0) {
                stream.write(chunk, 0, n);
            }
        }
        return stream.toByteArray();
    }

    public static void validateResponse(int response) {
        if (Arrays.binarySearch(FINE_RESPONSE, response) < 0) {
            throw new IllegalStateException("Bad status code: " + response);
        }
    }
}

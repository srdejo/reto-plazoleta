package co.com.srdejo.plazoleta.infrastructure.configuration;

/**
 * Holds the Authorization header value captured synchronously from the original HTTP request,
 * so it can be forwarded from an {@code @Async} worker thread without touching the (possibly
 * already recycled by the servlet container) {@code HttpServletRequest} object.
 */
public final class AsyncAuthorizationContext {

    private static final ThreadLocal<String> AUTHORIZATION_HEADER = new ThreadLocal<>();

    private AsyncAuthorizationContext() {
    }

    public static void set(String authorizationHeader) {
        AUTHORIZATION_HEADER.set(authorizationHeader);
    }

    public static String get() {
        return AUTHORIZATION_HEADER.get();
    }

    public static void clear() {
        AUTHORIZATION_HEADER.remove();
    }
}

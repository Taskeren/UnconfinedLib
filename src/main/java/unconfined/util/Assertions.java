package unconfined.util;

import org.jspecify.annotations.Nullable;

@SuppressWarnings("unused")
public final class Assertions {

    public static void check(boolean condition) {
        if (!condition) {
            throw new IllegalStateException();
        }
    }

    public static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    public static <T> T checkNotNull(@Nullable T value) {
        if (value == null) throw new IllegalStateException();
        return value;
    }

    public static <T> T checkNotNull(@Nullable T value, String nameof) {
        if (value == null) throw new IllegalStateException(nameof + " is null");
        return value;
    }

    public static void require(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> T requireNotNull(@Nullable T value) {
        if (value == null) throw new IllegalArgumentException();
        return value;
    }

    public static <T> T requireNotNull(@Nullable T value, String nameof) {
        if (value == null) throw new IllegalArgumentException(nameof + " is null");
        return value;
    }

}

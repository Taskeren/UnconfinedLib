package unconfined.util;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntFunction;

@SuppressWarnings("unused")
public final class Utils {

    @Contract("_, _ -> param1")
    public static <T> T make(T value, Consumer<T> applier) {
        applier.accept(value);
        return value;
    }

    /// Construct a value-filled array.
    ///
    /// ```java
    /// makeArray(
    ///     new int[42],            // the array to be filled
    ///     index -> index * index, // the value in the index
    /// ) // { 0, 1, 4, 9, 16, ..., 41 * 41 }
    /// ```
    @NullUnmarked // JSpecify is not smart enough to handle this situation.
    @Contract("_, _ -> param1")
    public static <T> T[] makeArray(T[] array, IntFunction<T> generator) {
        for (int i = 0; i < array.length; i++) {
            array[i] = generator.apply(i);
        }
        return array;
    }

    /// Construct a value-filled array.
    ///
    /// ```java
    /// makeArray(
    ///     42,                     // the size of the array
    ///     Integer[]::new,         // the constructor of the array
    ///     index -> index * index, // the value in the index
    /// ) // { 0, 1, 4, 9, 16, ..., 41 * 41 }
    /// ```
    ///
    /// @deprecated Use {@link #makeArray(Object[], IntFunction)} instead.
    @NullUnmarked // JSpecify is not smart enough to handle this situation.
    @Deprecated
    public static <T> T[] makeArray(int size, IntFunction<T[]> constructor, IntFunction<T> generator) {
        T[] array = constructor.apply(size);
        return makeArray(array, generator);
    }

    public static <T> int countNull(@Nullable T[] array) {
        int count = 0;
        for (T element : array) {
            if (element == null) count++;
        }
        return count;
    }

    /// Get the existing value in the index, or calculate and store the value from the given generator.
    public static <T> T computeIfAbsentArray(@Nullable T[] element, int index, IntFunction<T> generator) {
        T exist = element[index];
        return exist == null ? (element[index] = generator.apply(index)) : exist;
    }

    @Contract("_, _ -> param1")
    public static boolean runIfFalse(boolean value, Runnable action) {
        if (!value) action.run();
        return value;
    }

}

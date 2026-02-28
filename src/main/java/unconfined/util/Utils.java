package unconfined.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

/// General non-Minecraft utils
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

    @ApiStatus.Experimental
    @SuppressWarnings("unchecked")
    public static <T, R> R[] mapArray(T[] source, Function<T, R> mapper) {
        if (source.length == 0) throw new IllegalArgumentException("source array must not be empty");
        Iterator<T> iter = Arrays.asList(source).iterator();
        // get the return type by get the first element from the mapper
        R first = mapper.apply(iter.next());
        // then construct the array
        R[] ret = (R[]) Array.newInstance(first.getClass(), source.length);
        // fill the first element
        ret[0] = first;
        // iterate through the remainings
        for (int i = 1; i < source.length; i++) {
            ret[i] = mapper.apply(iter.next());
        }
        return ret;
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

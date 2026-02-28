package unconfined.util;

import java.util.Arrays;
import java.util.function.Supplier;

/// An accessor to a final array field.
public interface FinalArrayAccessor<T> extends Supplier<T[]> {

    /// @return the underlying array.
    T[] get();

    /// Fill the underlying array with given values.
    ///
    /// If the source contains more elements than the size of the underlying array, the exceeding elements are ignored.
    /// If the source contains less elements than the size of the underlying array, the tails are not affected. (call [#clear()] before [#fill(Object\[\])] if you want to clear them.)
    default void fill(T[] source) {
        int copyLength = Math.min(source.length, this.get().length);
        System.arraycopy(source, 0, get(), 0, copyLength);
    }

    /// Fill the underlying array with `null`s.
    default void clear() {
        Arrays.fill(get(), null);
    }

}

package unconfined.util;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public sealed interface Option<T> permits Option.Some, Option.None {
    static <T> Some<T> some(T value) {
        return new Some<>(value);
    }

    @SuppressWarnings("unchecked")
    static <T> Option<T> none() {
        return (Option<T>) None.INSTANCE;
    }

    T unwrap();

    <R> Option<R> map(Function<T, Option<R>> mapper);

    <R> Option<R> mapNotNull(Function<T, R> mapper);

    record Some<T>(T value) implements Option<T> {
        @Override
        public String toString() {
            return "Some(" + value + ")";
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Some<?>(Object otherSome) && Objects.equals(value, otherSome);
        }

        @Override
        public T unwrap() {
            return value;
        }

        @Override
        public <R> Option<R> map(Function<T, Option<R>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public <R> Option<R> mapNotNull(Function<T, @Nullable R> mapper) {
            R apply = mapper.apply(value);
            return apply != null ? some(apply) : none();
        }
    }

    final class None implements Option<Object> {
        private static final None INSTANCE = new None();

        private None() {
        }

        @Override
        public Object unwrap() {
            throw new IllegalStateException("Can't unwrap a none()");
        }

        @Override
        public <R> Option<R> map(Function<Object, Option<R>> mapper) {
            return none();
        }

        @Override
        public <R> Option<R> mapNotNull(Function<Object, R> mapper) {
            return none();
        }
    }
}

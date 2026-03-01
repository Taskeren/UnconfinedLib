package net.minecraft.util.parsing.packrat;

import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface ParseState<S> {
    Scope scope();

    ErrorCollector<S> errorCollector();

    default <T> Optional<T> parseTopRule(NamedRule<S, T> rule) {
        T t = this.parse(rule);
        if (t != null) {
            this.errorCollector().finish(this.mark());
        }

        if (!this.scope().hasOnlySingleFrame()) {
            throw new IllegalStateException("Malformed scope: " + this.scope());
        } else {
            return Optional.ofNullable(t);
        }
    }

    <T> @Nullable T parse(NamedRule<S, T> rule);

    S input();

    int mark();

    void restore(int cursor);

    Control acquireControl();

    void releaseControl();

    ParseState<S> silent();
}

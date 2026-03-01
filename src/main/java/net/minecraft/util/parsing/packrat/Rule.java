package net.minecraft.util.parsing.packrat;

import org.jspecify.annotations.Nullable;

public interface Rule<S, T> {
    @Nullable T parse(ParseState<S> parseState);

    static <S, T> Rule<S, T> fromTerm(Term<S> child, RuleAction<S, T> action) {
        return new WrappedTerm<>(action, child);
    }

    static <S, T> Rule<S, T> fromTerm(Term<S> child, SimpleRuleAction<S, T> action) {
        return new WrappedTerm<>(action, child);
    }

    @FunctionalInterface
    interface RuleAction<S, T> {
        @Nullable T run(ParseState<S> parseState);
    }

    @FunctionalInterface
    interface SimpleRuleAction<S, T> extends RuleAction<S, T> {
        T run(Scope scope);

        @Override
        default T run(ParseState<S> p_410290_) {
            return this.run(p_410290_.scope());
        }
    }

    record WrappedTerm<S, T>(RuleAction<S, T> action, Term<S> child) implements Rule<S, T> {
        @Override
        public @Nullable T parse(ParseState<S> p_336049_) {
            Scope scope = p_336049_.scope();
            scope.pushFrame();

            T object;
            try {
                if (!this.child.parse(p_336049_, scope, Control.UNBOUND)) {
                    return null;
                }

                object = this.action.run(p_336049_);
            } finally {
                scope.popFrame();
            }

            return object;
        }
    }
}

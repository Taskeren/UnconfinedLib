package net.minecraft.util.parsing.packrat;

import org.jspecify.annotations.Nullable;
import unconfined.util.UMaths;

import java.util.ArrayList;
import java.util.List;

public interface ErrorCollector<S> {
    void store(int cursor, SuggestionSupplier<S> suggestions, Object reason);

    default void store(int cursor, Object reason) {
        this.store(cursor, SuggestionSupplier.empty(), reason);
    }

    void finish(int cursor);

    class LongestOnly<S> implements ErrorCollector<S> {
        @SuppressWarnings("unchecked")
        private @Nullable MutableErrorEntry<S>[] entries = new MutableErrorEntry[16];
        private int nextErrorEntry;
        private int lastCursor = -1;

        private void discardErrorsFromShorterParse(int cursor) {
            if (cursor > this.lastCursor) {
                this.lastCursor = cursor;
                this.nextErrorEntry = 0;
            }
        }

        @Override
        public void finish(int p_335534_) {
            this.discardErrorsFromShorterParse(p_335534_);
        }

        @Override
        public void store(int p_335763_, SuggestionSupplier<S> p_336144_, Object p_335736_) {
            this.discardErrorsFromShorterParse(p_335763_);
            if (p_335763_ == this.lastCursor) {
                this.addErrorEntry(p_336144_, p_335736_);
            }
        }

        private void addErrorEntry(SuggestionSupplier<S> suggestions, Object reason) {
            int i = this.entries.length;
            if (this.nextErrorEntry >= i) {
                int j = UMaths.growByHalf(i, this.nextErrorEntry + 1);
                @SuppressWarnings("unchecked")
                MutableErrorEntry<S>[] mutableerrorentry = new MutableErrorEntry[j];
                System.arraycopy(this.entries, 0, mutableerrorentry, 0, i);
                this.entries = mutableerrorentry;
            }

            int k = this.nextErrorEntry++;
            MutableErrorEntry<S> mutableerrorentry1 = this.entries[k];
            if (mutableerrorentry1 == null) {
                mutableerrorentry1 = new MutableErrorEntry<>();
                this.entries[k] = mutableerrorentry1;
            }

            mutableerrorentry1.suggestions = suggestions;
            mutableerrorentry1.reason = reason;
        }

        public List<ErrorEntry<S>> entries() {
            int i = this.nextErrorEntry;
            if (i == 0) {
                return List.of();
            } else {
                List<ErrorEntry<S>> list = new ArrayList<>(i);

                for (int j = 0; j < i; j++) {
                    MutableErrorEntry<S> mutableerrorentry = this.entries[j];
                    list.add(new ErrorEntry<>(
                        this.lastCursor,
                        mutableerrorentry.suggestions,
                        mutableerrorentry.reason
                    ));
                }

                return list;
            }
        }

        public int cursor() {
            return this.lastCursor;
        }

        static class MutableErrorEntry<S> {
            SuggestionSupplier<S> suggestions = SuggestionSupplier.empty();
            Object reason = "empty";
        }
    }

    class Nop<S> implements ErrorCollector<S> {
        @Override
        public void store(int p_409770_, SuggestionSupplier<S> p_409587_, Object p_410314_) {
        }

        @Override
        public void finish(int p_409839_) {
        }
    }
}

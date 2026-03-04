package unconfined.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ListConsumer<T> implements Consumer<T> {
    protected final List<Consumer<? super T>> consumers;

    public ListConsumer() {
        this(new ArrayList<>());
    }

    public ListConsumer(List<Consumer<? super T>> consumers) {
        this.consumers = consumers;
    }

    @Override
    public void accept(T t) {
        for (Consumer<? super T> consumer : consumers) consumer.accept(t);
    }

    @Override
    public String toString() {
        return "ListConsumer{ " + consumers.size() + " consumers }";
    }

    public void add(Consumer<? super T> consumer) {
        consumers.add(consumer);
    }

    public boolean remove(Consumer<? super T> consumer) {
        return consumers.remove(consumer);
    }

    public int size() {
        return consumers.size();
    }

    public void clear() {
        consumers.clear();
    }

    public boolean isEmpty() {
        return consumers.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }
}

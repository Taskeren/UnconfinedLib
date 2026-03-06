package unconfined.mod.client.util.data;

public interface Rect2i {

    int x();

    int y();

    int width();

    int height();

    Rect2i x(int x);

    Rect2i y(int y);

    Rect2i width(int width);

    Rect2i height(int height);

    default boolean contains(int x, int y) {
        return x >= x() && x <= x() + width() && y >= y() && y <= y() + height();
    }

    static Rect2i of(int x, int y, int width, int height) {
        return new ImmutableRect2i(x, y, width, height);
    }

    static Rect2i ofMutable(int x, int y, int width, int height) {
        return new MutableRect2i(x, y, width, height);
    }
}

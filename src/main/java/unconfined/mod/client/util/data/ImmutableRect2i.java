package unconfined.mod.client.util.data;

public record ImmutableRect2i(int x, int y, int width, int height) implements Rect2i {

    @Override
    public Rect2i x(int x) {
        return new ImmutableRect2i(x, y, width, height);
    }

    @Override
    public Rect2i y(int y) {
        return new ImmutableRect2i(x, y, width, height);
    }

    @Override
    public Rect2i width(int width) {
        return new ImmutableRect2i(x, y, width, height);
    }

    @Override
    public Rect2i height(int height) {
        return new ImmutableRect2i(x, y, width, height);
    }
}

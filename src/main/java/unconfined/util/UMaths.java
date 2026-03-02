package unconfined.util;

public final class UMaths {

    public static int floor(double value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    public static int floor(float value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    public static int growByHalf(int value, int minValue) {
        return (int) Math.max(Math.min((long) value + (value >> 1), 2147483639L), minValue);
    }

}

package unconfined.mod.util;

public class MojUtil {

    public static int offsetByCodepoints(String text, int cursorPos, int offset) {
        int pos = cursorPos;
        if (offset >= 0) { // move to right
            for (int i = 0; pos < text.length() && i < offset; i++) {
                if (Character.isHighSurrogate(text.charAt(pos++))
                    && pos < text.length()
                    && Character.isLowSurrogate(text.charAt(pos))
                ) pos++;
            }
        } else { // move to left
            for (int i = offset; pos > 0 && i < 0; i++) {
                pos--;
                if (Character.isLowSurrogate(text.charAt(pos))
                    && pos > 0
                    && Character.isHighSurrogate(text.charAt(pos - 1))
                ) pos--;
            }
        }
        return pos;
    }
}

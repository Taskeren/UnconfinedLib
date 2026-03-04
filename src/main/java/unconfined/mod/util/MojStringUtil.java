package unconfined.mod.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MojStringUtil {

    public static boolean isAllowedChatCharacter(char c) {
        return c != 167 && c >= 32 && c != 127;
    }

    public String filterText(String text) {
        return filterText(text, false);
    }

    public String filterText(String text, boolean allowLineBreaks) {
        StringBuilder s = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (isAllowedChatCharacter(c)) {
                s.append(c);
            } else if (allowLineBreaks && c == '\n') {
                s.append(c);
            }
        }
        return s.toString();
    }

}

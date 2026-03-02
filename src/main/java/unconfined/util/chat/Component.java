package unconfined.util.chat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.jspecify.annotations.Nullable;

import java.util.function.UnaryOperator;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Component implements IChatComponent, com.mojang.brigadier.Message { // mimic Component builder
    private static final Component EMPTY = literal("");

    @Delegate(types = IChatComponent.class)
    private final ChatBuilder builder;

    public static Component empty() {
        return EMPTY;
    }

    public static Component literal(String text) {
        return new Component(ChatBuilder.text(text));
    }

    public static Component translatable(String key, Object... args) {
        return new Component(ChatBuilder.translation(key, args));
    }

    private static boolean isAllowedPrimitiveArgument(@Nullable Object input) {
        return input instanceof Number || input instanceof Boolean || input instanceof String;
    }

    public static Component translatableEscape(String key, Object... args) {
        for (int i = 0; i < args.length; i++) {
            Object object = args[i];
            if (!isAllowedPrimitiveArgument(object) && !(object instanceof Component)) {
                args[i] = String.valueOf(object);
            }
        }

        return translatable(key, args);
    }

    private boolean isEmpty() {
        return this == EMPTY;
    }

    public Component withStyle(UnaryOperator<ChatStyle> consumer) {
        builder.setChatStyle(consumer.apply(builder.getChatStyle()));
        return this;
    }

    public Component withStyle(ChatStyle style) {
        builder.setChatStyle(style.setParentStyle(builder.getChatStyle()));
        return this;
    }

    public Component withStyle(EnumChatFormatting color) {
        builder.color(color);
        return this;
    }

    public Component append(String string) {
        return string.isEmpty() ? this : this.append(literal(string));
    }

    public Component append(IChatComponent sibling) {
        builder.appendSibling(sibling);
        return this;
    }

    @Override
    public String getString() {
        return this.getUnformattedText();
    }
}

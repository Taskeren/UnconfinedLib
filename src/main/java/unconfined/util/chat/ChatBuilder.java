package unconfined.util.chat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatBuilder implements IChatComponent {
    @Delegate
    private final IChatComponent chat;

    public IChatComponent build() {
        return chat.createCopy();
    }

    // helper method
    public ChatBuilder also(Consumer<ChatBuilder> action) {
        action.accept(this);
        return this;
    }

    public static ChatBuilder text(String text) {
        return new ChatBuilder(new ChatComponentText(text));
    }

    public static ChatBuilder translation(String key, Object... args) {
        return new ChatBuilder(new ChatComponentTranslation(key, args));
    }

    public static ChatBuilder copyableText(String text) {
        return text(text).underlined().clickSuggestMessage(text);
    }

    public ChatBuilder color(EnumChatFormatting color) {
        chat.getChatStyle().setColor(color);
        return this;
    }

    public ChatBuilder noColor() {
        chat.getChatStyle().setColor(null);
        return this;
    }

    public ChatBuilder bold() {
        return bold(true);
    }

    public ChatBuilder bold(boolean bold) {
        chat.getChatStyle().setBold(bold);
        return this;
    }

    public ChatBuilder italic() {
        return italic(true);
    }

    public ChatBuilder italic(boolean italic) {
        chat.getChatStyle().setItalic(italic);
        return this;
    }

    public ChatBuilder underlined() {
        return underlined(true);
    }

    public ChatBuilder underlined(boolean underlined) {
        chat.getChatStyle().setUnderlined(underlined);
        return this;
    }

    public ChatBuilder strikethrough() {
        return strikethrough(true);
    }

    public ChatBuilder strikethrough(boolean strikethrough) {
        chat.getChatStyle().setStrikethrough(strikethrough);
        return this;
    }

    public ChatBuilder obfuscated() {
        return obfuscated(true);
    }

    public ChatBuilder obfuscated(boolean obfuscated) {
        chat.getChatStyle().setObfuscated(obfuscated);
        return this;
    }

    public ChatBuilder click(ClickEvent event) {
        chat.getChatStyle().setChatClickEvent(event);
        return this;
    }

    public ChatBuilder clickOpenUrl(String url) {
        return click(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    }

    public ChatBuilder clickOpenFile(String absolutePath) {
        return click(new ClickEvent(ClickEvent.Action.OPEN_FILE, absolutePath));
    }

    public ChatBuilder clickSendMessage(String message) {
        return click(new ClickEvent(ClickEvent.Action.RUN_COMMAND, message));
    }

    public ChatBuilder clickSuggestMessage(String message) {
        return click(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message));
    }

    public ChatBuilder hover(HoverEvent event) {
        chat.getChatStyle().setChatHoverEvent(event);
        return this;
    }

    public ChatBuilder hoverText(IChatComponent component) {
        return hover(new HoverEvent(HoverEvent.Action.SHOW_TEXT, component));
    }

    public ChatBuilder hoverAchievement(StatBase achivement) {
        // from StatBase#func_150951_e
        return hover(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, new ChatComponentText(achivement.statId)));
        // color(EnumChatFormatting.GRAY);
    }

    public ChatBuilder hoverShowItem(ItemStack itemStack) {
        // from ItemStack#func_151000_E
        String itemInfo = itemStack.writeToNBT(new NBTTagCompound()).toString();
        return hover(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText(itemInfo)));
        // color(itemStack.getRarity().rarityColor);
    }

    public ChatBuilder append(IChatComponent component) {
        chat.appendSibling(component);
        return this;
    }

    public ChatBuilder append(ChatBuilder builder) {
        chat.appendSibling(builder.chat);
        return this;
    }

    public ChatBuilder appendText(String text) {
        return append(new ChatComponentText(text));
    }
}

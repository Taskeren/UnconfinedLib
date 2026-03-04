package unconfined.mod.client.util;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvTabCompleter extends TabCompleter {
    private static final Pattern WS = Pattern.compile("(\\s+)");

    protected ClickBox box = new ClickBox(0, 0, -1, -1);
    protected int offset;
    protected boolean cycle;
    protected boolean keepSuggestions;
    protected boolean wasFirst;

    public AdvTabCompleter(GuiTextField textField, boolean hasTargetBlock) {
        super(textField, hasTargetBlock);
    }

    public boolean onKeyPress(int key) {
        if (hasSuggestions()) {
            if (key == Keyboard.KEY_TAB) {
                complete();
                return true;
            } else if (key == Keyboard.KEY_UP) {
                prev();
                return true;
            } else if (key == Keyboard.KEY_DOWN) {
                next();
                return true;
            }
        }
        return false;
    }

    public boolean onClick(int mouseX, int mouseY) {
        if (box.isInside(mouseX, mouseY)) {
            select(offset + ((mouseY - box.getY()) / 12));
            return true;
        }
        return false;
    }

    public boolean onScroll(int mouseX, int mouseY, int scroll) {
        if (box.isInside(mouseX, mouseY)) {
            this.offset += scroll;
            if (offset < 0) offset = 0;
            else if (offset + 10 > completions.size()) offset = Math.max(0, completions.size() - 10);
            return true;
        }
        return false;
    }

    public boolean hasSuggestions() {
        return !completions.isEmpty();
    }

    @Override
    public void complete() {
        if (completions.isEmpty()) return;
        if (wasFirst) {
            select(completionIndex);
            wasFirst = false;
            return;
        }
        if (GuiScreen.isShiftKeyDown()) {
            prev();
        } else {
            next();
        }
    }

    public void prev() {
        select(completionIndex == 0 ? completions.size() - 1 : completionIndex - 1);
    }

    public void next() {
        select((++completionIndex) % completions.size());
    }

    public void select(int index) {
        if (index < 0 || index >= completions.size()) return;
        select0(index);
        updateWord(completions.get(completionIndex));
    }

    protected void select0(int index) {
        completionIndex = index;
        if (index - 9 >= offset) offset = index - 9;
        else if (index < offset) offset = index;
    }

    public void updateWord(String value) {
        textField.deleteFromCursor(0);
        int end = textField.getCursorPosition();
        String text = this.textField.getText().substring(0, end);
        int start = getWordIndex(text);
        if (start == 0) start = 1;
        cycle = true;
        StringBuilder builder = new StringBuilder(textField.getText());
        builder.replace(start, end, value);
        value = builder.toString();
        keepSuggestions = true;
        textField.setText(EnumChatFormatting.getTextWithoutFormattingCodes(value));
        textField.setCursorPosition(start + value.length());
        keepSuggestions = false;
    }

    public void requestUpdate() {
        if (textField.getText().isEmpty() || textField.getCursorPosition() == 0) {
            completionIndex = 0;
            completions.clear();
            return;
        }
        String prefix = this.textField.getText().substring(0, this.textField.getCursorPosition());
        if (prefix.isEmpty()) {
            prefix = "/";
        }
        int i = this.textField.func_146197_a(-1, this.textField.getCursorPosition(), false);
        String s = textField.getText().substring(i).toLowerCase();
        net.minecraftforge.client.ClientCommandHandler.instance.autoComplete(prefix, s);
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(prefix));
        cycle = false;
    }

    @Override
    public void setComplete(List<String> completions) {
        boolean allStart = true;
        for (String completion : completions) {
            if (completion.isEmpty()) continue;
            if (!completion.startsWith("/")) {
                allStart = false;
                break;
            }
        }
        if (allStart) {
            for (int i = 0, m = completions.size(); i < m; i++) {
                if (completions.get(i).isEmpty()) continue;
                completions.set(i, completions.get(i).substring(1));
            }
        }
        wasFirst = !cycle || this.completions.isEmpty();
        this.completions.clear();
        completionIndex = 0;
        String currentWord = "";
        if (!textField.getText().isEmpty()) {
            int end = textField.getCursorPosition();
            currentWord = textField.getText().substring(0, end);
            int start = getWordIndex(currentWord);
            if (start == 0) start = 1;
            if (start <= currentWord.length()) {
                currentWord = currentWord.substring(start);
            }
        }
        for (String value : completions) {
            if (value.isEmpty() || value.equals(currentWord)) continue;
            this.completions.add(value);
        }
        offset = 0;
    }

    public int getWordIndex(String text) {
        if (text != null && !text.isEmpty()) {
            int i = 0;
            Matcher matcher = WS.matcher(text);
            while (matcher.find()) {
                i = matcher.end();
            }
            return i;
        }
        return 0;
    }

    public void render(int mouseX, int mouseY, FontRenderer font) {
        int limit = Math.min(completions.size() - offset, 10);
        if (limit > 0) {
            String s = textField.getText();
            int x = Math.min(
                s.isEmpty() ? 0 : font.getStringWidth(s.substring(
                    0,
                    Math.max(0, textField.getText().lastIndexOf(" ")) + 1
                )), textField.getWidth()
            ) + textField.xPosition;
            int width = 0;
            for (String completion : completions) {
                width = Math.max(width, font.getStringWidth(completion));
            }
            int baseY = textField.yPosition - (12 * limit) - 3;

            int index = mouseX >= x && mouseX <= x + width ? ((mouseY - baseY) / 12) : -1;

            Gui.drawRect(x, baseY, x + width + 5, textField.yPosition - 3, -805306368);
            for (int i = 0; i < limit; i++) {
                font.drawStringWithShadow(
                    completions.get(i + offset),
                    x + 2,
                    baseY + (i * 12) + 2,
                    i == index || i + offset == completionIndex ? -256 : -5592406
                );
            }
            box.set(x, baseY, width, (textField.yPosition - 3) - baseY);
        }
    }

    protected static class ClickBox {
        @Getter
        @Setter
        protected int x, y, width, height;

        public ClickBox(int x, int y, int width, int height) {
            this.set(x, y, width, height);
        }

        public ClickBox set(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public boolean isInside(int xPos, int yPos) {
            return xPos >= x && xPos <= x + width && yPos >= y && yPos <= y + height;
        }
    }
}

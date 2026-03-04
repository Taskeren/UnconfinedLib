package unconfined.mod.client.util;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public abstract class TabCompleter {

    protected final GuiTextField textField;
    protected final boolean hasTargetBlock;

    protected boolean didComplete;
    protected boolean requestedComplete;
    protected int completionIndex;
    protected List<String> completions = Lists.newArrayList();

    public void complete() {
        if (didComplete) {
            textField.deleteFromCursor(0);
            textField.deleteFromCursor(textField.func_146197_a(-1, textField.getCursorPosition(), false)
                - textField.getCursorPosition());

            if (this.completionIndex >= this.completions.size()) {
                this.completionIndex = 0;
            }
        } else {
            completions.clear();
            completionIndex = 0;
            int i = textField.func_146197_a(-1, textField.getCursorPosition(), false);
            String whole = textField.getText().substring(i).toLowerCase(Locale.ROOT);
            String prefix = textField.getText().substring(0, textField.getCursorPosition());
            requestComplete(prefix, whole);
            if (completions.isEmpty()) {
                return;
            }

            didComplete = true;
            textField.deleteFromCursor(i - textField.getCursorPosition());
        }

        textField.writeText(EnumChatFormatting.getTextWithoutFormattingCodes(completions.get(completionIndex++)));
    }

    private void requestComplete(String prefix, String whole) {
        if (!prefix.isEmpty()) {
            ClientCommandHandler.instance.autoComplete(prefix, whole);
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(prefix));
            requestedComplete = true;
        }
    }

    public void setComplete(List<String> completions) {
        if (requestedComplete) {
            didComplete = false;
            this.completions.clear();

            this.completions.addAll(completions);
            // also the client ones
            String[] clientCompletions = ClientCommandHandler.instance.latestAutoComplete;
            if (clientCompletions != null) {
                Collections.addAll(this.completions, clientCompletions);
            }
            // filter out empty elements
            this.completions.removeIf(String::isEmpty);
            String[] completionsArray = this.completions.toArray(new String[0]);

            String s1 = textField.getText()
                .substring(textField.func_146197_a(-1, textField.getCursorPosition(), false));
            String s2 = EnumChatFormatting.getTextWithoutFormattingCodes(StringUtils.getCommonPrefix(completionsArray));

            if (!s2.isEmpty() && !s1.equalsIgnoreCase(s2)) {
                textField.deleteFromCursor(0);
                textField.deleteFromCursor(textField.func_146197_a(-1, textField.getCursorPosition(), false)
                    - textField.getCursorPosition());
                textField.writeText(s2);
            } else if (!this.completions.isEmpty()) {
                didComplete = true;
                complete();
            }
        }
    }

    public void resetDidComplete() {
        didComplete = false;
    }

    public void resetRequested() {
        requestedComplete = false;
    }

}

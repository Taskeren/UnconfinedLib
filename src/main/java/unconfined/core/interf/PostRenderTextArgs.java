package unconfined.core.interf;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.jetbrains.annotations.ApiStatus;

/// @param xPos      the x position of the input field
/// @param yPos      the y position of the input field
/// @param text      the text that has been rendered
/// @param textWidth the width of the text that has been rendered (used to calculate where to continue the rendering the text)
/// @see IGuiTextFieldExtension
public record PostRenderTextArgs(GuiTextField self, int xPos, int yPos, String text, int textWidth) {
    @ApiStatus.Internal
    public void drawStringRightAfter(FontRenderer font, String text) {
        font.drawStringWithShadow(text, xPos() + textWidth(), yPos(), 0xFF000000);
    }
}

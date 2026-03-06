package unconfined.core.mixins.misc;

import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiChat.class)
public interface GuiChatAccessor {

    @Accessor("defaultInputFieldText")
    String getDefaultInputFieldText();

}

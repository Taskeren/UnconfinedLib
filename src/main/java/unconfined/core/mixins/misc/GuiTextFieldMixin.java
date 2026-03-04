package unconfined.core.mixins.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unconfined.core.interf.IGuiTextFieldExtension;
import unconfined.core.interf.PostRenderTextArgs;
import unconfined.util.ListConsumer;

import java.util.function.Consumer;

@Mixin(GuiTextField.class)
public class GuiTextFieldMixin implements IGuiTextFieldExtension {

    @Shadow
    @Final
    private FontRenderer field_146211_a;
    @Unique
    private final ListConsumer<PostRenderTextArgs> unconfined$postRenderTextCallback = new ListConsumer<>();

    @Override
    public void unconfined$addPostRenderTextCallback(Consumer<PostRenderTextArgs> callback) {
        this.unconfined$postRenderTextCallback.add(callback);
    }

    @Inject(method = "drawTextBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I", ordinal = 0, shift = At.Shift.AFTER))
    private void unconfined$afterRenderText(CallbackInfo ci,
                                            @Local(ordinal = 5, name = "j1") int xPos,
                                            @Local(ordinal = 4, name = "i1") int yPos,
                                            @Local(ordinal = 1, name = "s1") String text) {
        if (this.unconfined$postRenderTextCallback.isNotEmpty()) {
            int textWidth = field_146211_a.getStringWidth(text);
            PostRenderTextArgs args = new PostRenderTextArgs((GuiTextField) (Object) this, xPos, yPos, text, textWidth);
            unconfined$postRenderTextCallback.accept(args);
        }
    }
}

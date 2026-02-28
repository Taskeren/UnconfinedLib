package unconfined.core.mixins.treecommand;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.command.CommandHelp;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import unconfined.util.command.ITreeCommand;

@Mixin(CommandHelp.class)
public class CommandHelpMixin {

    @WrapOperation(method = "processCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/ICommand;getCommandUsage(Lnet/minecraft/command/ICommandSender;)Ljava/lang/String;"))
    private String unconfined$betterCommandUsageForTrees(ICommand instance, ICommandSender sender, Operation<String> original, @Local(argsOnly = true) String[] args) {
        if (instance instanceof ITreeCommand tree) {
            return tree.getCommandUsage(sender, args);
        }
        return original.call(instance, sender);
    }

}

package unconfined.util.command;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import org.jspecify.annotations.Nullable;
import unconfined.util.UnconfinedUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static unconfined.util.command.ITreeCommand.shiftArgs;

public abstract class TreeCommand extends CommandBase implements ITreeCommand {

    @Getter
    protected final Map<String, ICommand> subCommands = new HashMap<>();

    @Override
    public final void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            // print subcommands
            UnconfinedUtils.addAllChatMessages(
                sender,
                getSubCommandHelpMessageList(sender, Lists.newArrayList(getCommandName()))
            );
        } else {
            ICommand subCommand = getSubCommand(args[0]);
            if (subCommand == null) {
                processMissingCommand(sender, args[0], shiftArgs(args));
            } else {
                if (subCommand.canCommandSenderUseCommand(sender)) {
                    subCommand.processCommand(sender, shiftArgs(args));
                } else {
                    // permission failure
                    sender.addChatMessage(new ChatComponentTranslation("commands.generic.permission")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
                }
            }
        }
    }

    @Override
    public final @Nullable List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length <= 1) {
            return getListOfStringsFromIterableMatchingLastWord(args, getSubCommands().keySet());
        }
        ICommand subCommand = getSubCommand(args[0]);
        if (subCommand != null) {
            return subCommand.addTabCompletionOptions(sender, shiftArgs(args));
        }
        return null;
    }
}

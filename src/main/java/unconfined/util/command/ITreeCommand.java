package unconfined.util.command;

import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.jspecify.annotations.Nullable;
import unconfined.util.Assertions;
import unconfined.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ITreeCommand extends ICommand {
    Map<String, ICommand> getSubCommands();

    /// @return the subcommand with the given name.
    @Nullable
    default ICommand getSubCommand(String name) {
        return getSubCommands().get(name);
    }

    /// Add a subcommand.
    default void addSubCommand(ICommand command) {
        addSubCommand(command.getCommandName(), command);
    }

    /// Add a subcommand with the given name.
    default void addSubCommand(String name, ICommand command) {
        Assertions.check(
            getSubCommands().putIfAbsent(name, command) == null,
            "Failed to add a subcommand that already exists, name " + name
        );
    }

    /// Get the command usage of the targeted level.
    default String getCommandUsage(ICommandSender sender, String[] args) {
        if (args.length <= 1) return getCommandUsage(sender);
        ICommand subCommand = getSubCommand(args[1]);
        if (subCommand != null) {
            if (subCommand instanceof ITreeCommand tree) {
                return tree.getCommandUsage(sender, shiftArgs(args));
            } else {
                return subCommand.getCommandUsage(sender);
            }
        }
        throw new CommandNotFoundException();
    }

    /// Invoked when the targeted subcommand is `null`.
    default void processMissingCommand(ICommandSender sender, String commandName, String[] shiftedArgs) {
    }

    /// Get the message of subcommands recursive.
    ///
    /// @param paths the command names to this command (include the current command).
    default List<IChatComponent> getSubCommandHelpMessageList(ICommandSender sender, List<String> paths) {
        return Utils.make(
            new ArrayList<>(), list -> {
                // generates "/paths[0] paths[1] ... "
                String path = "/" + String.join(" ", paths) + " ";

                for (Map.Entry<String, ICommand> entry : getSubCommands().entrySet()) {
                    String subCommandName = entry.getKey();
                    ICommand subCommand = entry.getValue();

                    // if the child is also a tree command, we gather the help message from it.
                    if (subCommand instanceof ITreeCommand tree) {
                        paths.add(subCommandName);
                        list.addAll(tree.getSubCommandHelpMessageList(sender, paths));
                        paths.remove(paths.size() - 1);
                        continue;
                    }

                    // or handle a normal command
                    list.add(new ChatComponentText(path + subCommandName)
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA))
                        .appendSibling(new ChatComponentText(" : ")
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY).setBold(true)))
                        .appendSibling(new ChatComponentTranslation(subCommand.getCommandUsage(sender))
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY))));
                }
            }
        );
    }

    static String[] shiftArgs(String[] args) {
        if (args.length == 0) return new String[0];
        String[] shifted = new String[args.length - 1];
        System.arraycopy(args, 1, shifted, 0, shifted.length);
        return shifted;
    }
}

package unconfined.mod.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.EnumChatFormatting;
import unconfined.util.chat.Component;
import unconfined.util.command.TreeCommand;

final class SubCommandNBT extends TreeCommand {

    @Override
    public String getCommandName() {
        return "nbt";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "command.unconfined.nbt.usage";
    }

    {
        addSubCommand(PARSE);
    }

    private static final ICommand PARSE = new CommandBase() {
        @Override
        public String getCommandName() {
            return "parse";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "command.unconfined.nbt.parse.usage";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            String nbtString = String.join(" ", args);
            try {
                CompoundTag tag = TagParser.parseCompoundFully(nbtString);
                // pretty print to the sender
                NbtUtils.prettyPrint(tag).lines()
                    .map(Component::literal)
                    .forEach(sender::addChatMessage);
            } catch (CommandSyntaxException e) {
                // show failure
                sender.addChatMessage(Component.literal("Failed to parse the given data.")
                    .append(Component.literal(e.getMessage()).withStyle(EnumChatFormatting.RED)));
            }
        }
    };
}

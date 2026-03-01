package unconfined.mod.command;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import unconfined.util.command.TreeCommand;

import java.util.List;

public class UnconfinedCommand extends TreeCommand {

    {
        addSubCommand(new Foo());
        addSubCommand("teleport", new CommandTeleport());
        addSubCommand(new SubCommandBlockInfo());
        addSubCommand(new SubCommandNBT());
    }

    @Override
    public String getCommandName() {
        return "unconfined";
    }

    @Override
    public List<String> getCommandAliases() {
        return Lists.newArrayList("unc", "unconfinedlib");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "command.unconfined.usage";
    }

    private static class Foo extends CommandBase {
        @Override
        public String getCommandName() {
            return "foo";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "command.unconfined.foo.usage";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            sender.addChatMessage(new ChatComponentText("FOO!").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)
                .setItalic(true)));
        }
    }

}

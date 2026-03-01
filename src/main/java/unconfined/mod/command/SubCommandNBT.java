package unconfined.mod.command;

import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.EnumChatFormatting;
import unconfined.util.chat.ChatBuilder;
import unconfined.util.chat.Component;
import unconfined.util.command.TreeCommand;

import static unconfined.mod.command.UnconfinedCommand.asCommand;

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
        addSubCommand(TO_JSON);
    }

    private static final ICommand PARSE = asCommand(
        "parse", "command.unconfined.nbt.parse.usage", (sender, args) -> {
            String nbtString = String.join(" ", args);
            try {
                CompoundTag tag = TagParser.parseCompoundFully(nbtString);
                // pretty print to the sender
                NbtUtils.prettyPrint(tag).lines()
                    .map(Component::literal)
                    .forEach(sender::addChatMessage);
            } catch (CommandSyntaxException e) {
                // show failure
                sender.addChatMessage(Component.literal("Failed to parse the given data. ")
                    .append(Component.literal(e.getMessage()).withStyle(EnumChatFormatting.RED)));
            }
        }
    );

    private static final ICommand TO_JSON = asCommand(
        "toJson", "command.unconfined.nbt.toJson.usage", (sender, args) -> {
            String nbtString = String.join(" ", args);
            try {
                JsonElement json = TagParser.create(JsonOps.INSTANCE).parseFully(nbtString);
                sender.addChatMessage(ChatBuilder.text(json.toString()).clickSuggestMessage(json.toString()));
            } catch (CommandSyntaxException e) {
                // show failure
                sender.addChatMessage(Component.literal("Failed to parse the given data. ")
                    .append(Component.literal(e.getMessage()).withStyle(EnumChatFormatting.RED)));
            }
        }
    );
}

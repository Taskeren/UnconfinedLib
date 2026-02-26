package unconfined.mod.command;

import codechicken.lib.raytracer.RayTracer;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import unconfined.util.command.TreeCommand;

import java.util.List;

public class UnconfinedCommand extends TreeCommand {

    {
        addSubCommand(new Foo());
        addSubCommand("teleport", new CommandTeleport());
        addSubCommand(new InspectBlockInfo());
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

    private static class InspectBlockInfo extends CommandBase {
        @Override
        public String getCommandName() {
            return "blockinfo";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "command.unconfined.blockinfo.usage";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            World world = player.worldObj;
            MovingObjectPosition mop = RayTracer.reTrace(world, player);
            if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
                sender.addChatMessage(new ChatComponentText("Unable to ray-trace the block."));
                return;
            }

            int x = mop.blockX, y = mop.blockY, z = mop.blockZ;
            // TODO: block info gather
            Block block = world.getBlock(x, y, z);
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            sender.addChatMessage(new ChatComponentText(String.format("Block at %s/%s/%s is ", x, y, z)).appendSibling(
                new ChatComponentTranslation(block.getUnlocalizedName() + ".name")));
            sender.addChatMessage(new ChatComponentText("TileEntity type: ").appendSibling(new ChatComponentText(
                tileEntity.getClass().getCanonicalName()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)
                .setItalic(true))));
        }
    }

}

package unconfined.mod.command;

import codechicken.lib.raytracer.RayTracer;
import com.google.common.collect.Lists;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import unconfined.util.UnconfinedUtils;
import unconfined.util.chat.ChatBuilder;
import unconfined.util.command.TreeCommand;

import java.util.Iterator;
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
            sender.addChatMessage(
                ChatBuilder.text("Block at ")
                    .color(EnumChatFormatting.WHITE)
                    .append(
                        ChatBuilder.text(String.format("%s/%s/%s", x, y, z))
                            .color(EnumChatFormatting.YELLOW)
                            .underlined()
                            .clickSuggestMessage(String.format("/tp @p %s %s %s", x, y, z))
                    )
                    .appendText(" is ")
                    .append(
                        ChatBuilder.translation(block.getUnlocalizedName() + ".name").bold()
                    )
            );

            try {
                ItemStack item = block.getPickBlock(null, world, x, y, z, player);
                if (item != null) {
                    String giveCommand = String.format(
                        "/give @p %s 1 %s",
                        Item.getIdFromItem(item.getItem()),
                        item.getItemDamage()
                    );
                    if (item.stackTagCompound != null) giveCommand += " " + item.stackTagCompound;
                    sender.addChatMessage(
                        ChatBuilder.translation(item.getUnlocalizedName() + ".name")
                            .underlined()
                            .hoverShowItem(item)
                            .clickSuggestMessage(giveCommand)
                    );
                }
            } catch (Throwable ignored) {
            }

            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null) {
                sender.addChatMessage(
                    ChatBuilder.text("TE: ").append(getClassInfoChatComponent(te.getClass()))
                );
                if (te instanceof IGregTechTileEntity gte) {
                    IMetaTileEntity mte = gte.getMetaTileEntity();
                    sender.addChatMessage(
                        ChatBuilder.text("MTE: ").append(getClassInfoChatComponent(mte.getClass()))
                    );
                    UnconfinedUtils.addAllChatMessages(sender, BlockInspectUtils.getMTEFluidInfo(mte));
                }
            }
        }

        private static IChatComponent getClassInfoChatComponent(Class<?> clazz) {
            return ChatBuilder.text("")
                .color(EnumChatFormatting.GRAY)
                .append(
                    ChatBuilder.text(clazz.getCanonicalName())
                        .color(EnumChatFormatting.AQUA)
                        .italic()
                        .underlined()
                        .clickSuggestMessage(clazz.getCanonicalName())
                )
                .append(ChatBuilder.text(" implements ").color(EnumChatFormatting.GRAY))
                .also(b -> {
                    List<Class<?>> ifaces = BlockInspectUtils.getImplementedInterfaces(clazz);
                    for (Iterator<Class<?>> iter = ifaces.iterator(); iter.hasNext(); ) {
                        Class<?> iface = iter.next();
                        b.append(
                            ChatBuilder.text(iface.getSimpleName())
                                .color(EnumChatFormatting.DARK_AQUA)
                                .hoverText(ChatBuilder.text(iface.getCanonicalName())
                                    .color(EnumChatFormatting.DARK_AQUA))
                                .clickSuggestMessage(iface.getCanonicalName())
                        );
                        if (iter.hasNext()) b.appendText(", ");
                    }
                });
        }
    }

}

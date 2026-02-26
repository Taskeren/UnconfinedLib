package unconfined.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fluids.FluidStack;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

/// Minecraft-related utils
public final class UnconfinedUtils {

    public static FluidStack copy(FluidStack stack, int amount) {
        FluidStack copy = stack.copy();
        copy.amount = amount;
        return copy;
    }

    @NullUnmarked
    public static FluidStack[] copyArray(FluidStack[] array) {
        return Utils.make(
            new FluidStack[array.length], copy -> {
                for (int i = 0; i < array.length; i++) {
                    FluidStack fluidStack = array[i];
                    if (fluidStack != null) {
                        copy[i] = fluidStack.copy();
                    }
                }
            }
        );
    }

    public static void addAllChatMessages(ICommandSender sender, Iterable<IChatComponent> components) {
        for (IChatComponent component : components) {
            sender.addChatMessage(component);
        }
    }

    public static final class Persist {
        public static @Nullable FluidStack[] loadArray(NBTTagCompound tag, int arraySize) {
            return Utils.make(new FluidStack[arraySize], array -> loadToArray(tag, array));
        }

        public static void loadToArray(NBTTagCompound tag, @Nullable FluidStack[] dest) {
            for (int i = 0; i < dest.length; i++) {
                String key = String.valueOf(i);
                if (tag.hasKey(key)) {
                    dest[i] = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag(key));
                }
            }
        }

        public static NBTTagCompound saveArray(@Nullable FluidStack[] fluidStacks) {
            return Utils.make(
                new NBTTagCompound(), tag -> {
                    for (int i = 0; i < fluidStacks.length; i++) {
                        FluidStack fluid = fluidStacks[i];
                        if (fluid != null) {
                            tag.setTag(String.valueOf(i), fluid.writeToNBT(new NBTTagCompound()));
                        }
                    }
                }
            );
        }
    }
}

package unconfined.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import lombok.Getter;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fluids.FluidStack;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import unconfined.util.tier.Tier;
import unconfined.util.tier.Voltage;

import java.util.EnumMap;
import java.util.function.Function;

/// Minecraft-related utils
public final class UnconfinedUtils {
    @Getter
    private static final Joiner COMMA_JOINER = Joiner.on(", ");

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

    public static String toString(FluidStack stack) {
        return McUtils.runDist(
            () -> String.format("%sx %s", stack.amount, stack.getFluid().getName()),
            () -> String.format(
                "%sx %s (%s)",
                stack.amount,
                stack.getFluid().getName(),
                I18n.format(stack.getUnlocalizedName())
            )
        );
    }

    public static String toString(FluidStack[] fluidStacks) {
        return toString(fluidStacks, UnconfinedUtils::toString);
    }

    public static <T> String toString(T[] src, Function<T, String> toString) {
        return "[" + COMMA_JOINER.join(Iterators.transform(Iterators.forArray(src), toString::apply)) + "]";
    }

    public static <T> EnumMap<Tier, T> ofTieredMap() {
        return new EnumMap<>(Tier.class);
    }

    public static <T> EnumMap<Voltage, T> ofVoltageMap() {
        return new EnumMap<>(Voltage.class);
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

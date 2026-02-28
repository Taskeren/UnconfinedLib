package unconfined.mod.command;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;
import unconfined.api.gregtech.UnconfinedMultiFluidBasicMachine;
import unconfined.util.Utils;
import unconfined.util.chat.ChatBuilder;
import unconfined.util.fluidtank.IUnconfinedFluidTank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

class BlockInspectUtils {

    public static List<Class<?>> getImplementedInterfaces(Class<?> clazz) {
        return Utils.make(
            new ArrayList<>(),
            list -> gatherClassImplementedInterfaces(clazz, ifaces -> list.addAll(Arrays.asList(ifaces)), null)
        );
    }

    private static void gatherClassImplementedInterfaces(Class<?> clazz, Consumer<Class<?>[]> gather, @Nullable Class<?> ignoreAt) {
        while (true) {
            if (clazz == ignoreAt) return;
            Class<?>[] ifaces = clazz.getInterfaces();
            if (ifaces.length != 0) gather.accept(ifaces);
            if (clazz != Object.class) {
                clazz = clazz.getSuperclass();
                continue;
            }
            return;
        }
    }

    public static List<IChatComponent> getMTEFluidInfo(IMetaTileEntity mte) {
        return Utils.make(
            new ArrayList<>(), list -> {
                list.add(ChatBuilder.text("[Fluid Info]").color(EnumChatFormatting.WHITE).bold());
                if (mte instanceof MTEBasicTank basicTank) {
                    if (basicTank.isDrainableStackSeparate()) {
                        list.add(ChatBuilder.text("[Fillable] ")
                            .color(EnumChatFormatting.GRAY)
                            .append(getFluidInfoMessage(basicTank.getFillableStack())));
                        list.add(ChatBuilder.text("[Drainable] ")
                            .color(EnumChatFormatting.GRAY)
                            .append(getFluidInfoMessage(basicTank.getDrainableStack())));
                    } else {
                        list.add(ChatBuilder.text("[mFluid] ")
                            .color(EnumChatFormatting.GRAY)
                            .append(getFluidInfoMessage(basicTank.mFluid)));
                    }
                }
                if (mte instanceof UnconfinedMultiFluidBasicMachine multiFluidBasicMachine) {
                    list.add(ChatBuilder.text("[INPUT]"));
                    getUnconfinedFluidTankInfo(list, multiFluidBasicMachine.getInputFluids());
                    list.add(ChatBuilder.text("[OUTPUT]"));
                    getUnconfinedFluidTankInfo(list, multiFluidBasicMachine.getOutputFluids());
                }
                // finalize
                if (list.isEmpty()) {
                    list.add(ChatBuilder.text("NOT SUPPORTED").color(EnumChatFormatting.RED).italic());
                }
            }
        );
    }

    private static IChatComponent getFluidInfoMessage(@Nullable FluidStack fluid) {
        if (fluid == null) {
            return ChatBuilder.text("NONE").color(EnumChatFormatting.GRAY).italic();
        } else {
            return ChatBuilder.translation(fluid.getUnlocalizedName())
                .color(EnumChatFormatting.WHITE)
                .appendText(" ")
                .append(ChatBuilder.text("x" + fluid.amount).color(EnumChatFormatting.YELLOW));
        }
    }

    private static void getUnconfinedFluidTankInfo(ArrayList<IChatComponent> list, IUnconfinedFluidTank outputFluids) {
        for (int i = 0; i < outputFluids.getSlotCount(); i++) {
            FluidStack slot = outputFluids.get(i);
            list.add(
                // 1. Lava x1000
                // 2. NONE
                ChatBuilder.text(i + ". ").color(EnumChatFormatting.GRAY).append(getFluidInfoMessage(slot))
            );
        }
    }


}

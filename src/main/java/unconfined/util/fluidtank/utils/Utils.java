package unconfined.util.fluidtank.utils;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import unconfined.util.fluidtank.IUnconfinedFluidTank;

@ApiStatus.Internal
public final class Utils {

    /// @return `true` if the given output fluids can be all outputted to the tank.
    public static boolean canOutput(IUnconfinedFluidTank tank, FluidStack[] output) {
        int capacity = tank.getCapacity();
        @Nullable FluidStack[] tankArray = tank.toFluidStackArray();
        // the fluid count of output that can't find a slot with the same fluid.
        int unmerged = 0;
        output:
        for (FluidStack fluidToOutput : output) {
            for (FluidStack tankFluid : tankArray) {
                if (tankFluid != null && FluidStack.areFluidStackTagsEqual(tankFluid, fluidToOutput)) {
                    int amountCanOutput = capacity - tankFluid.amount;
                    if (amountCanOutput >= fluidToOutput.amount) {
                        // this fluid is good to be outputted
                        continue output;
                    }
                }
            }
            // this fluid can't find a slot with the same fluid.
            unmerged++;
        }
        // check if there's enough empty slots for unmerged fluids.
        return unconfined.util.Utils.countNull(tankArray) >= unmerged;
    }

}

package unconfined.api.gregtech;

import net.minecraftforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;
import unconfined.util.fluidtank.IUnconfinedFluidTank;

public interface UnconfinedMultiFluidBasicMachine {

    IUnconfinedFluidTank getInputFluids();

    IUnconfinedFluidTank getOutputFluids();

    @Nullable FluidStack[] getRecipeOutputFluids();

    void fillRecipeOutputFluids(@Nullable FluidStack[] fluidStacks);

    void clearRecipeOutputFluids();

}

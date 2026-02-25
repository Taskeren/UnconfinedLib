package unconfined.api.gregtech;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import unconfined.util.fluidtank.IUnconfinedFluidTank;

public interface UnconfinedMultiFluidBasicMachine {

    IUnconfinedFluidTank getInputFluids();

    IUnconfinedFluidTank getOutputFluids();

    @Nullable FluidStack[] getRecipeOutputFluids();

    void fillRecipeOutputFluids(@Nullable FluidStack[] fluidStacks);

    void clearRecipeOutputFluids();

    @ApiStatus.Experimental
    interface Mixin extends UnconfinedMultiFluidBasicMachine {
        IUnconfinedFluidTank unconfined$getInputFluids();

        IUnconfinedFluidTank unconfined$getOutputFluids();

        @Nullable FluidStack[] unconfined$getRecipeOutputFluids();

        void unconfined$fillRecipeOutputFluids(@Nullable FluidStack[] fluidStacks);

        void unconfined$clearRecipeOutputFluids();

        @Override
        default IUnconfinedFluidTank getInputFluids() {
            return unconfined$getInputFluids();
        }

        @Override
        default IUnconfinedFluidTank getOutputFluids() {
            return unconfined$getOutputFluids();
        }

        @Override
        @Nullable
        default FluidStack[] getRecipeOutputFluids() {
            return unconfined$getRecipeOutputFluids();
        }

        @Override
        default void fillRecipeOutputFluids(@Nullable FluidStack[] fluidStacks) {
            unconfined$fillRecipeOutputFluids(fluidStacks);
        }

        @Override
        default void clearRecipeOutputFluids() {
            unconfined$clearRecipeOutputFluids();
        }
    }

}

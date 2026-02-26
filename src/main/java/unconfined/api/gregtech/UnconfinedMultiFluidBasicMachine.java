package unconfined.api.gregtech;

import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import net.minecraftforge.fluids.FluidStack;
import unconfined.util.FinalArrayAccessor;
import unconfined.core.mixins.multifluid.MTEBasicMachineMixin;
import unconfined.util.fluidtank.IUnconfinedFluidTank;
import unconfined.util.fluidtank.UnconfinedFluidTankOverridden;

/// A marker to the *child* classes of [MTEBasicMachine] to support multiple fluid slots.
///
/// **NOTE:** After implementing this, some behavior in [MTEBasicMachine] will be changed via [MTEBasicMachineMixin], so be aware of this when overridden the methods.
///
/// An input and output tank is required. And it's better to delegate the existent fields [MTEBasicMachine#getDrainableStack()] and [MTEBasicMachine#getFillableStack()] by using [UnconfinedFluidTankOverridden] functionalities.
/// And an array to store the recipe fluid output is also required.
///
/// See also [unconfined.mod.gregtech.MultiFluidBasicMachineWithRecipe] (separated implementation) and [unconfined.core.mixins.multifluid.MTEBasicMachineImplMixin] (in-place implementation).
public interface UnconfinedMultiFluidBasicMachine {

    /// @return the tank of input slots.
    IUnconfinedFluidTank getInputFluids();

    /// @return the tank of output slots.
    IUnconfinedFluidTank getOutputFluids();

    /// @return the accessor to the array that stores the fluid outputs of the recipe, that will be added to the output slots when the progress is done.
    FinalArrayAccessor<FluidStack> getRecipeOutputAccessor();
}

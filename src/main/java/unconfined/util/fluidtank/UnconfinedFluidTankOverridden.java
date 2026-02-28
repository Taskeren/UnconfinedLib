package unconfined.util.fluidtank;

import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/// A [IUnconfinedFluidTank] wrapper that can have few slots overridden and delegate the getting and setting operations to other [UnconfinedFluidSlotView]s.
///
/// To set up the overridden, call [#setOverridden(int, UnconfinedFluidSlotView)].
public class UnconfinedFluidTankOverridden extends UnconfinedFluidTank {

    protected final @Nullable UnconfinedFluidSlotView[] overridden;

    public UnconfinedFluidTankOverridden(int slotCount, int capacity) {
        super(slotCount, capacity);
        this.overridden = new UnconfinedFluidSlotView[slotCount];
    }

    /// Set the slot overridden, or cancel the overridden by passing a `null`.
    ///
    /// @throws ArrayIndexOutOfBoundsException when the slot is out-of-bounds.
    public void setOverridden(int slot, UnconfinedFluidSlotView overridden) {
        this.overridden[slot] = overridden;
    }

    @Override
    public @Nullable FluidStack get(int slot) {
        UnconfinedFluidSlotView view = overridden[slot];
        return view != null ? view.get() : super.get(slot);
    }

    @Override
    public void set(int slot, @Nullable FluidStack stack) {
        UnconfinedFluidSlotView view = overridden[slot];
        if (view != null) {
            view.accept(stack);
        } else {
            super.set(slot, stack);
        }
    }

    @ApiStatus.Experimental
    public static void setupInputOverriddenFromBasicMachine(UnconfinedFluidTankOverridden tank, MTEBasicMachine self) {
        tank.setOverridden(0, UnconfinedFluidSlotView.of(self::getFillableStack, self::setFillableStack));
    }

    @ApiStatus.Experimental
    public static void setupOutputOverriddenFromBasicMachine(UnconfinedFluidTankOverridden tank, MTEBasicMachine self) {
        tank.setOverridden(0, UnconfinedFluidSlotView.of(self::getDrainableStack, self::setDrainableStack));
    }

}

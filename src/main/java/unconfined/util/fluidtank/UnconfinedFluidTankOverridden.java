package unconfined.util.fluidtank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.minecraftforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

/// A [IUnconfinedFluidTank] wrapper that can have few slots overridden and delegate the getting and setting operations to other [UnconfinedFluidSlotView]s.
///
/// To set up the overridden, call [#setOverridden(int, UnconfinedFluidSlotView)].
@RequiredArgsConstructor
public class UnconfinedFluidTankOverridden implements IUnconfinedFluidTank.Wrapper {

    @Delegate
    @Getter
    protected final IUnconfinedFluidTank delegate;

    protected final @Nullable UnconfinedFluidSlotView[] overridden;

    public UnconfinedFluidTankOverridden(IUnconfinedFluidTank delegate) {
        this.delegate = delegate;
        this.overridden = new UnconfinedFluidSlotView[delegate.getSlotCount()];
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
        return view != null ? view.get() : delegate.get(slot);
    }

    @Override
    public void set(int slot, @Nullable FluidStack stack) {
        UnconfinedFluidSlotView view = overridden[slot];
        if (view != null) {
            view.accept(stack);
        } else {
            delegate.set(slot, stack);
        }
    }
}

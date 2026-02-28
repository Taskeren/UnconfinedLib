package unconfined.util.fluidtank;

import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import unconfined.util.Utils;

import java.util.Arrays;

/// A [IUnconfinedFluidTank] wrapper that caches the [UnconfinedFluidSlotView] and [FluidStackTank] instances, as they're expected to be long-living objects.
///
/// In case of the values should be invalidated, call [#invalidate()] to wipe the caches.
@RequiredArgsConstructor
public class UnconfinedFluidTankCached implements IUnconfinedFluidTank.Wrapper {

    @Delegate
    @Getter
    protected final IUnconfinedFluidTank delegate;

    protected final UnconfinedFluidSlotView[] fluidSlotViews;
    protected final FluidStackTank[] fluidStackTanks;

    public UnconfinedFluidTankCached(IUnconfinedFluidTank delegate) {
        this.delegate = delegate;
        this.fluidSlotViews = new UnconfinedFluidSlotView[delegate.getSlotCount()];
        this.fluidStackTanks = new FluidStackTank[delegate.getSlotCount()];
    }

    @Override
    public UnconfinedFluidSlotView getFluidSlotView(int slot) {
        return Utils.computeIfAbsentArray(fluidSlotViews, slot, delegate::getFluidSlotView);
    }

    @Override
    public FluidStackTank getFluidStackTankForSlot(int slot) {
        return Utils.computeIfAbsentArray(fluidStackTanks, slot, delegate::getFluidStackTankForSlot);
    }

    public void invalidate() {
        Arrays.fill(fluidSlotViews, null);
        Arrays.fill(fluidStackTanks, null);
    }
}

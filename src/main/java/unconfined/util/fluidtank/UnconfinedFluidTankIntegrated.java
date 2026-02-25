package unconfined.util.fluidtank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class UnconfinedFluidTankIntegrated implements IUnconfinedFluidTank.Wrapper, IFluidTank, IFluidHandler {

    @Delegate
    @Getter
    protected final IUnconfinedFluidTank delegate;

    // region IFluidTank

    @Override
    public @Nullable FluidStack getFluid() {
        return delegate.getFirstNonEmpty();
    }

    @Override
    public int getFluidAmount() {
        FluidStack fluid = getFluid();
        return fluid != null ? fluid.amount : 0;
    }

    @Override
    public int getCapacity() {
        return delegate.getCapacity();
    }

    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(this);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return delegate.fill(resource, doFill);
    }

    @Override
    public @Nullable FluidStack drain(int maxDrain, boolean doDrain) {
        return delegate.drainAny(maxDrain, doDrain);
    }

    // endregion

    // region IFluidHandler

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return delegate.fill(resource, doFill);
    }

    @Override
    public @Nullable FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return delegate.drain(resource, resource.amount, doDrain);
    }

    @Override
    public @Nullable FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return delegate.drainAny(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return delegate.getTankInfo();
    }

    // endregion
}

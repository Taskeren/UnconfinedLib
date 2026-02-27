package unconfined.util.fluidtank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.minecraftforge.fluids.FluidStack;
import unconfined.util.UnconfinedUtils;

@RequiredArgsConstructor
public class UnconfinedFluidTankNoOverflow implements IUnconfinedFluidTank.Wrapper {
    @Getter
    @Delegate
    protected final IUnconfinedFluidTank delegate;

    @Override
    public int fill(FluidStack resource, boolean execute) {
        for (FluidStack slot : this) {
            if (slot != null && slot.isFluidEqual(resource)) {
                int amountToFill = Math.min(getCapacity() - slot.amount, resource.amount);
                if (execute) {
                    resource.amount -= amountToFill;
                    slot.amount += amountToFill;
                }
                return amountToFill;
            }
        }
        for (int i = 0; i < getSlotCount(); i++) {
            if (get(i) == null) {
                int amountToFill = Math.min(getCapacity(), resource.amount);
                if (execute) {
                    resource.amount -= amountToFill;
                    set(i, UnconfinedUtils.copy(resource, amountToFill));
                }
                return amountToFill;
            }
        }
        return 0;
    }
}

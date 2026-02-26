package unconfined.util.fluidtank;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import org.jspecify.annotations.Nullable;
import unconfined.util.Assertions;
import unconfined.util.UnconfinedUtils;
import unconfined.util.Utils;

import java.util.function.Consumer;

/// A default implementation of [IUnconfinedFluidTank].
///
/// To create a instance with more than basic features, use [#builder()].
@Getter
public class UnconfinedFluidTank implements IUnconfinedFluidTank {

    protected final @Nullable FluidStack[] internalFluids;
    protected final int capacity;

    public UnconfinedFluidTank(int slotCount, int capacity) {
        this.internalFluids = new FluidStack[slotCount];
        this.capacity = capacity;
    }

    // region direct access

    @Override
    public @Nullable FluidStack get(int slot) {
        FluidStack fluid = internalFluids[slot];
        // clean-up
        if (fluid != null && fluid.amount <= 0) {
            set(slot, null);
            return null;
        }
        return fluid;
    }

    @Override
    public void set(int slot, @Nullable FluidStack stack) {
        // clean-up
        if (stack != null && stack.amount <= 0) {
            internalFluids[slot] = null;
            return;
        }
        internalFluids[slot] = stack;
    }

    @Override
    public int getSlotCount() {
        return internalFluids.length;
    }

    // endregion

    // region fill

    public int fill(FluidStack resource, boolean execute) {
        int result = fillFluidMerging(resource, execute);
        if (result == 0) result = fillFluidToEmpty(resource, execute);
        return result;
    }

    /**
     * Fill the internal tank with the given fluid, to the first slot with the same fluid.
     *
     * @param resource the fluid to be filled to the tank
     * @param execute  {@code true} to apply the change
     * @return the amount of fluid that has been filled
     */
    protected int fillFluidMerging(FluidStack resource, boolean execute) {
        for (FluidStack slotFluid : this) {
            if (slotFluid == null) continue;
            if (FluidStack.areFluidStackTagsEqual(slotFluid, resource)) {
                int amountToFill = Math.min(getCapacity() - slotFluid.amount, resource.amount);
                if (execute) {
                    resource.amount -= amountToFill;
                    slotFluid.amount += amountToFill;
                }
                return amountToFill;
            }
        }
        return 0;
    }

    /**
     * Fill the internal tank with the given fluid, to the first empty slot.
     *
     * @param resource the fluid to be filled to the tank
     * @param execute  {@code true} to apply the change
     * @return the amount of fluid that has been filled
     */
    protected int fillFluidToEmpty(FluidStack resource, boolean execute) {
        for (int slot = 0; slot < getSlotCount(); slot++) {
            FluidStack slotFluid = get(slot);
            if (slotFluid == null) {
                int amountToFill = Math.min(getCapacity(), resource.amount);
                if (execute) {
                    resource.amount -= amountToFill;
                    set(slot, UnconfinedUtils.copy(resource, amountToFill));
                }
                return amountToFill;
            }
        }
        return 0;
    }

    // endregion

    // region drain

    @Nullable
    public FluidStack drainAny(int amount, boolean execute) {
        for (int slot = 0; slot < getSlotCount(); slot++) {
            FluidStack slotFluid = get(slot);
            if (slotFluid != null) {
                int amountToDrain = Math.min(slotFluid.amount, amount);
                if (execute) {
                    slotFluid.amount -= amountToDrain;
                    // clean-up
                    if (slotFluid.amount <= 0) set(slot, null);
                }
                return UnconfinedUtils.copy(slotFluid, amountToDrain);
            }
        }
        return null;
    }

    @Nullable
    public FluidStack drain(FluidStack resource, int amount, boolean execute) {
        for (int slot = 0; slot < getSlotCount(); slot++) {
            FluidStack slotFluid = get(slot);
            if (slotFluid == null) continue;
            if (FluidStack.areFluidStackTagsEqual(slotFluid, resource)) {
                int amountToDrain = Math.min(slotFluid.amount, amount);
                if (execute) {
                    slotFluid.amount -= amountToDrain;
                    // clean-up
                    if (slotFluid.amount <= 0) set(slot, null);
                }
                return UnconfinedUtils.copy(slotFluid, amountToDrain);
            }
        }
        return null;
    }

    // endregion

    // region utils

    @Nullable
    public FluidStack getFirstNonEmpty() {
        for (FluidStack slotFluid : getInternalFluids()) {
            if (slotFluid != null) {
                return slotFluid;
            }
        }
        return null;
    }

    @Override
    public FluidTank getFluidTank(int slot) {
        return new FluidTank(get(slot), getCapacity());
    }

    @Override
    public FluidTankInfo[] getTankInfo() {
        return Utils.makeArray(new FluidTankInfo[getSlotCount()], slot -> new FluidTankInfo(getFluidTank(slot)));
    }

    @Override
    public @Nullable FluidStack[] toFluidStackArray() {
        return Utils.makeArray(new FluidStack[getSlotCount()], this::get);
    }

    @Override
    public UnconfinedFluidSlotView getFluidSlotView(int slot) {
        return UnconfinedFluidSlotView.of(() -> get(slot), (value) -> set(slot, value));
    }

    @Override
    public void loadData(NBTTagCompound tag) {
        for (int i = 0; i < getSlotCount(); i++) {
            if (tag.hasKey(String.valueOf(i))) {
                set(i, FluidStack.loadFluidStackFromNBT(tag.getCompoundTag(String.valueOf(i))));
            }
        }
    }

    @Override
    public NBTTagCompound saveData() {
        return Utils.make(
            new NBTTagCompound(), tag -> {
                for (int i = 0; i < getSlotCount(); i++) {
                    FluidStack fluidStack = get(i);
                    if (fluidStack != null) {
                        tag.setTag(String.valueOf(i), fluidStack.writeToNBT(new NBTTagCompound()));
                    }
                }
            }
        );
    }

    // endregion

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class Builder {
        private int slotCount;
        private int capacity;

        private boolean overridden;
        private @Nullable Consumer<UnconfinedFluidTankOverridden> overriddenConfigurer;

        private boolean cached;
        private boolean integrated;

        public IUnconfinedFluidTank build() {
            Assertions.check(slotCount >= 0, "slotCount should be non-negative");
            Assertions.check(capacity >= 0, "capacity should be non-negative");
            IUnconfinedFluidTank result = new UnconfinedFluidTank(slotCount, capacity);
            if (overridden) {
                result = new UnconfinedFluidTankOverridden(result);
                if (overriddenConfigurer != null) {
                    overriddenConfigurer.accept((UnconfinedFluidTankOverridden) result);
                }
            }
            if (cached) {
                result = new UnconfinedFluidTankCached(result);
            }
            if (integrated) { // must be the last to wrap, so that the integration interfaces are accessible without unwrapping.
                result = new UnconfinedFluidTankIntegrated(result);
            }
            return result;
        }

        public Builder overridden(Consumer<UnconfinedFluidTankOverridden> configurer) {
            overridden = true;
            overriddenConfigurer = configurer;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}

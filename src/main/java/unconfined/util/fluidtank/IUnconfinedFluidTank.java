package unconfined.util.fluidtank;

import com.google.common.collect.Iterators;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;

public interface IUnconfinedFluidTank extends Iterable<@Nullable FluidStack> {

    /// @return the internal tank.
    /// @apiNote recommend to use [#get(int)] and [#set(int, FluidStack)] instead.
    @ApiStatus.Internal
    FluidStack[] getInternalFluids();

    /// Get the [FluidStack] in the given slot, or `null` if absent.
    ///
    /// @param slot the slot index
    /// @return the [FluidStack] in the slot or `null`.
    /// @throws ArrayIndexOutOfBoundsException when the slot index is out of bound. See [#getSlotCount()].
    @Nullable
    FluidStack get(int slot);

    /// Set the slot with given [FluidStack].
    ///
    /// @param slot  the slot index
    /// @param stack the given stack
    /// @throws ArrayIndexOutOfBoundsException when the slot index is out of bound. See [#getSlotCount()].
    void set(int slot, @Nullable FluidStack stack);

    /// @return the slot count.
    /// @implNote slot indices from 0 to slot count must be available.
    int getSlotCount();

    /**
     * @return the capacity of each tank.
     */
    int getCapacity();

    /// Fill the given resource to tank.
    ///
    /// @param resource the resource to fill the tank.
    /// @param execute  whether or not to apply the changes.
    /// @return the amount of resource that was filled to the tank.
    /// @implNote to reduce resource duplication, it's recommended to also reduce the amount in the given resource.
    int fill(FluidStack resource, boolean execute);

    /// Drain any fluid from the tank.
    ///
    /// @param amount  the maximum amount to drain.
    /// @param execute whether or not to apply the changes.
    /// @return the drained fluid stack or `null` if nothing can be drained.
    @Nullable FluidStack drainAny(int amount, boolean execute);

    /// Drain the same fluid of given resource.
    ///
    /// @param resource the fluid to be drained. (the amount doesn't matter)
    /// @param amount   the maximum amount to drain.
    /// @param execute  whether or not to apply the changes.
    /// @return the drained fluid stack or `null` if nothing can be drained.
    @Nullable FluidStack drain(FluidStack resource, int amount, boolean execute);

    /// @return the first non-empty (amount > 0) fluid in the tank or `null` if nothing in the tank.
    @Nullable FluidStack getFirstNonEmpty();

    /// Minecraft-related API usage only.
    ///
    /// @return the fluid slot snapshot at the given index.
    FluidTank getFluidTank(int slot);

    /// Minecraft-related API usage only.
    ///
    /// @return the tank snapshot of contained fluids.
    FluidTankInfo[] getTankInfo();

    /// @return the contained fluids and empty slots.
    @Nullable FluidStack[] toFluidStackArray();

    @Override
    default Iterator<@Nullable FluidStack> iterator() {
        return Iterators.forArray(toFluidStackArray());
    }

    /// Load the saved data from the given tag.
    ///
    /// @see #saveData()
    void loadData(NBTTagCompound tag);

    /// Save the data into a tag.
    ///
    /// @see #loadData(NBTTagCompound)
    NBTTagCompound saveData();

    /// @return the fluid slot view of the given slot.
    UnconfinedFluidSlotView getFluidSlotView(int slot);

    /// @return the fluid stack tank of the given slot.
    default FluidStackTank getFluidStackTankForSlot(int slot) {
        return getFluidSlotView(slot).asFluidStackTank(getCapacity());
    }

    /// Fill the tank with the given fluid stacks.
    ///
    /// @return fluids that can't be dumped to the tank.
    FluidStack[] fillAll(@Nullable FluidStack[] fluidStacks);

    boolean canFillAll(@Nullable FluidStack[] fluidStacks);

    /// Get the actual implementation of [IUnconfinedFluidTank] instead of wrappers.
    ///
    /// **WARNING:** The result will lose its extra functionalities like overridden slots. Accessing the data from it is meaningless, and error-prone!
    @ApiStatus.Experimental
    default IUnconfinedFluidTank unwrap() {
        IUnconfinedFluidTank tank = this;
        while (tank instanceof Wrapper wrapper) {
            tank = wrapper.getDelegate();
        }
        return tank;
    }

    /// A marker interface to indicate that the implementation is a wrapper around another [IUnconfinedFluidTank].
    @ApiStatus.Experimental
    interface Wrapper extends IUnconfinedFluidTank {
        /// @return the delegated tank
        IUnconfinedFluidTank getDelegate();
    }
}

package unconfined.util.fluidtank;

import lombok.Getter;

/// A dummy tank used to store the information of [#slotCount] and [#capacity].
public class UnconfinedFluidTankDummy extends UnconfinedFluidTank {

    @Getter // overriding the super
    private final int slotCount, capacity;

    public UnconfinedFluidTankDummy(int slotCount, int capacity) {
        super(0, 0);
        this.slotCount = slotCount;
        this.capacity = capacity;
    }
}

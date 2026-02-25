package unconfined.util.fluidtank;

import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import lombok.RequiredArgsConstructor;
import net.minecraftforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface UnconfinedFluidSlotView extends Consumer<@Nullable FluidStack>, Supplier<@Nullable FluidStack> {

    static UnconfinedFluidSlotView of(Supplier<@Nullable FluidStack> getter, Consumer<@Nullable FluidStack> setter) {
        return new DelegateImpl(getter, setter);
    }

    default FluidStackTank asFluidStackTank(int capacity) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @RequiredArgsConstructor
    class DelegateImpl implements UnconfinedFluidSlotView {
        private final Supplier<@Nullable FluidStack> getter;
        private final Consumer<@Nullable FluidStack> setter;

        @Override
        public void accept(@Nullable FluidStack stack) {
            setter.accept(stack);
        }

        @Override
        public @Nullable FluidStack get() {
            return getter.get();
        }

        @Override
        public FluidStackTank asFluidStackTank(int capacity) {
            return new FluidStackTank(getter, setter, capacity);
        }
    }
}

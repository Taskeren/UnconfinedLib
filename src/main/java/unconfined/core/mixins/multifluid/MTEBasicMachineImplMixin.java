package unconfined.core.mixins.multifluid;

import gregtech.api.interfaces.ITexture;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import net.minecraftforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unconfined.api.gregtech.UnconfinedMultiFluidBasicMachine;
import unconfined.util.fluidtank.IUnconfinedFluidTank;
import unconfined.util.fluidtank.UnconfinedFluidSlotView;
import unconfined.util.fluidtank.UnconfinedFluidTank;

import java.util.Arrays;

/// The implementation of multi-fluid basic machines.
///
/// The required methods is provided, so the [MTEBasicMachineMixin] can intervene and make it work.
///
/// TODO: add an option to disable this injection.
@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(value = MTEBasicMachine.class, remap = false)
public abstract class MTEBasicMachineImplMixin extends MTEBasicTank implements UnconfinedMultiFluidBasicMachine {

    public MTEBasicMachineImplMixin(int aID, String aName, String aNameRegional, int aTier, int aInvSlotCount, String aDescription, ITexture... aTextures) {
        super(aID, aName, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
        // dummy constructor
    }

    @Unique
    private IUnconfinedFluidTank unconfined$inputFluids;

    @Unique
    private IUnconfinedFluidTank unconfined$outputFluids;

    @Unique
    private final FluidStack[] unconfined$recipeOutputFluids = new FluidStack[10];

    @Inject(method = "<init>(Ljava/lang/String;II[Ljava/lang/String;[[[Lgregtech/api/interfaces/ITexture;II)V", at = @At("TAIL"))
    private void unconfined$init(String aName, int aTier, int aAmperage, String[] aDescription, ITexture[][][] aTextures, int aInputSlotCount, int aOutputSlotCount, CallbackInfo ci) {
        unconfined$inputFluids = UnconfinedFluidTank.builder()
            .slotCount(3)
            .capacity(128 * 1000)
            .overridden(tank ->
                tank.setOverridden(0, UnconfinedFluidSlotView.of(this::getFillableStack, this::setFillableStack)))
            .cached(true)
            .build();
        unconfined$outputFluids = UnconfinedFluidTank.builder()
            .slotCount(3)
            .capacity(128 * 1000)
            .overridden(tank ->
                tank.setOverridden(0, UnconfinedFluidSlotView.of(this::getDrainableStack, this::setDrainableStack)))
            .cached(true)
            .build();
    }

    @Override
    public IUnconfinedFluidTank getInputFluids() {
        return unconfined$inputFluids;
    }

    @Override
    public IUnconfinedFluidTank getOutputFluids() {
        return unconfined$outputFluids;
    }

    @Override
    public @Nullable FluidStack[] getRecipeOutputFluids() {
        return unconfined$recipeOutputFluids;
    }

    @Override
    public void fillRecipeOutputFluids(@Nullable FluidStack[] fluidStacks) {
        System.arraycopy(fluidStacks, 0, unconfined$recipeOutputFluids, 0, fluidStacks.length);
    }

    @Override
    public void clearRecipeOutputFluids() {
        Arrays.fill(unconfined$recipeOutputFluids, null);
    }
}

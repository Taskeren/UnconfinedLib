package unconfined.core.mixins.multifluid;

import gregtech.api.interfaces.ITexture;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import gregtech.api.util.GTModHandler;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unconfined.api.gregtech.UnconfinedMultiFluidBasicMachine;
import unconfined.util.FinalArrayAccessor;
import unconfined.util.fluidtank.IUnconfinedFluidTank;
import unconfined.util.fluidtank.UnconfinedFluidSlotView;
import unconfined.util.fluidtank.UnconfinedFluidTank;

import java.util.Objects;

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
    @Unique
    private final FinalArrayAccessor<FluidStack> unconfined$recipeOutputFluidsAccessor = () -> unconfined$recipeOutputFluids;

    @Inject(method = "<init>(Ljava/lang/String;II[Ljava/lang/String;[[[Lgregtech/api/interfaces/ITexture;II)V", at = @At("TAIL"))
    private void unconfined$init(String aName, int aTier, int aAmperage, String[] aDescription, ITexture[][][] aTextures, int aInputSlotCount, int aOutputSlotCount, CallbackInfo ci) {
        unconfined$inputFluids = UnconfinedFluidTank.builder()
            .slotCount(3)
            .capacity(128 * 1000)
            .overridden(tank ->
                tank.setOverridden(0, UnconfinedFluidSlotView.of(this::getFillableStack, this::setFillableStack)))
            .noOverflow(true)
            .cached(true)
            .build();
        unconfined$outputFluids = UnconfinedFluidTank.builder()
            .slotCount(3)
            .capacity(128 * 1000)
            .overridden(tank ->
                tank.setOverridden(0, UnconfinedFluidSlotView.of(this::getDrainableStack, this::setDrainableStack)))
            .noOverflow(true)
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
    public FinalArrayAccessor<FluidStack> getRecipeOutputAccessor() {
        return unconfined$recipeOutputFluidsAccessor;
    }

    // region IFluidTank

    @Override
    public FluidStack getFluid() {
        return getOutputFluids().getFirstNonEmpty();
    }

    @Override
    public int getFluidAmount() {
        FluidStack fluid = getOutputFluids().getFirstNonEmpty();
        return fluid != null ? fluid.amount : 0;
    }

    @Override
    public int getCapacity() {
        return getOutputFluids().getCapacity();
    }

    @Override
    public int fill(FluidStack aFluid, boolean doFill) {
        return getInputFluids().fill(aFluid, doFill);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return getOutputFluids().drainAny(maxDrain, doDrain);
    }

    // endregion

    // region IFluidHandler

    @Override
    public int fill(ForgeDirection side, FluidStack aFluid, boolean doFill) {
        if (Objects.requireNonNull(getBaseMetaTileEntity()).isSteampowered() && GTModHandler.isSteam(aFluid)) {
            return super.fill(side, aFluid, doFill);
        }
        return getInputFluids().fill(aFluid, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection side, FluidStack fluidStack, boolean doDrain) {
        return getOutputFluids().drain(fluidStack, fluidStack.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection side, int maxDrain, boolean doDrain) {
        return getOutputFluids().drainAny(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection side, Fluid fluid) {
        return super.canFill(side, fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection side, Fluid fluid) {
        return super.canDrain(side, fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection side) {
        return getOutputFluids().getTankInfo();
    }

    // endregion
}

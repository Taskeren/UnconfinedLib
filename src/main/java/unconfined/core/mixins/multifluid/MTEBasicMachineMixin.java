package unconfined.core.mixins.multifluid;

import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.util.GTRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import unconfined.api.gregtech.UnconfinedMultiFluidBasicMachine;
import unconfined.util.UnconfinedUtils;
import unconfined.util.Utils;
import unconfined.util.fluidtank.IUnconfinedFluidTank;

/// The injection to make multi-fluid basic work.
///
/// To make it work, the machine must implement [UnconfinedMultiFluidBasicMachine], and this injection should be good to do everything.
///
/// TODO: properly handle the fluid insertion and extraction from outside (via [net.minecraftforge.fluids.IFluidHandler] and [net.minecraftforge.fluids.IFluidTank]).
@Mixin(value = MTEBasicMachine.class, remap = false)
public class MTEBasicMachineMixin {

    @ModifyArgs(method = "checkRecipe(Z)I", at = @At(value = "INVOKE", target = "Lgregtech/api/recipe/FindRecipeQuery;fluids([Lnet/minecraftforge/fluids/FluidStack;)Lgregtech/api/recipe/FindRecipeQuery;"))
    private void unconfined$recipeQueryMultiFluid(Args args) {
        // find the recipe by input tank.
        if (this instanceof UnconfinedMultiFluidBasicMachine mf) {
            args.set(0, mf.getInputFluids().toFluidStackArray());
        }
    }

    @ModifyArgs(method = "checkRecipe(Z)I", at = @At(value = "INVOKE", target = "Lgregtech/api/util/GTRecipe;isRecipeInputEqual(Z[Lnet/minecraftforge/fluids/FluidStack;[Lnet/minecraft/item/ItemStack;)Z"))
    private void unconfined$recipeInputEqualMultiFluid(Args args) {
        // re-check the recipe by input tank.
        if (this instanceof UnconfinedMultiFluidBasicMachine mf) {
            args.set(1, mf.getInputFluids().toFluidStackArray());
        }
    }

    @WrapOperation(method = "canOutput(Lgregtech/api/util/GTRecipe;)Z", at = @At(value = "INVOKE", target = "Lgregtech/api/metatileentity/implementations/MTEBasicMachine;canOutput(Lnet/minecraftforge/fluids/FluidStack;)Z"))
    private boolean unconfined$canOutputMultiFluid(MTEBasicMachine instance, FluidStack aOutput, Operation<Boolean> original, @Local(argsOnly = true) GTRecipe recipe) {
        // check if the output tank can hold all the recipe output
        if (this instanceof UnconfinedMultiFluidBasicMachine mf) {
            IUnconfinedFluidTank output = mf.getOutputFluids();
            return unconfined.util.fluidtank.utils.Utils.canOutput(output, recipe.mFluidOutputs);
        }
        return original.call(instance, aOutput);
    }

    @WrapOperation(method = "checkRecipe(Z)I", at = @At(value = "FIELD", target = "Lgregtech/api/metatileentity/implementations/MTEBasicMachine;mOutputFluid:Lnet/minecraftforge/fluids/FluidStack;", opcode = Opcodes.PUTFIELD))
    private void unconfined$recipeOutputMultiFluid(MTEBasicMachine instance, FluidStack value, Operation<Void> original, @Local(name = "tRecipe") GTRecipe recipe) {
        // store the recipe output for later usage.
        if (this instanceof UnconfinedMultiFluidBasicMachine mf) {
            mf.fillRecipeOutputFluids(UnconfinedUtils.copyArray(recipe.mFluidOutputs));
            return;
        }
        original.call(instance, value);
    }

    @WrapOperation(method = "onPostTick", at = @At(value = "FIELD", target = "Lgregtech/api/metatileentity/implementations/MTEBasicMachine;mOutputFluid:Lnet/minecraftforge/fluids/FluidStack;", opcode = Opcodes.GETFIELD))
    private FluidStack unconfined$recipeDoneOutputMultiFluid(MTEBasicMachine instance, Operation<FluidStack> original) {
        // when the recipe is finished,
        // dump the recipe output to the output tank.
        if (this instanceof UnconfinedMultiFluidBasicMachine mf) {
            FluidStack[] recipeOut = mf.getRecipeOutputFluids();
            for (FluidStack fluid : recipeOut) {
                if (fluid != null) {
                    mf.getOutputFluids().fill(fluid, true);
                }
            }
            mf.clearRecipeOutputFluids();
        }
        return original.call(instance);
    }

    @Inject(method = "loadNBTData", at = @At("TAIL"))
    private void unconfined$loadData(NBTTagCompound aNBT, CallbackInfo ci) {
        // load persisted data
        if (this instanceof UnconfinedMultiFluidBasicMachine mf) {
            mf.getInputFluids().loadData(aNBT.getCompoundTag("unconfined$inputFluids"));
            mf.getOutputFluids().loadData(aNBT.getCompoundTag("unconfined$outputFluids"));
            UnconfinedUtils.Persist.loadToArray(
                aNBT.getCompoundTag("unconfined$recipeOutput"),
                mf.getRecipeOutputFluids()
            );
        }
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"))
    private void unconfined$saveData(NBTTagCompound aNBT, CallbackInfo ci) {
        // persist data
        if (this instanceof UnconfinedMultiFluidBasicMachine mf) {
            aNBT.setTag("unconfined$inputFluids", mf.getInputFluids().saveData());
            aNBT.setTag("unconfined$outputFluids", mf.getOutputFluids().saveData());
            aNBT.setTag("unconfined$recipeOutput", UnconfinedUtils.Persist.saveArray(mf.getRecipeOutputFluids()));
        }
    }

    @ModifyArgs(method = "getUIProperties", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 0))
    private void unconfined$modifyInputFluidCount(Args args) {
        if (this instanceof UnconfinedMultiFluidBasicMachine mf) {
            args.set(1, mf.getInputFluids().getSlotCount());
        }
    }

    @ModifyArgs(method = "getUIProperties", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 1))
    private void unconfined$modifyOutputFluidCount(Args args) {
        if (this instanceof UnconfinedMultiFluidBasicMachine mf) {
            args.set(1, mf.getOutputFluids().getSlotCount());
        }
    }

    @Unique
    private FluidSlotWidget unconfined$createFluidSlot(IDrawable[] backgrounds, Pos2d pos, FluidStackTank tank, boolean isOutput) {
        return Utils.make(
            new FluidSlotWidget(tank), w -> {
                if (isOutput) {
                    w.setInteraction(true, false);
                }
                w.setBackground(backgrounds).setPos(pos);
            }
        );
    }

    @WrapOperation(method = "lambda$addIOSlots$5", at = @At(value = "INVOKE", target = "Lgregtech/api/metatileentity/implementations/MTEBasicMachine;createFluidInputSlot([Lcom/gtnewhorizons/modularui/api/drawable/IDrawable;Lcom/gtnewhorizons/modularui/api/math/Pos2d;)Lcom/gtnewhorizons/modularui/common/widget/FluidSlotWidget;"))
    private FluidSlotWidget unconfined$createInputFluidSlot(MTEBasicMachine instance, IDrawable[] backgrounds, Pos2d pos, Operation<FluidSlotWidget> original, @Local(argsOnly = true) int index) {
        if (this instanceof UnconfinedMultiFluidBasicMachine mf) {
            return unconfined$createFluidSlot(
                backgrounds,
                pos,
                mf.getInputFluids().getFluidStackTankForSlot(index),
                false
            );
        }
        return original.call(instance, backgrounds, pos);
    }

    @WrapOperation(method = "lambda$addIOSlots$6", at = @At(value = "INVOKE", target = "Lgregtech/api/metatileentity/implementations/MTEBasicMachine;createFluidOutputSlot([Lcom/gtnewhorizons/modularui/api/drawable/IDrawable;Lcom/gtnewhorizons/modularui/api/math/Pos2d;)Lcom/gtnewhorizons/modularui/common/widget/FluidSlotWidget;"))
    private FluidSlotWidget unconfined$createOutputFluidSlot(MTEBasicMachine instance, IDrawable[] backgrounds, Pos2d pos, Operation<FluidSlotWidget> original, @Local(argsOnly = true) int index) {
        if (this instanceof UnconfinedMultiFluidBasicMachine mf) {
            return unconfined$createFluidSlot(
                backgrounds,
                pos,
                mf.getOutputFluids().getFluidStackTankForSlot(index),
                true
            );
        }
        return original.call(instance, backgrounds, pos);
    }
}

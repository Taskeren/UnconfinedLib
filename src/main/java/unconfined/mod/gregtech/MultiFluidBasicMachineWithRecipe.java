package unconfined.mod.gregtech;

import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.recipe.RecipeMap;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import unconfined.api.gregtech.UnconfinedMultiFluidBasicMachine;
import unconfined.util.FinalArrayAccessor;
import unconfined.core.mixins.misc.MTEBasicMachineWithRecipeAccessor;
import unconfined.util.fluidtank.IUnconfinedFluidTank;
import unconfined.util.fluidtank.UnconfinedFluidTank;
import unconfined.util.fluidtank.UnconfinedFluidTankOverridden;

/// A modified version of [MTEBasicMachineWithRecipe] that has multiple fluid slots.
///
/// The count of fluid slots is equal to the item slots at maximum 3.
@SuppressWarnings("unused") // expected unused
@ApiStatus.Internal // test only
public class MultiFluidBasicMachineWithRecipe extends MTEBasicMachineWithRecipe implements UnconfinedMultiFluidBasicMachine {

    /*
     * Implementation Note:
     * All 3 introduced object ([#inputFluids], [#outputFluids] and [#recipeOutputFluids]) should not be used as a meta machine, so they're null.
     */

    @Getter
    protected final IUnconfinedFluidTank inputFluids, outputFluids;
    @Getter
    protected final FluidStack[] recipeOutputFluids;

    public MultiFluidBasicMachineWithRecipe(int aID, String aName, String aNameRegional, int aTier, String aDescription, RecipeMap<?> aRecipes, int aInputSlots, int aOutputSlots, int aTankCapacity, SoundResource aSound, SpecialEffects aSpecialEffect, String aOverlays, Object[] aRecipe) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            aDescription,
            aRecipes,
            aInputSlots,
            aOutputSlots,
            aTankCapacity,
            aSound,
            aSpecialEffect,
            aOverlays,
            aRecipe
        );
        inputFluids = null;
        outputFluids = null;
        recipeOutputFluids = null;
    }

    public MultiFluidBasicMachineWithRecipe(int aID, String aName, String aNameRegional, int aTier, String[] aDescription, RecipeMap<?> aRecipes, int aInputSlots, int aOutputSlots, int aTankCapacity, SoundResource aSound, SpecialEffects aSpecialEffect, String aOverlays, Object[] aRecipe) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            aDescription,
            aRecipes,
            aInputSlots,
            aOutputSlots,
            aTankCapacity,
            aSound,
            aSpecialEffect,
            aOverlays,
            aRecipe
        );
        inputFluids = null;
        outputFluids = null;
        recipeOutputFluids = null;
    }

    public MultiFluidBasicMachineWithRecipe(int aID, String aName, String aNameRegional, int aTier, String[] aDescription, RecipeMap<?> aRecipes, int aInputSlots, int aOutputSlots, boolean usesFluids, SoundResource aSound, SpecialEffects aSpecialEffect, String aOverlays) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            aDescription,
            aRecipes,
            aInputSlots,
            aOutputSlots,
            usesFluids,
            aSound,
            aSpecialEffect,
            aOverlays
        );
        inputFluids = null;
        outputFluids = null;
        recipeOutputFluids = null;
    }

    public MultiFluidBasicMachineWithRecipe(int aID, String aName, String aNameRegional, int aTier, String aDescription, RecipeMap<?> aRecipes, int aInputSlots, int aOutputSlots, int aTankCapacity, SoundResource aSound, SpecialEffects aSpecialEffect, String aOverlays) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            aDescription,
            aRecipes,
            aInputSlots,
            aOutputSlots,
            aTankCapacity,
            aSound,
            aSpecialEffect,
            aOverlays
        );
        inputFluids = null;
        outputFluids = null;
        recipeOutputFluids = null;
    }

    public MultiFluidBasicMachineWithRecipe(int aID, String aName, String aNameRegional, int aTier, String[] aDescription, RecipeMap<?> aRecipes, int aInputSlots, int aOutputSlots, int aTankCapacity, SoundResource aSound, SpecialEffects aSpecialEffect, String aOverlays) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            aDescription,
            aRecipes,
            aInputSlots,
            aOutputSlots,
            aTankCapacity,
            aSound,
            aSpecialEffect,
            aOverlays
        );
        inputFluids = null;
        outputFluids = null;
        recipeOutputFluids = null;
    }

    public MultiFluidBasicMachineWithRecipe(String aName, int aTier, String[] aDescription, RecipeMap<?> aRecipes, int aInputSlots, int aOutputSlots, int aTankCapacity, int aAmperage, ITexture[][][] aTextures, SoundResource aSound, SpecialEffects aSpecialEffect) {
        super(
            aName,
            aTier,
            aDescription,
            aRecipes,
            aInputSlots,
            aOutputSlots,
            aTankCapacity,
            aAmperage,
            aTextures,
            aSound,
            aSpecialEffect
        );
        inputFluids = UnconfinedFluidTank.builder()
            .slotCount(Math.min(3, aInputSlots))
            .capacity(aTankCapacity)
            .overridden(t -> UnconfinedFluidTankOverridden.setupInputOverriddenFromBasicMachine(t, this))
            .cached(true)
            .build();
        outputFluids = UnconfinedFluidTank.builder()
            .slotCount(Math.min(3, aOutputSlots))
            .capacity(aTankCapacity)
            .overridden(t -> UnconfinedFluidTankOverridden.setupOutputOverriddenFromBasicMachine(t, this))
            .cached(true)
            .build();
        recipeOutputFluids = new FluidStack[outputFluids.getSlotCount()];
    }

    public MultiFluidBasicMachineWithRecipe(String aName, int aTier, String[] aDescription, RecipeMap<?> aRecipes, int aInputSlots, int aOutputSlots, int aTankCapacity, int aAmperage, ITexture[][][] aTextures, ResourceLocation aSound, SpecialEffects aSpecialEffect) {
        super(
            aName,
            aTier,
            aDescription,
            aRecipes,
            aInputSlots,
            aOutputSlots,
            aTankCapacity,
            aAmperage,
            aTextures,
            aSound,
            aSpecialEffect
        );
        inputFluids = UnconfinedFluidTank.builder()
            .slotCount(Math.min(3, aInputSlots))
            .capacity(aTankCapacity)
            .overridden(t -> UnconfinedFluidTankOverridden.setupInputOverriddenFromBasicMachine(t, this))
            .cached(true)
            .build();
        outputFluids = UnconfinedFluidTank.builder()
            .slotCount(Math.min(3, aOutputSlots))
            .capacity(aTankCapacity)
            .overridden(t -> UnconfinedFluidTankOverridden.setupOutputOverriddenFromBasicMachine(t, this))
            .cached(true)
            .build();
        recipeOutputFluids = new FluidStack[outputFluids.getSlotCount()];
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        MTEBasicMachineWithRecipeAccessor self = (MTEBasicMachineWithRecipeAccessor) this;
        return new MultiFluidBasicMachineWithRecipe(
            this.mName,
            this.mTier,
            this.mDescriptionArray,
            self.unconfined$mRecipes(),
            this.mInputSlotCount,
            this.mOutputItems == null ? 0 : this.mOutputItems.length,
            self.unconfined$mTankCapacity(),
            this.mAmperage,
            this.mTextures,
            self.unconfined$mSoundResource(),
            self.unconfined$mSpecialEffect()
        ).setProgressBarTexture(self.unconfined$progressBarTexture())
            .setRecipeCatalystPriority(self.unconfined$recipeCatalystPriority());
    }

    @Override
    public FinalArrayAccessor<FluidStack> getRecipeOutputAccessor() {
        return () -> recipeOutputFluids;
    }
}

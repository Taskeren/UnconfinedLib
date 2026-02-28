package unconfined.core.mixins.misc;

import com.gtnewhorizons.modularui.api.drawable.FallbackableUITexture;
import gregtech.api.enums.SoundResource;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.recipe.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MTEBasicMachineWithRecipe.class, remap = false)
public interface MTEBasicMachineWithRecipeAccessor {

    @Accessor("mRecipes")
    RecipeMap<?> unconfined$mRecipes();

    @Accessor("mTankCapacity")
    int unconfined$mTankCapacity();

    @Accessor("mSoundResource")
    SoundResource unconfined$mSoundResource();

    @Accessor("mSpecialEffect")
    MTEBasicMachineWithRecipe.SpecialEffects unconfined$mSpecialEffect();

    @Accessor("progressBarTexture")
    FallbackableUITexture unconfined$progressBarTexture();

    @Accessor("recipeCatalystPriority")
    int unconfined$recipeCatalystPriority();

}

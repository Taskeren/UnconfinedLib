package unconfined.core.mixins.misc;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unconfined.api.event.RecipeMapBuildingEvent;

@Mixin(value = RecipeMapBuilder.class, remap = false)
public class RecipeMapBuilderMixin<B extends RecipeMapBackend> {

    @Shadow
    @Final
    private String unlocalizedName;

    @Inject(method = "build", at = @At(value = "NEW", target = "(Ljava/lang/String;Lgregtech/api/recipe/RecipeMapBackend;Lgregtech/api/recipe/RecipeMapFrontend;)Lgregtech/api/recipe/RecipeMap;"))
    private void unconfined$postRecipeMapBuildingEvent(CallbackInfoReturnable<RecipeMap<B>> cir) {
        MinecraftForge.EVENT_BUS.post(new RecipeMapBuildingEvent(unlocalizedName, (RecipeMapBuilder<?>) (Object) this));
    }

}

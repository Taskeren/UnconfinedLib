package unconfined.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/// Fired when the [RecipeMapBuilder] is about to instantiate the [RecipeMap].
@RequiredArgsConstructor
public class RecipeMapBuildingEvent extends Event {

    @Getter
    private final String unlocalizedName;
    @Getter
    private final RecipeMapBuilder<?> builder;

}

package com.minecolonies.core.client.recipe;

import com.ldtteam.domumornamentum.recipe.architectscutter.ArchitectsCutterRecipe;
import com.ldtteam.domumornamentum.recipe.architectscutter.ArchitectsCutterRecipeInput;
import com.ldtteam.domumornamentum.recipe.ModRecipeTypes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.Level;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import java.util.Optional;

import java.util.List;

/** Immutable client-side snapshot of the recipes supplied by {@code RecipesReceivedEvent}. */
public final class ClientRecipeCache
{
    private static volatile RecipeMap recipes = RecipeMap.EMPTY;

    private ClientRecipeCache()
    {
    }

    public static void replace(final RecipeMap newRecipes)
    {
        recipes = newRecipes;
    }

    public static void clear()
    {
        recipes = RecipeMap.EMPTY;
    }

    /**
     * Get the latest complete recipe snapshot supplied by RecipesReceivedEvent.
     *
     * @return the current client recipe map.
     */
    public static RecipeMap getRecipes()
    {
        return recipes;
    }

    public static List<RecipeHolder<ArchitectsCutterRecipe>> getArchitectsCutterRecipes(
      final ArchitectsCutterRecipeInput input, final Level level)
    {
        return recipes.getRecipesFor(ModRecipeTypes.ARCHITECTS_CUTTER.get(), input, level).toList();
    }

    public static Optional<RecipeHolder<?>> byKey(final Identifier id)
    {
        return Optional.ofNullable(recipes.byKey(ResourceKey.create(Registries.RECIPE, id)));
    }
}

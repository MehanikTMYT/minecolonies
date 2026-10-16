package com.minecolonies.api.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Helpers for reading vanilla recipes for display and analysis purposes.
 * <p>
 * Since 26.1 {@link Recipe} no longer exposes {@code getIngredients()} or {@code getResultItem(RegistryAccess)}:
 * the inputs are described by the {@link net.minecraft.world.item.crafting.PlacementInfo} and the shown result by the
 * recipe {@link RecipeDisplay}. These helpers restore the two lookups MineColonies needs in a single place.
 */
public final class VanillaRecipeUtils
{
    private VanillaRecipeUtils()
    {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Get every recipe of a given type known to the manager. Since 26.1 {@link RecipeManager} no longer exposes
     * {@code getAllRecipesFor(RecipeType)} directly; recipes must be filtered out of the full recipe list instead.
     *
     * @param recipeManager the recipe manager to search.
     * @param type          the recipe type to filter by.
     * @param <I>           the recipe's input type.
     * @param <T>           the recipe type.
     * @return every recipe of the given type.
     */
    @NotNull
    public static <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getAllRecipesFor(
      @NotNull final RecipeManager recipeManager,
      @NotNull final RecipeType<T> type)
    {
        return getAllRecipesFor(RecipeMap.create(recipeManager.getRecipes()), type);
    }

    /**
     * Get every recipe of a given type from a recipe map.
     *
     * @param recipeMap the recipe map to search.
     * @param type      the recipe type to filter by.
     * @param <T>       the recipe type.
     * @return every recipe of the given type.
     */
    @NotNull
    public static <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getAllRecipesFor(
      @NotNull final RecipeMap recipeMap,
      @NotNull final RecipeType<T> type)
    {
        return List.copyOf(recipeMap.byType(type));
    }

    /**
     * Get the ingredients of a recipe, in placement order.
     *
     * @param recipe the recipe to read.
     * @return the ingredients, empty if the recipe cannot be placed on a grid.
     */
    @NotNull
    public static List<Ingredient> getIngredients(@NotNull final Recipe<?> recipe)
    {
        return recipe.placementInfo().ingredients();
    }

    /**
     * Get the result a recipe advertises to the recipe book and recipe viewers.
     *
     * @param recipe the recipe to read.
     * @param level  the level supplying the registries and fuel values needed to resolve the display.
     * @return the result stack, or {@link ItemStack#EMPTY} when there is no level or no static display result.
     */
    @NotNull
    public static ItemStack getResultItem(@NotNull final Recipe<?> recipe, @Nullable final Level level)
    {
        final List<RecipeDisplay> displays = recipe.display();
        if (level == null || displays.isEmpty())
        {
            return ItemStack.EMPTY;
        }

        return displays.getFirst().result().resolveForFirstStack(SlotDisplayContext.fromLevel(level));
    }
}

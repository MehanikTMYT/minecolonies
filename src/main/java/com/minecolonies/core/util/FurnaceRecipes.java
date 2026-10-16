package com.minecolonies.core.util;

import com.minecolonies.api.crafting.VanillaRecipeUtils;
import com.google.common.collect.ImmutableList;
import com.minecolonies.api.compatibility.IFurnaceRecipes;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.crafting.RecipeStorage;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FurnaceRecipes implements IFurnaceRecipes
{
    /**
     * Furnace recipes.
     */
    private Map<ItemStorage, RecipeStorage> recipes = new HashMap<>();
    private Map<ItemStorage, RecipeStorage> reverseRecipes = new HashMap<>();

    /**
     * Load all the recipes in the recipe storage.
     *
     * @param recipeMap  The recipe map to parse.
     */
    public void loadRecipes(final RecipeMap recipeMap, final Level level)
    {
        recipes.clear();
        reverseRecipes.clear();
        recipeMap.byType(RecipeType.SMELTING).forEach(holder -> {
            final SmeltingRecipe recipe = holder.value();
            final List<Ingredient> list = VanillaRecipeUtils.getIngredients(recipe);
            if (list.size() == 1)
            {
                for(final var itemHolder: list.get(0).items().toList())
                {
                    final ItemStack smeltable = new ItemStack(itemHolder);
                    if (!smeltable.isEmpty())
                    {
                        final RecipeStorage storage = RecipeStorage.builder()
                                .withInputs(ImmutableList.of(new ItemStorage(smeltable)))
                                .withPrimaryOutput(VanillaRecipeUtils.getResultItem(recipe, level))
                                .withGridSize(1)
                                .withIntermediate(Blocks.FURNACE)
                                .withRecipeId(holder.id().identifier())
                                .build();

                        recipes.put(storage.getCleanedInput().get(0), storage);

                        final ItemStack output = VanillaRecipeUtils.getResultItem(recipe, level).copy();
                        output.setCount(1);
                        reverseRecipes.put(new ItemStorage(output), storage);
                    }
                }
            }
        });
    }

    @Override
    public ItemStack getSmeltingResult(final ItemStack itemStack)
    {
        final RecipeStorage storage = recipes.getOrDefault(new ItemStorage(itemStack), null);
        if (storage != null)
        {
            return storage.getPrimaryOutput();
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public RecipeStorage getFirstSmeltingRecipeByResult(final ItemStorage storage)
    {
        return reverseRecipes.get(storage);
    }
}

package com.minecolonies.api.crafting;

import com.minecolonies.api.crafting.registry.ModRecipeSerializer;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.tileentities.AbstractTileEntityBarrel;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A vanilla recipe class describing the operation of the compost barrel.  Primarily this is just used to
 * assign different "strength" ratings to different input items; currently the actual outputs of the barrel
 * are fixed.
 */
public class CompostRecipe implements Recipe<SingleRecipeInput>
{
    public static final MapCodec<CompostRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
        .group(Ingredient.CODEC.fieldOf("input").forGetter(CompostRecipe::getInput),
          ExtraCodecs.POSITIVE_INT.optionalFieldOf("strength", 1).forGetter(CompostRecipe::getStrength))
        .apply(builder, CompostRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CompostRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, CompostRecipe::getInput,
                    ByteBufCodecs.VAR_INT, CompostRecipe::getStrength,
                    CompostRecipe::new);

    /**
     * The serializer of this recipe; recipe serializers are plain records holding the two codecs since 26.1.
     */
    public static final RecipeSerializer<CompostRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static final int FERMENT_TIME = 24000;
    private static final int COMPOST_RESULT = 6;

    private final Ingredient input;
    private final int strength;

    public CompostRecipe(@NotNull final Ingredient ingredient, final int strength)
    {
        this.input = ingredient;
        this.strength = strength;
    }

    @NotNull
    @Override
    public RecipeType<CompostRecipe> getType() { return ModRecipeSerializer.CompostRecipeType.get(); }

    /**
     * Get the input ingredient for this recipe (this is multiple alternative item types).
     * Use {@link #getCountedInput()} instead to also calculate the amount of items needed for one composting operation.
     *
     * @return The input.
     */
    public Ingredient getInput() { return this.input; }

    /**
     * Get the strength of this recipe.  A higher strength means fewer input items are required to craft the same
     * quantity of output compost/dirt.
     *
     * @return The strength; typically 2, 4, or 8, but other values are possible.
     */
    public int getStrength() { return this.strength; }

    /**
     * The number of ticks that this recipe should take to ferment into compost.
     * This is currently just for informational purposes and is not configurable (nor actually used).
     *
     * @return A number of ticks.
     */
    public int getFermentTime() { return FERMENT_TIME; }

    @Override
    public boolean matches(@NotNull final SingleRecipeInput input, @NotNull final Level worldIn)
    {
        return false;
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull final SingleRecipeInput input)
    {
        return getResultItem();
    }

    /**
     * Get the fixed result of this recipe; built fresh on every call, both to avoid sharing mutable state between
     * crafts and because {@link ModItems#compost}'s components aren't bound yet while this recipe is constructed
     * during datagen.
     *
     * @return the compost stack produced by one composting operation.
     */
    @NotNull
    public ItemStack getResultItem()
    {
        return new ItemStack(ModItems.compost, COMPOST_RESULT);
    }

    @Override
    public boolean showNotification()
    {
        return false;
    }

    @NotNull
    @Override
    public String group()
    {
        return "";
    }

    @NotNull
    @Override
    public RecipeSerializer<CompostRecipe> getSerializer()
    {
        return SERIALIZER;
    }

    @NotNull
    @Override
    public PlacementInfo placementInfo()
    {
        // Composting happens in the barrel, never on a crafting grid, so this recipe is not placeable.
        return PlacementInfo.NOT_PLACEABLE;
    }

    /**
     * PORT(26.1): RecipeManager warns "can't be placed due to empty ingredients" for every non-special recipe that is
     * NOT_PLACEABLE. Barrel recipes never belong in the recipe book, which is exactly what special means.
     */
    @Override
    public boolean isSpecial()
    {
        return true;
    }

    @NotNull
    @Override
    public RecipeBookCategory recipeBookCategory()
    {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    /**
     * Gets the input ingredient of this recipe including the specific count of items that are required for one
     * composting operation.
     *
     * @return The counted input ingredient.
     */
    @NotNull
    public List<Ingredient> getCountedInput()
    {
        return List.of(CountedIngredient.of(this.input, calculateIngredientCount()));
    }

    private int calculateIngredientCount()
    {
        return AbstractTileEntityBarrel.MAX_ITEMS / this.strength;
    }

    // Recipe viewers look better if we render these as many individual recipes rather than as
    // a few recipes with a massive number of alternative ingredients.
    @NotNull
    public static CompostRecipe individualize(@NotNull final Item item, @NotNull final RecipeHolder<CompostRecipe> recipe)
    {
        return new CompostRecipe(Ingredient.of(item), recipe.value().getStrength());
    }
}

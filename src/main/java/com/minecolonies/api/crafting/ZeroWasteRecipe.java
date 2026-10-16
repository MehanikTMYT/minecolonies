package com.minecolonies.api.crafting;

import com.google.common.collect.Lists;
import com.minecolonies.api.crafting.registry.ModRecipeSerializer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A shapeless recipe that discards any remaining items.  Mainly intended for mixing things into bottles or bowls
 * without leaving extra empties behind, but can be used for other things too.
 */
public class ZeroWasteRecipe extends NormalCraftingRecipe
{
    public ZeroWasteRecipe(@NotNull final ItemStackTemplate output,
                           @NotNull final List<Ingredient> inputs)
    {
        super(new Recipe.CommonInfo(true), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""));
        this.output = output;
        this.inputs = List.copyOf(inputs);
    }

    /**
     * The recipe result, kept as a template so that datagen can build recipes before item default components are
     * bound; only materialized into a real {@link ItemStack} on actual crafting via {@link #assemble}.
     */
    private final ItemStackTemplate output;

    /**
     * The recipe inputs; kept so that the serializer can write them back without going through the placement info.
     */
    private final List<Ingredient> inputs;

    /**
     * Get the fixed result of this recipe.
     *
     * @return the result stack.
     */
    @NotNull
    public ItemStackTemplate getOutput()
    {
        return this.output;
    }

    /**
     * Get the inputs of this recipe.
     *
     * @return the input ingredients.
     */
    @NotNull
    public List<Ingredient> getInputs()
    {
        return this.inputs;
    }

    @NotNull
    @Override
    public NonNullList<ItemStack> getRemainingItems(@NotNull final CraftingInput input)
    {
        final NonNullList<ItemStack> remainingItems = super.getRemainingItems(input);
        Collections.fill(remainingItems, ItemStack.EMPTY);
        return remainingItems;
    }

    @Override
    protected PlacementInfo createPlacementInfo()
    {
        return PlacementInfo.create(inputs);
    }

    @Override
    public boolean matches(final CraftingInput input, final Level level)
    {
        if (input.ingredientCount() != inputs.size())
        {
            return false;
        }
        final List<ItemStack> nonEmptyItems = input.items().stream().filter(stack -> !stack.isEmpty()).toList();
        return net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(nonEmptyItems, inputs) != null;
    }

    @Override
    public ItemStack assemble(final CraftingInput input)
    {
        return output.create();
    }

    @Override
    public List<RecipeDisplay> display()
    {
        return List.of(new ShapelessCraftingRecipeDisplay(
          inputs.stream().map(Ingredient::display).toList(),
          new SlotDisplay.ItemStackSlotDisplay(output),
          new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }

    @NotNull
    @Override
    public RecipeSerializer<ZeroWasteRecipe> getSerializer()
    {
        return SERIALIZER;
    }

    private static final MapCodec<ZeroWasteRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(ZeroWasteRecipe::getOutput),
                    Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(ZeroWasteRecipe::getInputs)
            ).apply(builder, ZeroWasteRecipe::new)
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, ZeroWasteRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, ZeroWasteRecipe::getOutput,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ZeroWasteRecipe::getInputs,
            ZeroWasteRecipe::new
    );

    /**
     * The serializer of this recipe; recipe serializers are plain records holding the two codecs since 26.1.
     */
    public static final RecipeSerializer<ZeroWasteRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    public static Builder build(@NotNull final RecipeCategory category,
                                @NotNull final ItemLike output,
                                final int count)
    {
        return new Builder(category, new ItemStackTemplate(output.asItem(), count));
    }

    public static Builder build(@NotNull final RecipeCategory category,
                                @NotNull final ItemStack output)
    {
        return new Builder(category, ItemStackTemplate.fromNonEmptyStack(output));
    }

    public static class Builder implements RecipeBuilder
    {
        private final RecipeCategory category;
        private final ItemStackTemplate output;
        private final List<Ingredient> ingredients = Lists.newArrayList();
        private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();

        public Builder(@NotNull final RecipeCategory category,
                       @NotNull final ItemStackTemplate output)
        {
            this.category = category;
            this.output = output;
        }

        public Builder requires(@NotNull final TagKey<Item> tag)
        {
            return this.requires(Ingredient.of(BuiltInRegistries.ITEM.get(tag).orElseThrow()));
        }

        public Builder requires(@NotNull final ItemLike item)
        {
            return this.requires(item, 1);
        }

        public Builder requires(@NotNull final ItemLike item, final int count)
        {
            for (int i = 0; i < count; ++i)
            {
                this.requires(Ingredient.of(item));
            }
            return this;
        }

        public Builder requires(@NotNull final Ingredient ingredient)
        {
            return this.requires(ingredient, 1);
        }

        public Builder requires(@NotNull final Ingredient ingredient, final int count)
        {
            for (int i = 0; i < count; ++i)
            {
                this.ingredients.add(ingredient);
            }
            return this;
        }

        @NotNull
        public Builder unlockedBy(@NotNull final String name, @NotNull final Criterion<?> criterion)
        {
            this.advancementBuilder.unlockedBy(name, criterion);
            return this;
        }

        @NotNull
        public Item getResult()
        {
            return this.output.item().value();
        }

        @NotNull
        @Override
        public RecipeBuilder group(@Nullable String group)
        {
            return this;
        }

        @Override
        public ResourceKey<Recipe<?>> defaultId()
        {
            return RecipeBuilder.getDefaultRecipeId(output);
        }

        @Override
        public void save(@NotNull final RecipeOutput consumer, @NotNull final ResourceKey<Recipe<?>> id)
        {
            final ZeroWasteRecipe recipe = new ZeroWasteRecipe(this.output, List.copyOf(this.ingredients));
            consumer.accept(id, recipe, advancementBuilder.build(consumer, id, category));
        }
    }
}

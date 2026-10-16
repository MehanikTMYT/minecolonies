package com.minecolonies.api.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/** Registry-aware ingredient plus the number of matching items required by a research. */
public record ResearchIngredient(Ingredient ingredient, int count)
{
    public static final Codec<ResearchIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Ingredient.CODEC.fieldOf("ingredient").forGetter(ResearchIngredient::ingredient),
      ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ResearchIngredient::count))
      .apply(instance, ResearchIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchIngredient> STREAM_CODEC = StreamCodec.composite(
      Ingredient.CONTENTS_STREAM_CODEC, ResearchIngredient::ingredient,
      ByteBufCodecs.VAR_INT, ResearchIngredient::count,
      ResearchIngredient::new);

    public ResearchIngredient
    {
        if (count <= 0)
        {
            throw new IllegalArgumentException("Research ingredient must be non-empty and have a positive count");
        }

        if (!ingredient.isCustom())
        {
            final HolderSet<Item> values = ingredient.getValues();
            if (!(values instanceof HolderSet.Named<Item> named) || named.isBound())
            {
                if (ingredient.isEmpty())
                {
                    throw new IllegalArgumentException("Research ingredient must be non-empty and have a positive count");
                }
            }
        }
    }

    public static ResearchIngredient of(final ItemLike item, final int count)
    {
        return new ResearchIngredient(Ingredient.of(item), count);
    }

    public static ResearchIngredient of(final HolderSet<Item> items, final int count)
    {
        return new ResearchIngredient(Ingredient.of(items), count);
    }

    public boolean test(final ItemStack stack)
    {
        return ingredient.test(stack) && stack.getCount() >= count;
    }

    public ItemStack[] getItems()
    {
        return ingredient.items().map(item -> new ItemStack(item, count)).toArray(ItemStack[]::new);
    }
}

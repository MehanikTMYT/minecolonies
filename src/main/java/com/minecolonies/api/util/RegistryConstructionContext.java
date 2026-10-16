package com.minecolonies.api.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Supplies the exact registry key while legacy block factories are constructing their properties.
 */
public final class RegistryConstructionContext
{
    private static final ThreadLocal<ResourceKey<Block>> BLOCK_KEY = new ThreadLocal<>();

    private RegistryConstructionContext()
    {
    }

    public static <B extends Block> B constructBlock(final Identifier id, final Supplier<B> factory)
    {
        if (BLOCK_KEY.get() != null)
        {
            throw new IllegalStateException("Nested MineColonies block construction for " + id);
        }

        BLOCK_KEY.set(ResourceKey.create(Registries.BLOCK, id));
        try
        {
            return factory.get();
        }
        finally
        {
            BLOCK_KEY.remove();
        }
    }

    public static BlockBehaviour.Properties applyBlockKey(final BlockBehaviour.Properties properties)
    {
        return properties.setId(Objects.requireNonNull(BLOCK_KEY.get(), "MineColonies block constructed outside its keyed registry factory"));
    }
}

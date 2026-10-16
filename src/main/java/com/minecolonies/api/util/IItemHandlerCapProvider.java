package com.minecolonies.api.util;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities.Item;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;

/**
 * Our class for to join {@link IItemHandler} providers, so we can have type independent code.
 */
@FunctionalInterface
public interface IItemHandlerCapProvider
{
    /**
     * @return direction-unaware itemHandler
     */
    @Nullable
    default IItemHandler getItemHandlerCap()
    {
        return getItemHandlerCap((Direction) null);
    }

    /**
     * @return direction-aware itemHandler
     */
    @Nullable
    IItemHandler getItemHandlerCap(final Direction direction);

    /**
     * Exposes the existing stable inventory through the transactional 26.1 item capability API.
     */
    @Nullable
    default ResourceHandler<ItemResource> getItemResourceHandlerCap(@Nullable final Direction direction)
    {
        final IItemHandler handler = getItemHandlerCap(direction);
        return handler == null ? null : LegacyItemHandlerResourceAdapter.of(handler);
    }

    public static IItemHandlerCapProvider wrap(final BlockEntity blockEntity)
    {
        return direction -> toLegacy(Item.BLOCK.getCapability(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, direction));
    }

    /**
     * @param sided if true then will use Direction aware capability, roughly should be true for machine-entities and false for mobs
     */
    public static IItemHandlerCapProvider wrap(final Entity entity, final boolean sided)
    {
        return sided ? direction -> toLegacy(Item.ENTITY_AUTOMATION.getCapability(entity, direction)) :
            direction -> toLegacy(Item.ENTITY.getCapability(entity, null));
    }

    public static IItemHandlerCapProvider wrap(final ItemStack itemStack)
    {
        return direction -> toLegacy(Item.ITEM.getCapability(itemStack, ItemAccess.forStack(itemStack)));
    }

    @Nullable
    private static IItemHandler toLegacy(@Nullable final ResourceHandler<ItemResource> handler)
    {
        return handler == null ? null : IItemHandler.of(handler);
    }
}

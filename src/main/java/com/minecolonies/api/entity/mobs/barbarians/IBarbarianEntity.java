package com.minecolonies.api.entity.mobs.barbarians;

import com.minecolonies.api.util.IItemHandlerCapProvider;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.capabilities.Capabilities.Item;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public interface IBarbarianEntity extends Enemy, CommandSource, IItemHandlerCapProvider
{
    @Override
    default void sendSystemMessage(final Component message) { }

    @Override
    default boolean acceptsSuccess() { return false; }

    @Override
    default boolean acceptsFailure() { return false; }

    @Override
    default boolean shouldInformAdmins() { return false; }

    @Override
    @Nullable
    default IItemHandler getItemHandlerCap(final Direction direction)
    {
        // LivingEntities have cap registered by forge
        final var handler = ((LivingEntity) this).getCapability(Item.ENTITY);
        return handler == null ? null : IItemHandler.of(handler);
    }
}

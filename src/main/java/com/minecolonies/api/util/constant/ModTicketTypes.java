package com.minecolonies.api.util.constant;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.TicketType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * MineColonies chunk ticket types.
 */
public final class ModTicketTypes
{
    public static final DeferredRegister<TicketType> TICKET_TYPES =
      DeferredRegister.create(Registries.TICKET_TYPE, Constants.MOD_ID);

    public static final DeferredHolder<TicketType, TicketType> KEEP_LOADED =
      TICKET_TYPES.register(
        "initial_chunkload",
        () -> new TicketType(
          TicketType.NO_TIMEOUT,
          TicketType.FLAG_LOADING | TicketType.FLAG_SIMULATION));

    public static final DeferredHolder<TicketType, TicketType> POST_TELEPORT =
      TICKET_TYPES.register(
        "post_teleport",
        () -> new TicketType(
          20L * 5,
          TicketType.FLAG_LOADING));

    private ModTicketTypes()
    {
    }

    public static void register(final IEventBus modEventBus)
    {
        TICKET_TYPES.register(modEventBus);
    }
}

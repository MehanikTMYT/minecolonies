package com.minecolonies.core.event;

import com.minecolonies.api.colony.IColonyManager;
import static com.minecolonies.api.util.constant.Constants.MOD_ID;
import com.minecolonies.core.datalistener.*;
import com.minecolonies.core.entity.pathfinding.Pathfinding;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.core.util.BackUpHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.DefaultDataComponentsBoundEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event handler used to catch various forge events.
 */
public class FMLEventHandler
{
    @SubscribeEvent
    public static void onDefaultDataComponentsBound(final DefaultDataComponentsBoundEvent event)
    {
        CrafterRecipeListener.applyPendingRecipes();
        RecruitmentItemsListener.bindPendingRecruitCosts();
        CustomVisitorListener.bindPendingVisitors();
        QuestJsonListener.bindPendingQuests();
        DiseasesListener.applyPendingDiseases();
        ModEquipmentTypes.initRegisterEquipmentTiers();
    }

    @SubscribeEvent
    public static void onServerTick(final ServerTickEvent.Pre event)
    {
        IColonyManager.getInstance().onServerTick(event);
        DataPackSyncEventHandler.ServerEvents.load(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerLogin(@NotNull final PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer)
        {
            // This automatically reloads the owner of the colony if failed.
            IColonyManager.getInstance().getIColonyByOwner(event.getEntity().level(), event.getEntity());
            //ColonyManager.syncAllColoniesAchievements();
        }
    }

    @SubscribeEvent
    public static void onAddReloadListenerEvent(@NotNull final AddServerReloadListenersEvent event)
    {
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "crafter_recipes"), new CrafterRecipeListener());
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "research"), new ResearchListener());
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "custom_visitor"), new CustomVisitorListener());
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "citizen_name"), new CitizenNameListener());
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "quest_json"), new QuestJsonListener());
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "item_nbt"), new ItemNbtListener());
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "study_item"), StudyItemListener.INSTANCE);
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "diseases"), new DiseasesListener());
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "recruitment_items"), new RecruitmentItemsListener());
    }

    @SubscribeEvent
    public static void onServerStarted(@NotNull final ServerStartedEvent event)
    {
        BackUpHelper.loadMissingColonies();
    }

    @SubscribeEvent
    public static void onWorldTick(final LevelTickEvent.Pre event)
    {
        IColonyManager.getInstance().onWorldTick(event);
    }

    @SubscribeEvent
    public static void onServerAboutToStart(@NotNull final ServerAboutToStartEvent event)
    {
        IColonyManager.getInstance().getRecipeManager().reset();
    }

    @SubscribeEvent
    public static void onServerStopped(@NotNull final ServerStoppingEvent event)
    {
        Pathfinding.shutdown();
        DataPackSyncEventHandler.ServerEvents.reset();
    }
}

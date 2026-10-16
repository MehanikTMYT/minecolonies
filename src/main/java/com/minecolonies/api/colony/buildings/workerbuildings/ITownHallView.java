package com.minecolonies.api.colony.buildings.workerbuildings;

import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.colonyEvents.descriptions.IColonyEventDescription;
import com.minecolonies.api.colony.permissions.PermissionEvent;
import com.minecolonies.api.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.List;

public interface ITownHallView extends IBuildingView
{
    /**
     * Get a list of permission events.
     *
     * @return a copy of the list of events.
     */
    List<PermissionEvent> getPermissionEvents();

    /**
     * Gets a list if colony events.
     * 
     * @return a copy of the list of events.
     */
    List<IColonyEventDescription> getColonyEvents();

    /**
     * Check if the player can use the teleport command.
     *
     * @return true if so.
     */
    boolean canPlayerUseTP();

    /**
     * Getter for the mapdata.
     * @return the original list.
     */
    List<MapEntry> getMapDataList();

    public record MapEntry(MapId mapId, MapItemSavedData mapData)
    {
        public static final StreamCodec<RegistryFriendlyByteBuf, MapEntry> STREAM_CODEC =
            StreamCodec.composite(MapId.STREAM_CODEC,
                MapEntry::mapId,
                StreamCodec.of(
                  (buf, data) -> buf.writeNbt((CompoundTag) Utils.serializeCodecMess(MapItemSavedData.CODEC, buf.registryAccess(), data)),
                  buf -> Utils.deserializeCodecMess(MapItemSavedData.CODEC, buf.registryAccess(), buf.readNbt())),
                MapEntry::mapData,
                MapEntry::new);
    }
}

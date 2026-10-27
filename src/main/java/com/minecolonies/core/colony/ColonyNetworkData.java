package com.minecolonies.core.colony;

import com.minecolonies.api.colony.claim.ChunkClaimData;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.Utils;
import com.minecolonies.core.datalistener.CitizenNameListener;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Set;

/**
 * Writes colony view packets without loading the client-side ColonyView implementation.
 * Keep this class free of client references: dedicated servers use it when a player subscribes.
 */
public final class ColonyNetworkData
{
    private static final int REQUEST_MANAGER_MAX_SIZE = 700000;

    private ColonyNetworkData()
    {
    }

    public static void serializeNetworkData(@NotNull Colony colony, @NotNull RegistryFriendlyByteBuf buf, boolean hasNewSubscribers)
    {
        //  General Attributes
        buf.writeUtf(colony.getName());
        buf.writeUtf(colony.getDimension().identifier().toString());
        buf.writeBlockPos(colony.getCenter());
        //  Citizenry
        buf.writeInt(colony.getCitizenManager().getMaxCitizens());
        buf.writeInt(colony.getCitizenManager().getPotentialMaxCitizens());

        final Set<Block> freeBlocks = colony.getFreeBlocks();
        final Set<BlockPos> freePos = colony.getFreePositions();
        final Map<BlockPos, BlockState> waypoints = colony.getWayPoints();

        buf.writeInt(freeBlocks.size());
        for (final Block block : freeBlocks)
        {
            buf.writeUtf(BuiltInRegistries.BLOCK.getKey(block).toString());
        }

        buf.writeInt(freePos.size());
        for (final BlockPos block : freePos)
        {
            buf.writeBlockPos(block);
        }
        buf.writeDouble(colony.getOverallHappiness());

        buf.writeInt(waypoints.size());
        for (final Map.Entry<BlockPos, BlockState> block : waypoints.entrySet())
        {
            buf.writeBlockPos(block.getKey());
            buf.writeInt(Block.getId(block.getValue()));
        }

        buf.writeInt(colony.getLastContactInHours());
        buf.writeUtf(colony.getTextureStyleId());

        buf.writeUtf(colony.getNameStyle());
        buf.writeInt(CitizenNameListener.nameFileMap.size());
        for (final String nameFileIndex : CitizenNameListener.nameFileMap.keySet())
        {
            buf.writeUtf(nameFileIndex);
        }
        //  Citizens are sent as a separate packet

        if (colony.getRequestManager() != null && (colony.getRequestManager().isDirty() || hasNewSubscribers))
        {
            final int preIndex = buf.writerIndex();
            try
            {
                buf.writeBoolean(true);
                colony.getRequestManager().serialize(StandardFactoryController.getInstance(), buf);
                final int postSize = buf.writerIndex();
                if ((postSize - preIndex) >= REQUEST_MANAGER_MAX_SIZE)
                {
                    Log.getLogger().warn("Colony " + colony.getID() + " has a very big memory imprint, this could be a memory leak, please contact the mod author!");
                }
            }
            catch (Exception e)
            {
                buf.writerIndex(preIndex);
                Log.getLogger().warn("Error during request manager serialization for:" + colony.getID(), e);
                colony.getRequestManager().reset();
                buf.writeBoolean(false);
            }
        }
        else
        {
            buf.writeBoolean(false);
        }

        buf.writeInt(colony.getRaiderManager().getLastSpawnPoints().size());
        for (final BlockPos block : colony.getRaiderManager().getLastSpawnPoints())
        {
            buf.writeBlockPos(block);
        }

        buf.writeInt(colony.getTeamColonyColor().ordinal());
        Utils.serializeCodecMess(BannerPatternLayers.STREAM_CODEC, buf, colony.getColonyFlag());
        buf.writeLong(colony.getMercenaryUseTime());

        buf.writeUtf(colony.getStructurePack());
        buf.writeBoolean(colony.getRaiderManager().isRaided());
        buf.writeBoolean(colony.getRaiderManager().areSpiesEnabled());

        if (hasNewSubscribers || colony.isTicketedChunksDirty())
        {
            buf.writeInt(colony.getTicketedChunks().size());
            for (final long pos : colony.getTicketedChunks())
            {
                buf.writeLong(pos);
            }
        }
        else
        {
            buf.writeInt(-1);
        }

        final Long2ObjectMap<ChunkClaimData> colonyClaimData = colony.getClaimData();
        buf.writeInt(colonyClaimData.size());
        for (final Long2ObjectMap.Entry<ChunkClaimData> entry : colonyClaimData.long2ObjectEntrySet())
        {
            buf.writeLong(entry.getLongKey());
            entry.getValue().serialize(buf);
        }

        final CompoundTag graveTag = new CompoundTag();
        colony.getGraveManager().write(graveTag);
        buf.writeNbt(graveTag);     // this could be more efficient, but it should usually be short anyway
        colony.getStatisticsManager().serialize(buf, hasNewSubscribers);
        colony.getQuestManager().serialize(buf, hasNewSubscribers);
        buf.writeInt(colony.getDay());
        buf.writeNbt(colony.getTravellingManager().serializeNBT(buf.registryAccess()));
        colony.getConnectionManager().serializeToView(buf);
        colony.getSettings().serializeToView(buf);
    }
}

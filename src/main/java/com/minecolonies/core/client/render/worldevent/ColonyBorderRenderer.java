package com.minecolonies.core.client.render.worldevent;

import com.ldtteam.structurize.items.ModItems;
import com.ldtteam.structurize.util.WorldRenderMacros;
import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.claim.IChunkClaimData;
import com.minecolonies.core.MineColonies;
import net.minecraft.ChatFormatting;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColonyBorderRenderer
{
    private static final int RENDER_DIST_THRESHOLD = 3;
    private static final int CHUNK_SIZE = 16;
    private static final int PLAYER_CHUNK_STEP = CHUNK_SIZE / 4;

    private static List<BorderLine> colonies = List.of();
    private static List<BorderLine> chunktickets = List.of();
    private static ChunkPos                     lastPlayerChunkPos = null;
    private static IColonyView lastColony = null;

    static void render(final WorldEventContext ctx)
    {
        if (ctx.mainHandItem.getItem() != ModItems.buildTool.get() || !ctx.hasNearestColony())
        {
            return;
        }

        final ChunkPos playerChunkPos = ChunkPos.containing(ctx.clientPlayer.blockPosition());

        if (lastColony != ctx.nearestColony || !playerChunkPos.equals(lastPlayerChunkPos))
        {
            lastColony = ctx.nearestColony;
            lastPlayerChunkPos = playerChunkPos;

            final Map<ChunkPos, Integer> coloniesMap = new HashMap<>();
            final Map<ChunkPos, Integer> chunkticketsMap = new HashMap<>();
            final int nearestColonyId = ctx.nearestColony.getID();
            final int playerRenderDist = Math.max(ctx.clientRenderDist - RENDER_DIST_THRESHOLD, 2);
            final int range = Math.max(ctx.clientRenderDist, MineColonies.getConfig().getServer().maxColonySize.get());

            for (int chunkX = -range; chunkX <= range; chunkX++)
            {
                for (int chunkZ = -range; chunkZ <= range; chunkZ++)
                {
                    final LevelChunk chunk = ctx.clientLevel.getChunk(playerChunkPos.x() + chunkX, playerChunkPos.z() + chunkZ);
                    if (chunk.isEmpty()) { continue; }
                    final ChunkPos chunkPos = chunk.getPos();

                    final IChunkClaimData cap = IColonyManager.getInstance().getClaimData(ctx.nearestColony.getDimension(), chunkPos);;
                    if (cap != null)
                    {
                        coloniesMap.put(chunkPos, cap.getOwningColony());
                    }

                    if (ctx.nearestColony.getTicketedChunks().contains(chunkPos.pack()))
                    {
                        chunkticketsMap.put(chunkPos, nearestColonyId);
                    }
                    else
                    {
                        chunkticketsMap.put(chunkPos, 0);
                    }
                }
            }

            colonies = draw(ctx, coloniesMap, nearestColonyId, playerChunkPos, playerRenderDist);
            chunktickets = draw(ctx, chunkticketsMap, nearestColonyId, playerChunkPos, playerRenderDist);
        }

        final List<BorderLine> lines = ctx.mc.hasControlDown() ? chunktickets : colonies;
        if (lines.isEmpty())
        {
            return;
        }

        ctx.pushPoseCameraToPos(lastPlayerChunkPos.getWorldPosition());
        final VertexConsumer buffer = ctx.bufferSource.getBuffer(WorldRenderMacros.LINES);
        lines.forEach(line -> line.submit(ctx.poseStack.last().pose(), buffer));
        ctx.popPose();
    }

    private static List<BorderLine> draw(final WorldEventContext ctx,
        final Map<ChunkPos, Integer> mapToDraw,
        final int playerColonyId,
        final ChunkPos playerChunkPos,
        final int playerRenderDist)
    {
        final Map<Integer, Integer> colonyColours = new HashMap<>();
        final boolean useColonyColour = IMinecoloniesAPI.getInstance().getConfig().getClient().colonyteamborders.get();

        final LineCollector buf = new LineCollector();
        mapToDraw.forEach((chunkPos, colonyId) -> {
            if (colonyId == 0 || chunkPos.x() <= playerChunkPos.x() - playerRenderDist || chunkPos.x() >= playerChunkPos.x() + playerRenderDist
                || chunkPos.z() <= playerChunkPos.z() - playerRenderDist || chunkPos.z() >= playerChunkPos.z() + playerRenderDist)
            {
                return;
            }

            final boolean isPlayerChunkX = colonyId == playerColonyId && chunkPos.x() == playerChunkPos.x();
            final boolean isPlayerChunkZ = colonyId == playerColonyId && chunkPos.z() == playerChunkPos.z();
            final float minX = chunkPos.getMinBlockX() - playerChunkPos.getMinBlockX();
            final float maxX = chunkPos.getMaxBlockX() - playerChunkPos.getMinBlockX() + 1.0f;
            final float minZ = chunkPos.getMinBlockZ() - playerChunkPos.getMinBlockZ();
            final float maxZ = chunkPos.getMaxBlockZ() - playerChunkPos.getMinBlockZ() + 1.0f;
            final int minY = ctx.clientLevel.getMinY();
            final int maxY = ctx.clientLevel.getMaxY();
            final int testedColonyId = colonyId;

            if (useColonyColour)
            {
                buf.defaultColor = colonyColours.computeIfAbsent(colonyId, id ->
                {
                    final IColonyView colony = IMinecoloniesAPI.getInstance().getColonyManager().getColonyView(id, ctx.clientLevel.dimension());
                    final ChatFormatting team = colony != null ? colony.getTeamColonyColor()
                            : id == playerColonyId ? ChatFormatting.WHITE : ChatFormatting.RED;
                    return (team.getColor() == null ? 0xffffff : team.getColor()) | 0xff000000;
                });
            }
            else if (colonyId == playerColonyId)
            {
                buf.defaultColor = 0xffffffff;
            }
            else
            {
                buf.defaultColor = 0xffff4646;
            }

            // ChunkPos is a record now, so the neighbours have to be allocated instead of reusing a mutable key.
            final boolean north = mapToDraw.getOrDefault(new ChunkPos(chunkPos.x(), chunkPos.z() - 1), -1) != testedColonyId;
            final boolean south = mapToDraw.getOrDefault(new ChunkPos(chunkPos.x(), chunkPos.z() + 1), -1) != testedColonyId;
            final boolean east = mapToDraw.getOrDefault(new ChunkPos(chunkPos.x() + 1, chunkPos.z()), -1) != testedColonyId;
            final boolean west = mapToDraw.getOrDefault(new ChunkPos(chunkPos.x() - 1, chunkPos.z()), -1) != testedColonyId;

            // vert lines
            if (north || west)
            {
                buf.addVertex(minX, minY, minZ).setDefaultColor();
                buf.addVertex(minX, maxY, minZ).setDefaultColor();
            }
            if (north || east)
            {
                buf.addVertex(maxX, minY, minZ).setDefaultColor();
                buf.addVertex(maxX, maxY, minZ).setDefaultColor();
            }
            if (south || west)
            {
                buf.addVertex(minX, minY, maxZ).setDefaultColor();
                buf.addVertex(minX, maxY, maxZ).setDefaultColor();
            }
            if (south || east)
            {
                buf.addVertex(maxX, minY, maxZ).setDefaultColor();
                buf.addVertex(maxX, maxY, maxZ).setDefaultColor();
            }

            // horizontal lines
            if (north)
            {
                if (isPlayerChunkX)
                {
                    for (int shift = PLAYER_CHUNK_STEP; shift < CHUNK_SIZE; shift += PLAYER_CHUNK_STEP)
                    {
                        buf.addVertex(minX + shift, minY, minZ).setDefaultColor();
                        buf.addVertex(minX + shift, maxY, minZ).setDefaultColor();
                    }
                    for (int y = minY + PLAYER_CHUNK_STEP; y < maxY; y += PLAYER_CHUNK_STEP)
                    {
                        buf.addVertex(minX, y, minZ).setDefaultColor();
                        buf.addVertex(maxX, y, minZ).setDefaultColor();
                    }
                }
                else
                {
                    for (int y = minY + CHUNK_SIZE; y < maxY; y += CHUNK_SIZE)
                    {
                        buf.addVertex(minX, y, minZ).setDefaultColor();
                        buf.addVertex(maxX, y, minZ).setDefaultColor();
                    }
                }
            }
            if (south)
            {
                if (isPlayerChunkX)
                {
                    for (int shift = PLAYER_CHUNK_STEP; shift < CHUNK_SIZE; shift += PLAYER_CHUNK_STEP)
                    {
                        buf.addVertex(minX + shift, minY, maxZ).setDefaultColor();
                        buf.addVertex(minX + shift, maxY, maxZ).setDefaultColor();
                    }
                    for (int y = minY + PLAYER_CHUNK_STEP; y < maxY; y += PLAYER_CHUNK_STEP)
                    {
                        buf.addVertex(minX, y, maxZ).setDefaultColor();
                        buf.addVertex(maxX, y, maxZ).setDefaultColor();
                    }
                }
                else
                {
                    for (int y = minY + CHUNK_SIZE; y < maxY; y += CHUNK_SIZE)
                    {
                        buf.addVertex(minX, y, maxZ).setDefaultColor();
                        buf.addVertex(maxX, y, maxZ).setDefaultColor();
                    }
                }
            }
            if (west)
            {
                if (isPlayerChunkZ)
                {
                    for (int shift = PLAYER_CHUNK_STEP; shift < CHUNK_SIZE; shift += PLAYER_CHUNK_STEP)
                    {
                        buf.addVertex(minX, minY, minZ + shift).setDefaultColor();
                        buf.addVertex(minX, maxY, minZ + shift).setDefaultColor();
                    }
                    for (int y = minY + PLAYER_CHUNK_STEP; y < maxY; y += PLAYER_CHUNK_STEP)
                    {
                        buf.addVertex(minX, y, minZ).setDefaultColor();
                        buf.addVertex(minX, y, maxZ).setDefaultColor();
                    }
                }
                else
                {
                    for (int y = minY + CHUNK_SIZE; y < maxY; y += CHUNK_SIZE)
                    {
                        buf.addVertex(minX, y, minZ).setDefaultColor();
                        buf.addVertex(minX, y, maxZ).setDefaultColor();
                    }
                }
            }
            if (east)
            {
                if (isPlayerChunkZ)
                {
                    for (int shift = PLAYER_CHUNK_STEP; shift < CHUNK_SIZE; shift += PLAYER_CHUNK_STEP)
                    {
                        buf.addVertex(maxX, minY, minZ + shift).setDefaultColor();
                        buf.addVertex(maxX, maxY, minZ + shift).setDefaultColor();
                    }
                    for (int y = minY + PLAYER_CHUNK_STEP; y < maxY; y += PLAYER_CHUNK_STEP)
                    {
                        buf.addVertex(maxX, y, minZ).setDefaultColor();
                        buf.addVertex(maxX, y, maxZ).setDefaultColor();
                    }
                }
                else
                {
                    for (int y = minY + CHUNK_SIZE; y < maxY; y += CHUNK_SIZE)
                    {
                        buf.addVertex(maxX, y, minZ).setDefaultColor();
                        buf.addVertex(maxX, y, maxZ).setDefaultColor();
                    }
                }
            }
        });

        return List.copyOf(buf.lines);
    }

    /**
     * Cleanup on logout.
     */
    public static void cleanup()
    {
        colonies = List.of();
        chunktickets = List.of();
        lastColony = null;
        lastPlayerChunkPos = null;
    }

    private record BorderLine(float x1, float y1, float z1, float x2, float y2, float z2, int color)
    {
        private void submit(final org.joml.Matrix4f matrix, final VertexConsumer buffer)
        {
            buffer.addVertex(matrix, x1, y1, z1).setColor(color);
            buffer.addVertex(matrix, x2, y2, z2).setColor(color);
        }
    }

    private static final class LineCollector
    {
        private final List<BorderLine> lines = new ArrayList<>();
        private int defaultColor;
        private float x;
        private float y;
        private float z;
        private boolean hasFirst;

        private LineCollector addVertex(final float x, final float y, final float z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        private void setDefaultColor()
        {
            if (hasFirst)
            {
                final BorderLine first = lines.remove(lines.size() - 1);
                lines.add(new BorderLine(first.x1, first.y1, first.z1, x, y, z, defaultColor));
            }
            else
            {
                lines.add(new BorderLine(x, y, z, 0, 0, 0, defaultColor));
            }
            hasFirst = !hasFirst;
        }
    }
}

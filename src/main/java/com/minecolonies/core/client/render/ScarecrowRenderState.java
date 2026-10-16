package com.minecolonies.core.client.render;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

/**
 * Snapshot of the scarecrow data required by the deferred block entity render pass.
 */
public class ScarecrowRenderState extends BlockEntityRenderState
{
    public boolean render;
    public boolean pumpkinHead;
    public Direction facing = Direction.NORTH;
    public final BlockModelRenderState lantern = new BlockModelRenderState();
}

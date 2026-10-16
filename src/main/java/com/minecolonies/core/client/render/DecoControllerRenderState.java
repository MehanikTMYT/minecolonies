package com.minecolonies.core.client.render;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.phys.Vec3;

/** Deferred snapshot of the decoration controller block and its surface offset. */
public class DecoControllerRenderState extends BlockEntityRenderState
{
    public boolean render;
    public Vec3 translation = Vec3.ZERO;
    public final BlockModelRenderState model = new BlockModelRenderState();
}

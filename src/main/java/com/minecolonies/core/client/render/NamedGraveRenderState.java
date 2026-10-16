package com.minecolonies.core.client.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/** Snapshot of the named grave text and orientation for deferred rendering. */
public class NamedGraveRenderState extends BlockEntityRenderState
{
    public boolean render;
    public Direction facing = Direction.SOUTH;
    public List<FormattedCharSequence> lines = List.of();
}

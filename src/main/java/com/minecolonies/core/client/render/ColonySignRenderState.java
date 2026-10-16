package com.minecolonies.core.client.render;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/** Deferred snapshot for a colony direction sign. */
public class ColonySignRenderState extends BlockEntityRenderState
{
    public boolean render;
    public float rotation;
    public final BlockModelRenderState model = new BlockModelRenderState();
    public List<TextLine> lines = List.of();

    public record TextLine(FormattedCharSequence text, float y) { }
}

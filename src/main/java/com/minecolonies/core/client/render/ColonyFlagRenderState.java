package com.minecolonies.core.client.render;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

/** Snapshot of colony flag and creative placeholder rendering data. */
public class ColonyFlagRenderState extends BannerRenderState
{
    public final ItemStackRenderState placeholder = new ItemStackRenderState();
    public Transformation placeholderTransformation = Transformation.IDENTITY;
}

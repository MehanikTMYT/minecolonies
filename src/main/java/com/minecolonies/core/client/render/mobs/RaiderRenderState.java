package com.minecolonies.core.client.render.mobs;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/**
 * Render state of the MineColonies raiders.
 * <p>
 * Renderers no longer see the entity while rendering since 26.1, so the per-entity texture variant has to be captured
 * during extraction instead of being read from the entity inside {@code getTextureLocation}.
 */
public class RaiderRenderState extends HumanoidRenderState
{
    /**
     * The texture variant this raider was spawned with.
     */
    public int textureId;
}

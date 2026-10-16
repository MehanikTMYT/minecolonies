package com.minecolonies.core.client.render.cavalry;

import net.minecraft.client.renderer.entity.state.HorseRenderState;

/**
 * Render state of the cavalry horses.
 * <p>
 * The combat readiness has to be captured during extraction, because the overlay layer no longer sees the entity.
 */
public class CavalryHorseRenderState extends HorseRenderState
{
    /**
     * How many of the five readiness segments the overlay should show, 0 (on cooldown) to 5 (ready).
     */
    public int combatReadinessSegments;
}

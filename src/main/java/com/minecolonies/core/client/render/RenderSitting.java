package com.minecolonies.core.client.render;

import com.minecolonies.core.entity.other.SittingEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.NotNull;

/**
 * Empty renderer for entities which should not actually be rendered.
 */
public class RenderSitting extends EntityRenderer<SittingEntity, EntityRenderState>
{
    public RenderSitting(final EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState()
    {
        return new EntityRenderState();
    }

    @Override
    public boolean shouldRender(@NotNull SittingEntity entity, @NotNull Frustum clippingHelper, double x, double y, double z)
    {
        return false;
    }
}

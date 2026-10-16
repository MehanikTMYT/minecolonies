package com.minecolonies.core.client.render.projectile;

import com.minecolonies.api.util.constant.Constants;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Custom renderer for the fire arrows.
 */
public class FireArrowRenderer extends ArrowRenderer<AbstractArrow, ArrowRenderState>
{
    /**
     * Array of different textures.
     */
    private static final Identifier[] RES = new Identifier[]
                                                    {
                                                      Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/item/magicalarrows/magical_arrow1.png"),
                                                      Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/item/magicalarrows/magical_arrow2.png"),
                                                      Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/item/magicalarrows/magical_arrow3.png"),
                                                      Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/item/magicalarrows/magical_arrow4.png"),
                                                      Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/item/magicalarrows/magical_arrow5.png"),
                                                      Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/item/magicalarrows/magical_arrow6.png")
                                                    };

    public FireArrowRenderer(final EntityRendererProvider.Context context)
    {
        super(context);
    }

    @NotNull
    @Override
    public ArrowRenderState createRenderState()
    {
        return new ArrowRenderState();
    }

    @NotNull
    @Override
    protected Identifier getTextureLocation(@NotNull final ArrowRenderState state)
    {
        // ageInTicks carries the entity tick count, which is what selected the animation frame before.
        return RES[(int) state.ageInTicks % 6];
    }
}

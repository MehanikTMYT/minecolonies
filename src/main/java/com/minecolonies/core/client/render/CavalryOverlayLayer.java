package com.minecolonies.core.client.render;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.render.cavalry.CavalryHorseRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import javax.annotation.Nonnull;

public class CavalryOverlayLayer extends RenderLayer<CavalryHorseRenderState, HorseModel>
{
    /**
     * Alpha the overlay is drawn with, 0.85 of full opacity.
     */
    private static final int OVERLAY_ALPHA = (int) (0.85f * 255.0f);

    public CavalryOverlayLayer(final RenderLayerParent<CavalryHorseRenderState, HorseModel> parent)
    {
        super(parent);
    }

    /**
     * Renders the cavalry horse overlay layer, which decorates the horse
     * and indicates the horse's readiness for combat.
     *
     * @param poseStack           the pose stack
     * @param submitNodeCollector the collector to submit the overlay to
     * @param lightCoords         the packed light
     * @param state               the cavalry horse render state
     * @param yRot                the net head yaw
     * @param xRot                the head pitch
     */
    @Override
    public void submit(@Nonnull final PoseStack poseStack,
      @Nonnull final SubmitNodeCollector submitNodeCollector,
      final int lightCoords,
      @Nonnull final CavalryHorseRenderState state,
      final float yRot,
      final float xRot)
    {
        final Identifier overlayTexture = Identifier.fromNamespaceAndPath(
          Constants.MOD_ID,
          "textures/entity/horse/cavalry_overlay_layer" + state.combatReadinessSegments + ".png");

        submitNodeCollector.submitModel(this.getParentModel(),
          state,
          poseStack,
          RenderTypes.entityTranslucent(overlayTexture),
          lightCoords,
          OverlayTexture.NO_OVERLAY,
          ARGB.color(OVERLAY_ALPHA, 255, 255, 255),
          null,
          state.outlineColor,
          null);
    }
}

package com.minecolonies.core.client.render;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.model.SpearModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/** Item special renderer for the three-dimensional spear model. */
public class SpearItemTileEntityRenderer implements NoDataSpecialModelRenderer
{
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/spear.png");

    private final SpearModel model;

    public SpearItemTileEntityRenderer(final SpearModel model)
    {
        this.model = model;
    }

    @Override
    public void submit(
      final PoseStack poseStack,
      final SubmitNodeCollector submitNodeCollector,
      final int lightCoords,
      final int overlayCoords,
      final boolean hasFoil,
      final int outlineColor)
    {
        submitNodeCollector.submitModelPart(
          this.model.root(), poseStack, this.model.renderType(TEXTURE), lightCoords, overlayCoords,
          null, false, hasFoil, -1, null, outlineColor);
    }

    @Override
    public void getExtents(final Consumer<Vector3fc> output)
    {
        this.model.root().getExtentsForGui(new PoseStack(), output);
    }

    /** Data-driven special model definition. */
    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type()
        {
            return MAP_CODEC;
        }

        @Override
        public SpearItemTileEntityRenderer bake(final SpecialModelRenderer.BakingContext context)
        {
            return new SpearItemTileEntityRenderer(new SpearModel(context.entityModelSet().bakeLayer(ModelLayers.TRIDENT)));
        }
    }
}

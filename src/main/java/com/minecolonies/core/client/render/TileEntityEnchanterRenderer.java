package com.minecolonies.core.client.render;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.tileentities.TileEntityEnchanter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.EnchantTableRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Renders the floating book above the enchanter's hut block.
 */
public class TileEntityEnchanterRenderer implements BlockEntityRenderer<TileEntityEnchanter, EnchantTableRenderState>
{
    /**
     * The book sprite, kept on the same atlas and at the same texture path as before.
     */
    public static final SpriteId TEXTURE_BOOK =
      new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block/enchanting_table_book"));

    /**
     * The sprite lookup used to resolve {@link #TEXTURE_BOOK} while drawing.
     */
    private final SpriteGetter sprites;

    /**
     * The book model to be rendered.
     */
    private final BookModel modelBook;

    /**
     * Create the renderer.
     *
     * @param context the context.
     */
    public TileEntityEnchanterRenderer(final BlockEntityRendererProvider.Context context)
    {
        this.sprites = context.sprites();
        this.modelBook = new BookModel(context.bakeLayer(ModelLayers.BOOK));
    }

    @NotNull
    @Override
    public EnchantTableRenderState createRenderState()
    {
        return new EnchantTableRenderState();
    }

    @Override
    public void extractRenderState(@NotNull final TileEntityEnchanter blockEntity,
      @NotNull final EnchantTableRenderState state,
      final float partialTicks,
      @NotNull final Vec3 cameraPosition,
      @Nullable final ModelFeatureRenderer.CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.time = blockEntity.tickCount + partialTicks;
        state.flip = Mth.lerp(partialTicks, blockEntity.pageFlipPrev, blockEntity.pageFlip);
        state.open = Mth.lerp(partialTicks, blockEntity.bookSpreadPrev, blockEntity.bookSpread);

        // Interpolate the book rotation the short way around the circle, as the old renderer did.
        float rotDelta = blockEntity.bookRotation - blockEntity.bookRotationPrev;
        while (rotDelta >= (float) Math.PI)
        {
            rotDelta -= (float) (Math.PI * 2);
        }
        while (rotDelta < (float) -Math.PI)
        {
            rotDelta += (float) (Math.PI * 2);
        }
        state.yRot = blockEntity.bookRotationPrev + rotDelta * partialTicks;
    }

    @Override
    public void submit(@NotNull final EnchantTableRenderState state,
      @NotNull final PoseStack poseStack,
      @NotNull final SubmitNodeCollector submitNodeCollector,
      @NotNull final CameraRenderState camera)
    {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.75F, 0.5F);
        poseStack.translate(0.0F, 0.1F + Mth.sin(state.time * 0.1F) * 0.01F, 0.0F);
        poseStack.mulPose(Axis.YP.rotation(-state.yRot));
        poseStack.mulPose(Axis.ZP.rotationDegrees(80.0F));

        final float flipA = Mth.frac(state.flip + 0.25F) * 1.6F - 0.3F;
        final float flipB = Mth.frac(state.flip + 0.75F) * 1.6F - 0.3F;
        final BookModel.State bookState =
          BookModel.State.forAnimation(state.time, Mth.clamp(flipA, 0.0F, 1.0F), Mth.clamp(flipB, 0.0F, 1.0F), state.open);

        submitNodeCollector.submitModel(this.modelBook,
          bookState,
          poseStack,
          state.lightCoords,
          OverlayTexture.NO_OVERLAY,
          -1,
          TEXTURE_BOOK,
          this.sprites,
          0,
          state.breakProgress);

        poseStack.popPose();
    }
}

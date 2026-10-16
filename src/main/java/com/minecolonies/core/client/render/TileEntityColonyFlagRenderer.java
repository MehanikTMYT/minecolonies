package com.minecolonies.core.client.render;

import com.ldtteam.structurize.blocks.ModBlocks;
import com.minecolonies.core.blocks.decorative.BlockColonyFlagBanner;
import com.minecolonies.core.blocks.decorative.BlockColonyFlagWallBanner;
import com.minecolonies.core.tileentities.TileEntityColonyFlag;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Renders colony banner patterns and the creative-mode substitution placeholder.
 */
public class TileEntityColonyFlagRenderer implements BlockEntityRenderer<TileEntityColonyFlag, ColonyFlagRenderState>
{
    private final BannerRenderer bannerRenderer;
    private final ItemModelResolver itemModelResolver;

    public TileEntityColonyFlagRenderer(final BlockEntityRendererProvider.Context context)
    {
        this.bannerRenderer = new BannerRenderer(context);
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public ColonyFlagRenderState createRenderState()
    {
        return new ColonyFlagRenderState();
    }

    @Override
    public void extractRenderState(
      final TileEntityColonyFlag flag,
      final ColonyFlagRenderState state,
      final float partialTicks,
      final Vec3 cameraPosition,
      final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(flag, state, partialTicks, cameraPosition, breakProgress);
        final BannerPatternLayers patterns = flag.getPatterns();
        state.patterns = new BannerPatternLayers(List.copyOf(patterns.layers()));
        state.baseColor = DyeColor.WHITE;

        final BlockState blockState = flag.getBlockState();
        if (blockState.getBlock() instanceof BlockColonyFlagBanner)
        {
            final float rotation = (float) (-blockState.getValue(BlockColonyFlagBanner.ROTATION) * 360) / 16.0F;
            state.attachmentType = BannerBlock.AttachmentType.GROUND;
            state.transformation = BannerRenderer.TRANSFORMATIONS.freeTransformations(blockState.getValue(BlockColonyFlagBanner.ROTATION));
            state.placeholderTransformation = new Transformation(
              new Matrix4f().translation(0.5F, 0.5F, 0.5F).rotate(Axis.YP.rotationDegrees(rotation)));
        }
        else if (blockState.getBlock() instanceof BlockColonyFlagWallBanner)
        {
            final float rotation = -blockState.getValue(BlockColonyFlagWallBanner.HORIZONTAL_FACING).toYRot();
            state.attachmentType = BannerBlock.AttachmentType.WALL;
            state.transformation = BannerRenderer.TRANSFORMATIONS.wallTransformation(blockState.getValue(BlockColonyFlagWallBanner.HORIZONTAL_FACING));
            state.placeholderTransformation = new Transformation(new Matrix4f()
              .translation(0.5F, -0.16666667F, 0.5F)
              .rotate(Axis.YP.rotationDegrees(rotation))
              .translate(0.0F, -0.3125F, -0.4375F));
        }
        else
        {
            state.attachmentType = BannerBlock.AttachmentType.GROUND;
            state.transformation = BannerRenderer.TRANSFORMATIONS.freeTransformations(0);
            state.placeholderTransformation = new Transformation(new Matrix4f().translation(0.5F, 0.5F, 0.5F));
        }

        final long gameTime = flag.getLevel() == null ? 0L : flag.getLevel().getGameTime();
        final BlockPos pos = flag.getBlockPos();
        state.phase = ((float) Math.floorMod(pos.getX() * 7 + pos.getY() * 9 + pos.getZ() * 13 + gameTime, 100L) + partialTicks) / 100.0F;

        final Minecraft minecraft = Minecraft.getInstance();
        if (flag.getLevel() != null && minecraft.player != null && minecraft.gameMode != null
              && minecraft.player.getMainHandItem().getItem() instanceof BannerItem
              && minecraft.gameMode.getPlayerMode() == GameType.CREATIVE)
        {
            this.itemModelResolver.updateForTopItem(
              state.placeholder,
              new ItemStack(ModBlocks.blockSubstitution.get()),
              ItemDisplayContext.FIXED,
              flag.getLevel(),
              minecraft.player,
              0);
        }
        else
        {
            state.placeholder.clear();
        }
    }

    @Override
    public void submit(
      final ColonyFlagRenderState state,
      final PoseStack poseStack,
      final SubmitNodeCollector submitNodeCollector,
      final CameraRenderState camera)
    {
        if (!state.placeholder.isEmpty())
        {
            poseStack.pushPose();
            poseStack.mulPose(state.placeholderTransformation);
            poseStack.translate(0.0D, 0.5D, 0.0D);
            poseStack.scale(0.75F, 0.75F, 0.75F);
            state.placeholder.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        this.bannerRenderer.submit(state, poseStack, submitNodeCollector, camera);
    }
}

package com.minecolonies.core.client.render;

import com.minecolonies.core.blocks.BlockDecorationController;
import com.minecolonies.core.tileentities.TileEntityDecorationController;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class TileEntityDecoControllerRenderer implements BlockEntityRenderer<TileEntityDecorationController, DecoControllerRenderState>
{
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

    private final BlockModelResolver blockModelResolver;

    public TileEntityDecoControllerRenderer(final BlockEntityRendererProvider.Context context)
    {
        this.blockModelResolver = context.blockModelResolver();
    }

    @Override
    public DecoControllerRenderState createRenderState()
    {
        return new DecoControllerRenderState();
    }

    @Override
    public void extractRenderState(
      final TileEntityDecorationController controller,
      final DecoControllerRenderState state,
      final float partialTicks,
      final Vec3 cameraPosition,
      final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(controller, state, partialTicks, cameraPosition, breakProgress);
        final Level level = controller.getLevel();
        final BlockState controllerState = controller.getBlockState();
        if (level == null || controllerState.isAir())
        {
            state.render = false;
            state.model.clear();
            return;
        }

        final Direction direction = controllerState.getValue(BlockDecorationController.FACING);
        final BlockPos offsetPos = controller.getBlockPos().relative(direction);
        final BlockState adjacentState = level.getBlockState(offsetPos);
        final VoxelShape shape = adjacentState.getShape(level, offsetPos);
        state.translation = shape.isEmpty() || Block.isShapeFullBlock(shape) ? Vec3.ZERO : surfaceOffset(shape, direction);
        state.render = true;
        this.blockModelResolver.update(state.model, controllerState, BLOCK_DISPLAY_CONTEXT);
    }

    private static Vec3 surfaceOffset(final VoxelShape shape, final Direction direction)
    {
        return switch (direction)
        {
            case UP -> new Vec3(0, shape.min(Direction.Axis.Y), 0);
            case DOWN -> new Vec3(0, shape.max(Direction.Axis.Y) - 1, 0);
            case NORTH -> new Vec3(0, 0, shape.max(Direction.Axis.Z) - 1);
            case SOUTH -> new Vec3(0, 0, shape.min(Direction.Axis.Z));
            case EAST -> new Vec3(shape.min(Direction.Axis.X), 0, 0);
            case WEST -> new Vec3(shape.max(Direction.Axis.X) - 1, 0, 0);
        };
    }

    @Override
    public void submit(
      final DecoControllerRenderState state,
      final PoseStack poseStack,
      final SubmitNodeCollector submitNodeCollector,
      final CameraRenderState camera)
    {
        if (!state.render)
        {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(state.translation.x, state.translation.y, state.translation.z);
        state.model.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}

package com.minecolonies.core.client.render;

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.blocks.huts.AbstractBlockMinecoloniesDefault;
import com.minecolonies.core.tileentities.TileEntityNamedGrave;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TileEntityNamedGraveRenderer implements BlockEntityRenderer<TileEntityNamedGrave, NamedGraveRenderState> {

    private static final int MAX_TEXT_SIZE = 20;

    private final Font font;

    /**
     * Basic rotation to achieve a certain direction.
     */
    private static final int BASIC_ROTATION = 90;

    /**
     * Rotate by amount to go east.
     */
    private static final int ROTATE_EAST = 1;

    /**
     * Rotate by amount to go north.
     */
    private static final int ROTATE_NORTH = 2;

    /**
     * Rotate by amount to go west.
     */
    private static final int ROTATE_WEST = 3;

    public TileEntityNamedGraveRenderer(final BlockEntityRendererProvider.Context context)
    {
        super();
        this.font = context.font();
    }

    @Override
    public NamedGraveRenderState createRenderState()
    {
        return new NamedGraveRenderState();
    }

    @Override
    public void extractRenderState(
      final TileEntityNamedGrave tileEntity,
      final NamedGraveRenderState state,
      final float partialTicks,
      final Vec3 cameraPosition,
      final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(tileEntity, state, partialTicks, cameraPosition, breakProgress);
        final BlockState blockState = tileEntity.getBlockState();
        state.render = blockState.getBlock() == ModBlocks.blockNamedGrave;
        state.facing = state.render ? blockState.getValue(AbstractBlockMinecoloniesDefault.FACING) : Direction.SOUTH;

        final List<String> textLines = tileEntity.getTextLines().isEmpty() ? List.of("Unknown Citizen") : List.copyOf(tileEntity.getTextLines());
        final List<FormattedCharSequence> lines = new ArrayList<>(textLines.size());
        for (final String original : textLines)
        {
            final String text = original.length() > MAX_TEXT_SIZE ? original.substring(0, MAX_TEXT_SIZE) : original;
            lines.add(FormattedCharSequence.forward(text, Style.EMPTY));
        }
        state.lines = List.copyOf(lines);
    }

    @Override
    public void submit(
      final NamedGraveRenderState state,
      final PoseStack matrixStack,
      final SubmitNodeCollector submitNodeCollector,
      final CameraRenderState camera)
    {
        if (!state.render)
        {
            return;
        }

        matrixStack.pushPose();
        switch (state.facing)
        {
            case NORTH -> {
                matrixStack.translate(0.5f, 1.18F, 0.48F);
                matrixStack.scale(0.006F, -0.006F, 0.006F);
                matrixStack.mulPose(Axis.YP.rotationDegrees(BASIC_ROTATION * ROTATE_NORTH));
            }
            case SOUTH -> {
                matrixStack.translate(0.5f, 1.18F, 0.54F);
                matrixStack.scale(0.006F, -0.006F, 0.006F);
            }
            case EAST -> {
                matrixStack.translate(0.54f, 1.18F, 0.5F);
                matrixStack.scale(0.006F, -0.006F, 0.006F);
                matrixStack.mulPose(Axis.YP.rotationDegrees(BASIC_ROTATION * ROTATE_EAST));
            }
            case WEST -> {
                matrixStack.translate(0.48f, 1.18F, 0.5F);
                matrixStack.scale(0.006F, -0.006F, 0.006F);
                matrixStack.mulPose(Axis.YP.rotationDegrees(BASIC_ROTATION * ROTATE_WEST));
            }
            default -> { }
        }

        for (int line = 0; line < state.lines.size(); line++)
        {
            final FormattedCharSequence text = state.lines.get(line);
            submitNodeCollector.submitText(
              matrixStack, -this.font.width(text) / 2.0F, line * 10.0F, text, false,
              Font.DisplayMode.NORMAL, state.lightCoords, 0xdcdcdc00, 0, 0);
        }
        matrixStack.popPose();
    }

    // this should be true for tileentities which render globally (no render bounding box), such as beacons.
    @Override
    public boolean shouldRenderOffScreen()
    {
        return false;
    }
}

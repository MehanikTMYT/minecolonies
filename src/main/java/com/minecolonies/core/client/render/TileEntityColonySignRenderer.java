package com.minecolonies.core.client.render;

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.core.client.render.worldevent.WorldEventContext;
import com.minecolonies.core.tileentities.TileEntityColonySign;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Style;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.api.util.constant.TranslationConstants.NEXT;
import static com.minecolonies.api.util.constant.TranslationConstants.PREVIOUS;
import static com.minecolonies.core.blocks.BlockColonySign.CONNECTED;

public class TileEntityColonySignRenderer implements BlockEntityRenderer<TileEntityColonySign, ColonySignRenderState>
{
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    private static final int MAX_TEXT_SIZE = 20;
    private final BlockModelResolver blockModelResolver;
    private final Font font;

    public TileEntityColonySignRenderer(final BlockEntityRendererProvider.Context context)
    {
        this.blockModelResolver = context.blockModelResolver();
        this.font = context.font();
    }

    @Override
    public ColonySignRenderState createRenderState()
    {
        return new ColonySignRenderState();
    }

    @Override
    public void extractRenderState(
      final TileEntityColonySign tileEntity,
      final ColonySignRenderState state,
      final float partialTicks,
      final Vec3 cameraPosition,
      final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(tileEntity, state, partialTicks, cameraPosition, breakProgress);
        final BlockState blockState = tileEntity.getBlockState();
        state.render = blockState.getBlock() == ModBlocks.blockColonySign;
        state.rotation = tileEntity.getRelativeRotation();
        if (!state.render)
        {
            state.model.clear();
            state.lines = List.of();
            return;
        }

        final boolean connected = tileEntity.getTargetColonyId() != tileEntity.getCachedSignAboveColony();
        this.blockModelResolver.update(state.model, blockState.setValue(CONNECTED, connected), BLOCK_DISPLAY_CONTEXT);
        final List<ColonySignRenderState.TextLine> lines = new ArrayList<>();
        final String colonyName = tileEntity.getColonyName();
        final int distance = tileEntity.getColonyDistance();
        if (colonyName.isEmpty())
        {
            addText(lines, "Unknown Colony", 0, 0);
            addText(lines, Component.translatable("com.minecolonies.coremod.dist.blocks", distance).getString(), 3, 0);
        }
        else
        {
            final String targetColonyName = tileEntity.getTargetColonyName();
            if (!targetColonyName.isEmpty() && connected)
            {
                addColonyName(lines, colonyName, distance, -10);
                addColonyName(lines, targetColonyName, tileEntity.getTargetColonyDistance(), -60);
            }
            else
            {
                addColonyName(lines, colonyName, distance, -35);
            }
        }
        state.lines = List.copyOf(lines);
    }

    private void addColonyName(final List<ColonySignRenderState.TextLine> lines, final String colonyName, final int distance, final int offset)
    {
        if (this.font.width(colonyName) > 90)
        {
            final List<FormattedText> splitName = this.font.getSplitter().splitLines(colonyName, 90, Style.EMPTY);
            for (int i = 0; i < Math.min(2, splitName.size()); i++)
            {
                addText(lines, splitName.get(i).getString(), i, offset);
            }
        }
        else
        {
            addText(lines, colonyName, 0, offset);
        }
        addText(lines, Component.translatable("com.minecolonies.coremod.dist.blocks", distance).getString(), 3, offset);
    }

    private static void addText(final List<ColonySignRenderState.TextLine> lines, final String original, final int line, final float offset)
    {
        final String text = original.length() > MAX_TEXT_SIZE ? original.substring(0, MAX_TEXT_SIZE) : original;
        lines.add(new ColonySignRenderState.TextLine(FormattedCharSequence.forward(text, Style.EMPTY), line * 8.0F + offset));
    }

    @Override
    public void submit(
      final ColonySignRenderState state,
      final PoseStack matrixStack,
      final SubmitNodeCollector submitNodeCollector,
      final CameraRenderState camera)
    {
        if (!state.render)
        {
            return;
        }

        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.mulPose(Axis.YP.rotationDegrees(state.rotation));
        matrixStack.translate(-0.5, -0.5, -0.5);
        state.model.submit(matrixStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        matrixStack.popPose();

        submitTextSide(state, matrixStack, submitNodeCollector, true);
        submitTextSide(state, matrixStack, submitNodeCollector, false);
    }

    private void submitTextSide(
      final ColonySignRenderState state,
      final PoseStack matrixStack,
      final SubmitNodeCollector submitNodeCollector,
      final boolean mirrored)
    {
        matrixStack.pushPose();
        matrixStack.translate(0.5F, 0.5F, 0.5F);
        matrixStack.mulPose(Axis.YP.rotationDegrees(state.rotation + (mirrored ? 180.0F : 0.0F)));
        matrixStack.translate(0.0F, -0.1F, 0.2F);
        matrixStack.scale(0.007F, -0.007F, 0.007F);
        for (final ColonySignRenderState.TextLine line : state.lines)
        {
            submitNodeCollector.submitText(matrixStack, -this.font.width(line.text()) / 2.0F, line.y(), line.text(), false,
              Font.DisplayMode.NORMAL, state.lightCoords, 0xdcdcdc00, 0, 0);
        }
        matrixStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen()
    {
        return false;
    }

    public static void renderSignHover(final WorldEventContext context)
    {
        final HitResult rayTraceResult = Minecraft.getInstance().hitResult;
        if (!(rayTraceResult instanceof final BlockHitResult blockRayTraceResult) || blockRayTraceResult.getType() == HitResult.Type.MISS)
            return;

        final BlockPos posAtCamera = blockRayTraceResult.getBlockPos();
        if (context.clientLevel.getBlockState(posAtCamera).getBlock() != ModBlocks.blockColonySign)
        {
            return;
        }

        if (context.clientLevel.getBlockEntity(posAtCamera) instanceof TileEntityColonySign tileEntityColonySign)
        {
            if (!BlockPos.ZERO.equals(tileEntityColonySign.getPreviousPos()))
            {
                renderTextBoxAtPos(context, tileEntityColonySign.getPreviousPos(), List.of(Component.translatable(PREVIOUS).getString()));
            }
            if (!BlockPos.ZERO.equals(tileEntityColonySign.getNextPosition()))
            {
                renderTextBoxAtPos(context, tileEntityColonySign.getNextPosition(), List.of(Component.translatable(NEXT).getString()));
            }
        }
    }

    private static void renderTextBoxAtPos(final WorldEventContext context, final BlockPos pos, final List<String> text)
    {
        context.pushPoseCameraToPos(pos);
        context.renderLineBoxWithShadow(BlockPos.ZERO, 0xffffffff, WorldEventContext.DEFAULT_LINE_WIDTH * 2);
        context.popPose();
        renderDebugText(pos, text, context.poseStack, true, 3, 2.5f, context.bufferSource, context.cameraPosition);
    }

    public static void renderDebugText(final BlockPos renderPos,
        final List<String> text,
        final PoseStack matrixStack,
        final boolean forceWhite,
        final int mergeEveryXListElements,
        final float scale,
        final MultiBufferSource buffer, final Vec3 cameraPosition)
    {
        if (mergeEveryXListElements < 1)
        {
            throw new IllegalArgumentException("mergeEveryXListElements is less than 1");
        }

        final int cap = text.size();
        if (cap > 0)
        {
            final Font fontrenderer = Minecraft.getInstance().font;

            matrixStack.pushPose();
            matrixStack.translate(renderPos.getX() + 0.5d - cameraPosition.x(), renderPos.getY() + 0.6d - cameraPosition.y(), renderPos.getZ() + 0.5d - cameraPosition.z());
            matrixStack.mulPose(Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.orientation);
            matrixStack.scale(0.014f, -0.014f, 0.014f);

            final float backgroundTextOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            final int alphaMask = (int) (backgroundTextOpacity * 255.0F) << 24;

            final Matrix4f rawPosMatrix = matrixStack.last().pose();
            rawPosMatrix.scale(scale, scale, scale);

            for (int i = 0; i < cap; i += mergeEveryXListElements)
            {
                final MutableComponent renderText = Component.literal(
                    mergeEveryXListElements == 1 ? text.get(i) : text.subList(i, Math.min(i + mergeEveryXListElements, cap)).toString());
                final float textCenterShift = (float) (-fontrenderer.width(renderText) / 2);

                fontrenderer.drawInBatch(renderText,
                    textCenterShift,
                    0,
                    forceWhite ? 0xffffffff : 0x20ffffff,
                    false,
                    rawPosMatrix,
                    buffer,
                    Font.DisplayMode.SEE_THROUGH,
                    alphaMask,
                    0x00f000f0);
                if (!forceWhite)
                {
                    fontrenderer.drawInBatch(renderText, textCenterShift, 0, 0xffffffff, false, rawPosMatrix, buffer, Font.DisplayMode.NORMAL, 0, 0x00f000f0);
                }
                matrixStack.translate(0.0d, fontrenderer.lineHeight + 1, 0.0d);
            }

            matrixStack.popPose();
        }
    }
}

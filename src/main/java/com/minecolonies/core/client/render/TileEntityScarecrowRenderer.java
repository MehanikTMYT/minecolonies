package com.minecolonies.core.client.render;

import com.minecolonies.api.blocks.huts.AbstractBlockMinecoloniesDefault;
import com.minecolonies.api.tileentities.AbstractTileEntityScarecrow;
import com.minecolonies.api.tileentities.ScareCrowType;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.blocks.BlockScarecrow;
import com.minecolonies.core.client.model.ScarecrowModel;
import com.minecolonies.core.event.ClientRegistryHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

/**
 * Class to render the scarecrow.
 */
public class TileEntityScarecrowRenderer implements BlockEntityRenderer<AbstractTileEntityScarecrow, ScarecrowRenderState>
{
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    /**
     * Offset to the block middle.
     */
    private static final double BLOCK_MIDDLE = 0.5;

    /**
     * Y-Offset in order to have the scarecrow over ground.
     */
    private static final double YOFFSET = 1.5;

    /**
     * Rotate the model some degrees.
     */
    private static final int ROTATION = 180;

    /**
     * Basic rotation to achieve a certain direction.
     */
    private static final int BASIC_ROTATION = 90;

    /**
     * Rotate by amount to go east.
     */
    private static final int ROTATE_EAST = 1;

    /**
     * Rotate by amount to go south.
     */
    private static final int ROTATE_SOUTH = 2;

    /**
     * Rotate by amount to go west.
     */
    private static final int ROTATE_WEST = 3;

    /**
     * The model of the scarecrow.
     */
    @NotNull
    private final ScarecrowModel model;

    private final SpriteGetter sprites;

    private final BlockModelResolver blockModelResolver;

    public static final SpriteId SCARECROW_A = new SpriteId(
      TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block/blockscarecrowpumpkin"));
    public static final SpriteId SCARECROW_B = new SpriteId(
      TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block/blockscarecrownormal"));
    /**
     * The public constructor for the renderer.
     *
     * @param context the render context.
     */
    public TileEntityScarecrowRenderer(final BlockEntityRendererProvider.Context context)
    {
        super();
        this.model = new ScarecrowModel(context.bakeLayer(ClientRegistryHandler.SCARECROW));
        this.sprites = context.sprites();
        this.blockModelResolver = context.blockModelResolver();
    }

    @Override
    public ScarecrowRenderState createRenderState()
    {
        return new ScarecrowRenderState();
    }

    @Override
    public void extractRenderState(
      final AbstractTileEntityScarecrow tileEntity,
      final ScarecrowRenderState state,
      final float partialTicks,
      final Vec3 cameraPosition,
      final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
    {
        BlockEntityRenderer.super.extractRenderState(tileEntity, state, partialTicks, cameraPosition, breakProgress);
        state.render = tileEntity.getBlockState().getValue(BlockScarecrow.HALF) != DoubleBlockHalf.UPPER;
        state.pumpkinHead = tileEntity.getScarecrowType() == ScareCrowType.PUMPKINHEAD;
        state.facing = tileEntity.getBlockState().getBlock() instanceof BlockScarecrow
                         ? tileEntity.getBlockState().getValue(AbstractBlockMinecoloniesDefault.FACING)
                         : Direction.NORTH;

        if (state.render && tileEntity.getBlockState().getValue(BlockScarecrow.LANTERN))
        {
            this.blockModelResolver.update(state.lantern, Blocks.LANTERN.defaultBlockState(), BLOCK_DISPLAY_CONTEXT);
            if (tileEntity.getLevel() != null)
            {
                final int blockLight = tileEntity.getLevel().getBrightness(LightLayer.BLOCK, tileEntity.getBlockPos());
                final int skyLight = tileEntity.getLevel().getBrightness(LightLayer.SKY, tileEntity.getBlockPos());
                state.lightCoords = LightCoordsUtil.pack(blockLight, skyLight);
            }
        }
        else
        {
            state.lantern.clear();
        }
    }

    @Override
    public void submit(
      final ScarecrowRenderState state,
      final PoseStack matrixStack,
      final SubmitNodeCollector submitNodeCollector,
      final CameraRenderState camera)
    {
        if (!state.render)
        {
            return;
        }

        //Store the transformation
        matrixStack.pushPose();
        //Set viewport to tile entity position to render it
        matrixStack.translate(BLOCK_MIDDLE, YOFFSET, BLOCK_MIDDLE);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(ROTATION));

        switch (state.facing)
        {
            case EAST -> matrixStack.mulPose(Axis.YP.rotationDegrees(BASIC_ROTATION * ROTATE_EAST));
            case SOUTH -> matrixStack.mulPose(Axis.YP.rotationDegrees(BASIC_ROTATION * ROTATE_SOUTH));
            case WEST -> matrixStack.mulPose(Axis.YP.rotationDegrees(BASIC_ROTATION * ROTATE_WEST));
            default -> { /* Don't rotate at all. */ }
        }

        submitNodeCollector.submitModel(
          this.model,
          net.minecraft.util.Unit.INSTANCE,
          matrixStack,
          state.lightCoords,
          OverlayTexture.NO_OVERLAY,
          -1,
          getSprite(state),
          this.sprites,
          0,
          state.breakProgress);

        if (!state.lantern.isEmpty())
        {
            submitLantern(matrixStack, submitNodeCollector, state);
        }

        matrixStack.popPose();
    }

    private static void submitLantern(
      final PoseStack matrixStack,
      final SubmitNodeCollector submitNodeCollector,
      final ScarecrowRenderState state)
    {
        matrixStack.pushPose();

        matrixStack.mulPose(Axis.ZP.rotationDegrees(180f));
        matrixStack.translate(0.6f, -0.6f, -0.375f);

        matrixStack.scale(0.75f, 0.65f, 0.75f);

        state.lantern.submit(matrixStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        matrixStack.popPose();
    }

    /**
     * Returns the Material of the scarecrow texture.
     *
     * @param tileEntity the tileEntity of the scarecrow.
     * @return the material.
     */
    @NotNull
    private static SpriteId getSprite(@NotNull final ScarecrowRenderState state)
    {
        return state.pumpkinHead ? SCARECROW_A : SCARECROW_B;
    }
}

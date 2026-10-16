package com.minecolonies.core.client.render;

import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.core.entity.other.NewBobberEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.FishingHookRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Determines how the fish hook is rendered.
 */
public class RenderFishHook extends EntityRenderer<NewBobberEntity, FishingHookRenderState>
{
    /**
     * The resource location containing the particle textures (Spawned by the fishHook).
     */
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/fishing_hook.png");

    /**
     * The render type of the hook.
     */
    private static final RenderType RENDER_TYPE = RenderTypes.entityCutout(TEXTURE);

    /**
     * Required constructor, sets the RenderManager.
     *
     * @param context context that we use.
     */
    public RenderFishHook(final EntityRendererProvider.Context context)
    {
        super(context);
    }

    @NotNull
    @Override
    public FishingHookRenderState createRenderState()
    {
        return new FishingHookRenderState();
    }

    @Override
    public void extractRenderState(@NotNull final NewBobberEntity entity, @NotNull final FishingHookRenderState state, final float partialTicks)
    {
        super.extractRenderState(entity, state, partialTicks);

        if (entity.getOwner() instanceof final AbstractEntityCitizen citizen)
        {
            final float swing = Mth.sin(Mth.sqrt(citizen.getAttackAnim(partialTicks)) * (float) Math.PI);
            state.lineOriginOffset = getCitizenHandPos(citizen, swing, partialTicks).subtract(entity.getPosition(partialTicks).add(0.0D, 0.25D, 0.0D));
        }
        else
        {
            state.lineOriginOffset = Vec3.ZERO;
        }
    }

    @Override
    public void submit(@NotNull final FishingHookRenderState state,
      @NotNull final PoseStack poseStack,
      @NotNull final SubmitNodeCollector submitNodeCollector,
      @NotNull final CameraRenderState camera)
    {
        poseStack.pushPose();
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(camera.orientation);
        submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE, (pose, buffer) -> {
            vertex(buffer, pose, state.lightCoords, 0.0F, 0, 0, 1);
            vertex(buffer, pose, state.lightCoords, 1.0F, 0, 1, 1);
            vertex(buffer, pose, state.lightCoords, 1.0F, 1, 1, 0);
            vertex(buffer, pose, state.lightCoords, 0.0F, 1, 0, 0);
        });
        poseStack.popPose();

        final float xa = (float) state.lineOriginOffset.x;
        final float ya = (float) state.lineOriginOffset.y;
        final float za = (float) state.lineOriginOffset.z;
        final float width = Minecraft.getInstance().gameRenderer.getGameRenderState().windowRenderState.appropriateLineWidth;
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, buffer) -> {
            for (int i = 0; i < 16; i++)
            {
                final float a0 = fraction(i, 16);
                final float a1 = fraction(i + 1, 16);
                stringVertex(xa, ya, za, buffer, pose, a0, a1, width);
                stringVertex(xa, ya, za, buffer, pose, a1, a0, width);
            }
        });
        poseStack.popPose();

        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    /**
     * Work out where the fishing line starts on the citizen holding the rod.
     *
     * @param citizen      the citizen holding the rod.
     * @param swing        the interpolated attack animation.
     * @param partialTicks the partial ticks of this frame.
     * @return the world position the line originates from.
     */
    private static Vec3 getCitizenHandPos(@NotNull final AbstractEntityCitizen citizen, final float swing, final float partialTicks)
    {
        int invert = citizen.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        final ItemStack mainHandItem = citizen.getMainHandItem();
        if (!mainHandItem.is(Items.FISHING_ROD))
        {
            invert = -invert;
        }

        final float bodyRot = Mth.lerp(partialTicks, citizen.yBodyRotO, citizen.yBodyRot) * ((float) Math.PI / 180F);
        final double sin = Mth.sin(bodyRot);
        final double cos = Mth.cos(bodyRot);
        final double rightOffset = invert * 0.35D;
        final double forwardOffset = 0.8D;
        final float yOffset = citizen.isCrouching() ? -0.1875F : 0.0F;

        return citizen.getEyePosition(partialTicks)
                 .add(-cos * rightOffset - sin * forwardOffset, yOffset - 0.45D, -sin * rightOffset + cos * forwardOffset);
    }

    private static float fraction(final int first, final int second)
    {
        return (float) first / (float) second;
    }

    private static void vertex(final VertexConsumer consumer, final PoseStack.Pose pose, final int lightCoords, final float x, final int y, final int u, final int v)
    {
        consumer.addVertex(pose, x - 0.5F, (float) y - 0.5F, 0.0F)
          .setColor(-1)
          .setUv((float) u, (float) v)
          .setOverlay(OverlayTexture.NO_OVERLAY)
          .setLight(lightCoords)
          .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private static void stringVertex(final float xa, final float ya, final float za,
      final VertexConsumer stringBuffer, final PoseStack.Pose stringPose,
      final float aa, final float nexta, final float width)
    {
        final float x = xa * aa;
        final float y = ya * (aa * aa + aa) * 0.5F + 0.25F;
        final float z = za * aa;
        float nx = xa * nexta - x;
        float ny = ya * (nexta * nexta + nexta) * 0.5F + 0.25F - y;
        float nz = za * nexta - z;
        final float length = Mth.sqrt(nx * nx + ny * ny + nz * nz);
        nx /= length;
        ny /= length;
        nz /= length;
        stringBuffer.addVertex(stringPose, x, y, z).setColor(-16777216).setNormal(stringPose, nx, ny, nz).setLineWidth(width);
    }
}

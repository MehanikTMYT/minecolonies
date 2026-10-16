package com.minecolonies.core.client.render.mobs.pirates;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import com.minecolonies.core.client.render.RenderUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import com.minecolonies.core.client.render.mobs.RaiderRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract for rendering Pirates.
 */
public abstract class AbstractRendererPirate<T extends AbstractEntityMinecoloniesMonster, M extends HumanoidModel<RaiderRenderState>> extends HumanoidMobRenderer<T, RaiderRenderState, M>
{
    public AbstractRendererPirate(final EntityRendererProvider.Context context, final M modelBipedIn, final float shadowSize)
    {
        super(context, modelBipedIn, shadowSize);
        this.addLayer(new HumanoidArmorLayer<>(this,
          ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), HumanoidModel::new),
          context.getEquipmentRenderer()));
    }

    @NotNull
    @Override
    public RaiderRenderState createRenderState()
    {
        return new RaiderRenderState();
    }

    @Override
    public void extractRenderState(@NotNull final T raider, @NotNull final RaiderRenderState state, final float partialTicks)
    {
        super.extractRenderState(raider, state, partialTicks);
        state.textureId = raider.getTextureId();
    }

    /**
     * The arm poses are pulled into the render state now; this hook is what feeds
     * {@code state.leftArmPose} and {@code state.rightArmPose}.
     */
    @NotNull
    @Override
    protected HumanoidModel.ArmPose getArmPose(@NotNull final T raider, @NotNull final HumanoidArm arm)
    {
        return RenderUtils.getArmPose(raider, arm == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }
}
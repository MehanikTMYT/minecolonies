package com.minecolonies.core.client.render.mobs;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.model.MercenaryModel;
import com.minecolonies.core.event.ClientRegistryHandler;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;

/**
 * Renderer for EntityMercenary.
 */
public class RenderMercenary extends HumanoidMobRenderer<PathfinderMob, HumanoidRenderState, MercenaryModel>
{
    /**
     * Texture of the entity.
     */
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/citizen/default/settlermale1_b.png");

    /**
     * Renders the mercenary mobs, with an held item and armorset.
     * <p>
     * The held item, worn head and wings layers are contributed by {@link HumanoidMobRenderer} itself since 26.1,
     * so only the armour layer has to be added here.
     *
     * @param context RenderManager
     */
    public RenderMercenary(final EntityRendererProvider.Context context)
    {
        super(context, new MercenaryModel(context.bakeLayer(ClientRegistryHandler.MERCENARY)), 0.5f);

        this.addLayer(new HumanoidArmorLayer<>(this,
          ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), HumanoidModel::new),
          context.getEquipmentRenderer()));
    }

    @NotNull
    @Override
    public HumanoidRenderState createRenderState()
    {
        return new HumanoidRenderState();
    }

    @NotNull
    @Override
    public Identifier getTextureLocation(@NotNull final HumanoidRenderState state)
    {
        return TEXTURE;
    }
}

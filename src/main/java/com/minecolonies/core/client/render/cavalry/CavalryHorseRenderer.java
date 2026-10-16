package com.minecolonies.core.client.render.cavalry;

import com.minecolonies.core.client.render.CavalryOverlayLayer;
import com.minecolonies.core.entity.other.cavalry.CavalryHorseEntity;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

/**
 * Renderer of the cavalry horses.
 * <p>
 * The vanilla {@code HorseRenderer} is final since 26.1, so the cavalry horse gets its own renderer instead of a
 * vanilla one with an extra layer attached. That is also what lets the readiness overlay read its value off the state.
 */
public class CavalryHorseRenderer extends AbstractHorseRenderer<CavalryHorseEntity, CavalryHorseRenderState, HorseModel>
{
    /**
     * Base horse texture the cavalry horses are drawn with.
     */
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/horse/horse_white.png");

    /**
     * Number of readiness segments the overlay distinguishes.
     */
    public static final int READINESS_SEGMENTS = 5;

    public CavalryHorseRenderer(final EntityRendererProvider.Context context)
    {
        super(context, new HorseModel(context.bakeLayer(ModelLayers.HORSE)), new HorseModel(context.bakeLayer(ModelLayers.HORSE_BABY)));
        this.addLayer(new CavalryOverlayLayer(this));
    }

    @NotNull
    @Override
    public CavalryHorseRenderState createRenderState()
    {
        return new CavalryHorseRenderState();
    }

    @Override
    public void extractRenderState(@NotNull final CavalryHorseEntity horse, @NotNull final CavalryHorseRenderState state, final float partialTicks)
    {
        super.extractRenderState(horse, state, partialTicks);
        state.bodyArmorItem = horse.getBodyArmorItem().copy();

        final float threshold = horse.getMaxHealth() * CavalryHorseEntity.COMBAT_READINESS_THRESHOLD;
        final float cooldown = Math.max(0f, horse.getAnimalDataView() == null ? 0 : horse.getAnimalDataView().getCombatCooldown());
        final float readiness = Mth.clamp(1.0f - (cooldown / Math.max(0.001f, threshold)), 0f, 1f);

        state.combatReadinessSegments = Mth.clamp((int) Math.floor(readiness * READINESS_SEGMENTS + 0.0001f), 0, READINESS_SEGMENTS);
    }

    @NotNull
    @Override
    public Identifier getTextureLocation(@NotNull final CavalryHorseRenderState state)
    {
        return TEXTURE;
    }
}

package com.minecolonies.core.client.model;

import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.render.worldevent.ColonyBlueprintRenderer;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Item model condition that is true while the build goggles are not showing any blueprint preview.
 * <p>
 * This replaces the former {@code minecraft:disabled} item property override of the build goggles; since 26.1 item
 * models select their variant through registered model properties instead of numeric property overrides.
 */
public record BlueprintRenderDisabled() implements ConditionalItemModelProperty
{
    /**
     * The id this property is registered under.
     */
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "blueprint_render_disabled");

    /**
     * The codec of this property; it carries no data.
     */
    public static final MapCodec<BlueprintRenderDisabled> MAP_CODEC = MapCodec.unit(new BlueprintRenderDisabled());

    @Override
    public boolean get(final ItemStack itemStack,
      @Nullable final ClientLevel level,
      @Nullable final LivingEntity owner,
      final int seed,
      final ItemDisplayContext displayContext)
    {
        return !ColonyBlueprintRenderer.willRenderBlueprints();
    }

    @Override
    public MapCodec<BlueprintRenderDisabled> type()
    {
        return MAP_CODEC;
    }
}

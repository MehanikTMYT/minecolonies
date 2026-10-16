package com.minecolonies.core.client.render;

import com.minecolonies.api.client.render.modeltype.CitizenModel;
import com.minecolonies.api.client.render.modeltype.CitizenRenderState;
import com.minecolonies.api.util.Log;
import com.minecolonies.core.event.ClientEventHandler;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.SkullBlock;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Armour layer of the citizens.
 * <p>
 * It differs from the vanilla layer in two ways: a citizen can have a display-only armour set that overrides what it
 * actually wears, and a citizen with a custom player skin renders that skin's head instead of the model head.
 * <p>
 * The per-slot drawing is delegated to {@link EquipmentLayerRenderer#renderLayers}, exactly like the vanilla layer does,
 * so dyes, armour trims, the glint and the inner (leggings) versus outer layer split all keep working unchanged.
 */
public class CitizenArmorLayer extends HumanoidArmorLayer<CitizenRenderState, CitizenModel, HumanoidModel<CitizenRenderState>>
{
    private final SkullModelBase               playerSkullModel;
    private final Map<UUID, ResolvableProfile> gameProfileMap = new HashMap<>();

    private final ArmorModelSet<HumanoidModel<CitizenRenderState>> armorModels;
    private final EquipmentLayerRenderer                           equipmentRenderer;
    private final PlayerSkinRenderCache                            playerSkinRenderCache;

    /**
     * Set of items excluded from rendering as they're causing errors.
     */
    private static final Set<Item> disabledFromRendering = new HashSet<>();

    public CitizenArmorLayer(final RenderLayerParent<CitizenRenderState, CitizenModel> parentLayer,
      final ArmorModelSet<HumanoidModel<CitizenRenderState>> armorModels,
      final EquipmentLayerRenderer equipmentRenderer,
      final EntityModelSet modelSet,
      final PlayerSkinRenderCache playerSkinRenderCache)
    {
        super(parentLayer, armorModels, equipmentRenderer);
        this.armorModels = armorModels;
        this.equipmentRenderer = equipmentRenderer;
        this.playerSkinRenderCache = playerSkinRenderCache;
        this.playerSkullModel = SkullBlockRenderer.createModel(modelSet, SkullBlock.Types.PLAYER);
    }

    @Override
    public void submit(@NotNull final PoseStack poseStack,
      @NotNull final SubmitNodeCollector submitNodeCollector,
      final int lightCoords,
      @NotNull final CitizenRenderState state,
      final float yRot,
      final float xRot)
    {
        if (!state.renderArmor || state.isInvisible)
        {
            return;
        }

        if (state.customTextureUUID != null && this.playerSkullModel != null)
        {
            submitCustomSkinHead(poseStack, submitNodeCollector, lightCoords, state, yRot, xRot);
        }

        // Same slot order as the vanilla layer, so overlapping pieces keep their draw order.
        submitArmorPiece(poseStack, submitNodeCollector, lightCoords, state, state.chestEquipment, EquipmentSlot.CHEST);
        submitArmorPiece(poseStack, submitNodeCollector, lightCoords, state, state.legsEquipment, EquipmentSlot.LEGS);
        submitArmorPiece(poseStack, submitNodeCollector, lightCoords, state, state.feetEquipment, EquipmentSlot.FEET);
        submitArmorPiece(poseStack, submitNodeCollector, lightCoords, state, state.headEquipment, EquipmentSlot.HEAD);
    }

    /**
     * Draw one armour piece, guarding against broken modded armour the same way the old layer did.
     */
    private void submitArmorPiece(final PoseStack poseStack,
      final SubmitNodeCollector submitNodeCollector,
      final int lightCoords,
      final CitizenRenderState state,
      final ItemStack itemStack,
      final EquipmentSlot slot)
    {
        if (itemStack.isEmpty() || disabledFromRendering.contains(itemStack.getItem()))
        {
            return;
        }

        final Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.slot() != slot || equippable.assetId().isEmpty())
        {
            return;
        }

        try
        {
            final EquipmentClientInfo.LayerType layerType = slot == EquipmentSlot.LEGS
                                                             ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS
                                                             : EquipmentClientInfo.LayerType.HUMANOID;

            this.equipmentRenderer.renderLayers(layerType,
              equippable.assetId().get(),
              this.armorModels.get(slot),
              state,
              itemStack,
              poseStack,
              submitNodeCollector,
              lightCoords,
              state.outlineColor);
        }
        catch (final Exception e)
        {
            Log.getLogger().warn("Error rendering armor: " + itemStack + " report to the armor's mod.", e);
            disabledFromRendering.add(itemStack.getItem());
            ClientEventHandler.extraItemTooltips.put(itemStack.getItem(),
              Component.literal("This armor is causing errors when rendering on citizens, check your latest.log and report to the respective armor mod.")
                .withStyle(ChatFormatting.RED));
        }
    }

    /**
     * Render the head of the player skin a citizen was given, in place of the model head.
     */
    private void submitCustomSkinHead(final PoseStack poseStack,
      final SubmitNodeCollector submitNodeCollector,
      final int lightCoords,
      final CitizenRenderState state,
      final float yRot,
      final float xRot)
    {
        final UUID textureUUID = state.customTextureUUID;
        final ResolvableProfile gameProfile = gameProfileMap.get(textureUUID);
        if (gameProfile == null)
        {
            // Request the profile once and draw nothing until it arrives.
            gameProfileMap.put(textureUUID, ResolvableProfile.createUnresolved(textureUUID));
            Util.backgroundExecutor().execute(() ->
            {
                final Minecraft minecraft = Minecraft.getInstance();
                final ProfileResult profile = minecraft.services().sessionService().fetchProfile(textureUUID, true);
                if (profile != null)
                {
                    minecraft.submit(() -> gameProfileMap.put(textureUUID, ResolvableProfile.createResolved(profile.profile())));
                }
            });
            return;
        }

        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.rotateAround(Axis.YP.rotationDegrees(180), 0.0f, 0.0f, 0.0f);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        final RenderType renderType = this.playerSkinRenderCache.getOrDefault(gameProfile).renderType();

        // The head has to follow the citizen's head rotation, so the model state is built here instead of using
        // SkullBlockRenderer#submitSkull, which only carries the animation position.
        final SkullModelBase.State modelState = new SkullModelBase.State();
        modelState.animationPos = 0f;
        modelState.yRot = yRot;
        modelState.xRot = xRot;
        submitNodeCollector.submitModel(this.playerSkullModel, modelState, poseStack, renderType,
          lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);

        poseStack.popPose();
    }
}

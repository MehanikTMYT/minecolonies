package com.minecolonies.core.client.render;

import com.minecolonies.api.client.render.modeltype.CitizenModel;
import com.minecolonies.api.client.render.modeltype.CitizenRenderState;
import com.minecolonies.api.client.render.modeltype.IModelType;
import com.minecolonies.api.client.render.modeltype.ModModelTypes;
import com.minecolonies.api.client.render.modeltype.registry.IModelTypeRegistry;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.apiimp.initializer.ModModelTypeInitializer;
import com.minecolonies.core.client.render.worldevent.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

/**
 * Renderer for the citizens.
 */
public class RenderBipedCitizen extends HumanoidMobRenderer<AbstractEntityCitizen, CitizenRenderState, CitizenModel>
{
    private static final double  SHADOW_SIZE   = 0.5F;

    /**
     * Alpha the citizens are drawn with while they are rendered as ghosts.
     */
    private static final int GHOST_ALPHA = 77;

    /**
     * Squared distance up to which the status icon above the name tag is still drawn.
     */
    private static final double STATUS_ICON_RENDER_DISTANCE_SQ = 4096.0D;

    public static boolean isItGhostTime = false;

    /**
     * Renders model, see {@link HumanoidMobRenderer}.
     *
     * @param context the context for this Renderer.
     */
    public RenderBipedCitizen(final EntityRendererProvider.Context context)
    {
        super(context, new CitizenModel(context.bakeLayer(ModelLayers.PLAYER)), (float) SHADOW_SIZE);
        this.addLayer(new CitizenArmorLayer(this,
          ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), HumanoidModel::new),
          context.getEquipmentRenderer(),
          context.getModelSet(),
          context.getPlayerSkinRenderCache()));
        ModModelTypeInitializer.init(context);
    }

    @NotNull
    @Override
    public CitizenRenderState createRenderState()
    {
        return new CitizenRenderState();
    }

    @Override
    public void extractRenderState(@NotNull final AbstractEntityCitizen citizen, @NotNull final CitizenRenderState state, final float partialTicks)
    {
        final ICitizenDataView dataView = citizen.getCitizenDataView();

        // Only the identity of the model is recorded here; the model itself is resolved in submit, because every
        // visible entity is extracted before anything is submitted.
        state.modelTypeId = dataView != null && dataView.getCustomTexture() != null ? ModModelTypes.CUSTOM_ID : citizen.getModelType();
        state.female = citizen.isFemale();

        // The humanoid extraction animates through the renderer model, so it has to be the citizen's own model.
        this.model = selectModel(state);

        super.extractRenderState(citizen, state, partialTicks);

        final Identifier customTexture = dataView == null ? null : dataView.getCustomTexture();

        state.texture = customTexture != null ? customTexture : citizen.getTexture();
        state.hasCustomSkin = dataView != null && dataView.getCustomTextureUUID() != null;
        state.renderMetadata = citizen.getRenderMetadata();
        state.civilianId = citizen.getCivilianID();
        state.isGhost = isItGhostTime;
        state.displayHat = citizen.getPose() != Pose.SLEEPING
                             && citizen.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                             && (dataView == null || (dataView.getDisplayArmor(EquipmentSlot.HEAD).isEmpty() && dataView.getCustomTextureUUID() == null));
        state.statusIcon = dataView != null && dataView.hasVisibleStatus() ? dataView.getStatusIcon() : null;
        state.mainHandEmpty = citizen.getMainHandItem().isEmpty();
        state.hasChestItem = citizen.hasItemInSlot(EquipmentSlot.CHEST);
        state.customTextureUUID = dataView == null ? null : dataView.getCustomTextureUUID();
        state.renderArmor = dataView != null && dataView.getInventory() != null;

        // A citizen can be shown wearing a display-only armour set that overrides what it actually carries.
        if (dataView != null)
        {
            state.headEquipment = displayOrWorn(dataView, state.headEquipment, EquipmentSlot.HEAD);
            state.chestEquipment = displayOrWorn(dataView, state.chestEquipment, EquipmentSlot.CHEST);
            state.legsEquipment = displayOrWorn(dataView, state.legsEquipment, EquipmentSlot.LEGS);
            state.feetEquipment = displayOrWorn(dataView, state.feetEquipment, EquipmentSlot.FEET);
        }
    }

    /**
     * Pick the display armour of a slot when the citizen has one, otherwise keep what it actually wears.
     *
     * @param dataView the citizen data view.
     * @param worn     the piece the citizen actually wears.
     * @param slot     the slot to resolve.
     * @return the stack to render for that slot.
     */
    private static ItemStack displayOrWorn(@NotNull final ICitizenDataView dataView, @NotNull final ItemStack worn, @NotNull final EquipmentSlot slot)
    {
        final ItemStack display = dataView.getDisplayArmor(slot);
        // The data view keeps its stacks alive and mutable; submit happens later, so the state needs its own copy.
        return display.isEmpty() ? worn : display.copy();
    }

    /**
     * Pick the model matching the citizen's job, sex and custom skin, from the render state alone.
     *
     * @param state the render state to pick a model for.
     * @return the model to draw this citizen with.
     */
    private CitizenModel selectModel(@NotNull final CitizenRenderState state)
    {
        final IModelType modelType = IModelTypeRegistry.getInstance().getModelType(state.modelTypeId);
        if (ModModelTypes.CUSTOM_ID.equals(state.modelTypeId))
        {
            return modelType.getMaleModel();
        }

        return state.female ? modelType.getFemaleModel() : modelType.getMaleModel();
    }

    /**
     * The arm poses are part of the render state now; this hook is what fills them.
     */
    @NotNull
    @Override
    protected HumanoidModel.ArmPose getArmPose(@NotNull final AbstractEntityCitizen citizen, @NotNull final HumanoidArm arm)
    {
        return RenderUtils.getArmPose(citizen, arm == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    @Override
    protected int getModelTint(@NotNull final CitizenRenderState state)
    {
        // Ghost citizens used to be drawn by forcing the shader colour to 30% alpha, which no longer exists.
        return state.isGhost ? ARGB.color(GHOST_ALPHA, 255, 255, 255) : super.getModelTint(state);
    }

    @Nullable
    @Override
    protected RenderType getRenderType(@NotNull final CitizenRenderState state,
      final boolean isBodyVisible,
      final boolean forceTransparent,
      final boolean appearGlowing)
    {
        if (state.isGhost && isBodyVisible && !forceTransparent)
        {
            // The tint above only takes effect on a translucent render type.
            return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(getTextureLocation(state));
        }
        return super.getRenderType(state, isBodyVisible, forceTransparent, appearGlowing);
    }

    @Override
    public void submit(@NotNull final CitizenRenderState state,
      @NotNull final PoseStack poseStack,
      @NotNull final SubmitNodeCollector submitNodeCollector,
      @NotNull final CameraRenderState camera)
    {
        // Re-select the model for THIS state: the renderer field may have been overwritten by another citizen's
        // extraction, and the submit nodes capture whichever model is set while this call runs.
        this.model = selectModel(state);

        super.submit(state, poseStack, submitNodeCollector, camera);

        if (state.statusIcon != null && state.nameTagAttachment != null && state.distanceToCameraSq <= STATUS_ICON_RENDER_DISTANCE_SQ)
        {
            poseStack.pushPose();
            poseStack.translate(state.nameTagAttachment.x, state.nameTagAttachment.y + 0.9D, state.nameTagAttachment.z);
            poseStack.mulPose(camera.orientation);
            poseStack.scale(0.025F, -0.025F, 0.025F);

            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.worldEntityIcon(state.statusIcon), (pose, buffer) -> {
                final Matrix4f matrix = pose.pose();
                buffer.addVertex(matrix, -5, 0, 0).setUv(0, 0);
                buffer.addVertex(matrix, -5, 10, 0).setUv(0, 1);
                buffer.addVertex(matrix, 5, 10, 0).setUv(1, 1);
                buffer.addVertex(matrix, 5, 0, 0).setUv(1, 0);
            });

            poseStack.popPose();
        }
    }

    @NotNull
    @Override
    public Identifier getTextureLocation(@NotNull final CitizenRenderState state)
    {
        return state.texture;
    }
}

package com.minecolonies.api.client.render.modeltype;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Citizen model.
 */
public class CitizenModel extends HumanoidModel<CitizenRenderState>
{
    /**
     * Working render meta.
     */
    private static final String RENDER_META_WORKING = "working";

    public static boolean isItApril1st = false;

    public CitizenModel(final ModelPart part)
    {
        super(part, RenderTypes::entityCutout);
    }

    @Override
    public void setupAnim(@NotNull final CitizenRenderState state)
    {
        super.setupAnim(state);
        if (body.xRot == 0)
        {
            body.xRot = getActualRotation(state);
        }

        if (head.xRot == 0)
        {
            head.xRot = getActualRotation(state);
        }

        if (state.hasCustomSkin)
        {
            head.visible = false;
            hat.visible = false;
        }
        else
        {
            head.visible = true;
            hat.visible = true;
        }

        if (isItApril1st)
        {
            switch (state.civilianId % 7)
            {
                case 0:
                    leftArm.visible = false;
                    break;
                case 1:
                    rightArm.visible = false;
                    break;
                case 2:
                    body.visible = false;
                    break;
                case 3:
                    head.visible = false;
                    break;
                case 4:
                    hat.visible = false;
                    break;
                case 5:
                    leftLeg.visible = false;
                    break;
                case 6:
                    rightLeg.visible = false;
                    break;
            }
        }
    }

    public static LayerDefinition createMesh()
    {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    /**
     * Override to change body rotation.
     *
     * @param state the citizen render state.
     * @return the rotation.
     */
    public float getActualRotation(@NotNull final CitizenRenderState state)
    {
        return 0;
    }

    /**
     * Check if the citizen is supposed to be working.
     *
     * @param state the citizen render state to check.
     * @return true if so.
     */
    public boolean isWorking(@NotNull final CitizenRenderState state)
    {
        return state.renderMetadata.contains(RENDER_META_WORKING);
    }

    /**
     * Check if the hat should be displayed.
     *
     * @param state the citizen render state to check.
     * @return true if so.
     */
    public boolean displayHat(@NotNull final CitizenRenderState state)
    {
        return state.displayHat;
    }
}

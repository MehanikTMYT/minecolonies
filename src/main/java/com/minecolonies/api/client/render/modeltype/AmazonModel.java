package com.minecolonies.api.client.render.modeltype;

import com.minecolonies.core.client.render.mobs.RaiderRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

/**
 * Amazon model.
 */
public class AmazonModel extends HumanoidModel<RaiderRenderState>
{
    public AmazonModel(final ModelPart part)
    {
        super(part);
    }

    @Override
    public void setupAnim(@NotNull final RaiderRenderState state)
    {
        super.setupAnim(state);
        head.y -= 3;
        rightLeg.y -= 3.5;
        leftLeg.y -= 3.5;
        rightArm.y -= 2;
        leftArm.y -= 2;
    }
}

package com.minecolonies.core.compatibility.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link EmiRecipeCategory} whose display name reuses an existing MineColonies translation
 * (e.g. a job's own name, already translated) instead of requiring a brand new
 * {@code emi.category.<id>} key, so the same profession terminology is used everywhere
 * (job screens, tooltips, and the EMI recipe browser alike).
 */
public class MineColoniesEmiRecipeCategory extends EmiRecipeCategory
{
    @NotNull
    private final Component name;

    public MineColoniesEmiRecipeCategory(@NotNull final Identifier id, @NotNull final EmiRenderable icon, @NotNull final Component name)
    {
        super(id, icon);
        this.name = name;
    }

    @Override
    @NotNull
    public Component getName()
    {
        return this.name;
    }
}

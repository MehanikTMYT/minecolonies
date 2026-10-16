package com.minecolonies.core.compatibility.emi;

import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.colony.crafting.ToolUsage;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.minecolonies.api.util.constant.Constants.MAX_BUILDING_LEVEL;

/**
 * Which tools a worker may use at each building level (plain and enchanted rows). Replaces the JEI ToolRecipeCategory.
 */
public class MineColoniesToolEmiRecipe extends BasicEmiRecipe
{
    private static final int WIDTH  = 162;
    private static final int SLOT_X = WIDTH - 2 - ((MAX_BUILDING_LEVEL + 1) * 18);

    private final ToolUsage tool;

    public MineColoniesToolEmiRecipe(@NotNull final EmiRecipeCategory category, @NotNull final Identifier id, @NotNull final ToolUsage tool)
    {
        super(category, id, WIDTH, 50);
        this.tool = tool;
        for (int i = 0; i <= MAX_BUILDING_LEVEL; ++i)
        {
            this.inputs.add(of(tool.toolLevels().get(i)));
            this.inputs.add(of(tool.enchantedToolLevels().get(i)));
        }
    }

    private static EmiIngredient of(@NotNull final List<ItemStack> stacks)
    {
        return EmiIngredient.of(stacks.stream().filter(s -> !s.isEmpty()).map(EmiStack::of).toList());
    }

    @Override
    public void addWidgets(@NotNull final WidgetHolder widgets)
    {
        widgets.addText(tool.tool().getDisplayName(), 2, 22, 0xFF404040, false);
        int x = SLOT_X;
        for (int i = 0; i <= MAX_BUILDING_LEVEL; ++i)
        {
            widgets.addText(Component.translatableEscape(TranslationConstants.PARTIAL_JEI_INFO + "onelevelrestriction", i), x + 2, 2, 0xFF404040, false);
            widgets.addSlot(of(tool.toolLevels().get(i)), x, 12);
            widgets.addSlot(of(tool.enchantedToolLevels().get(i)), x, 30);
            x += 18;
        }
    }
}

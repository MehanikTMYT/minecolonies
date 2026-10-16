package com.minecolonies.core.compatibility.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic EMI display for MineColonies "what can this produce" pages (crops, fishing, flowers): input/source slots on the
 * left, a catalyst (required tool / item) on the right, and a grid of possible outputs with per-slot tooltips such as drop
 * chances. Replaces the JEI CropRecipeCategory / FishermanRecipeCategory / FloristRecipeCategory layouts.
 */
public class MineColoniesLootEmiRecipe extends BasicEmiRecipe
{
    private static final int WIDTH   = 162;
    private static final int COLUMNS = 9;

    /** One output slot: what can come out, plus extra tooltip lines (chance, conditions...). */
    public record Output(@NotNull EmiIngredient stack, @NotNull List<Component> tooltip) {}

    private final List<EmiIngredient> sources;
    private final List<EmiIngredient> catalystSlots;
    private final List<Output>        outputSlots;
    private final Component           label;

    public MineColoniesLootEmiRecipe(@NotNull final EmiRecipeCategory category,
                                     @NotNull final Identifier id,
                                     @NotNull final List<EmiIngredient> sources,
                                     @NotNull final List<EmiIngredient> catalystSlots,
                                     @NotNull final List<Output> outputSlots,
                                     final Component label)
    {
        super(category, id, WIDTH, 22 + Math.max(1, (outputSlots.size() + COLUMNS - 1) / COLUMNS) * 18);
        this.sources = sources;
        this.catalystSlots = catalystSlots;
        this.outputSlots = outputSlots;
        this.label = label;
        this.inputs.addAll(sources);
        this.catalysts.addAll(catalystSlots);
        for (final Output output : outputSlots)
        {
            this.outputs.addAll(output.stack().getEmiStacks());
        }
    }

    @Override
    public void addWidgets(@NotNull final WidgetHolder widgets)
    {
        int x = 0;
        for (final EmiIngredient source : sources)
        {
            widgets.addSlot(source, x, 0);
            x += 18;
        }
        if (label != null)
        {
            widgets.addText(label, x + 2, 5, 0xFF404040, false);
        }
        int cx = WIDTH - 18;
        for (final EmiIngredient catalyst : catalystSlots)
        {
            widgets.addSlot(catalyst, cx, 0).catalyst(true);
            cx -= 18;
        }

        final int rows = Math.max(1, (outputSlots.size() + COLUMNS - 1) / COLUMNS);
        final int columns = Math.min(COLUMNS, Math.max(1, (outputSlots.size() + rows - 1) / rows));
        final int startX = (WIDTH - columns * 18) / 2;
        int index = 0;
        for (final Output output : outputSlots)
        {
            final SlotWidget slot = widgets.addSlot(output.stack(), startX + (index % columns) * 18, 22 + (index / columns) * 18)
                                      .recipeContext(this);
            for (final Component line : output.tooltip())
            {
                slot.appendTooltip(line);
            }
            index++;
        }
    }

    @NotNull
    public static List<EmiIngredient> stacks(@NotNull final List<net.minecraft.world.item.ItemStack> stacks)
    {
        final List<EmiStack> list = new ArrayList<>();
        for (final net.minecraft.world.item.ItemStack stack : stacks)
        {
            if (!stack.isEmpty())
            {
                list.add(EmiStack.of(stack));
            }
        }
        return list.isEmpty() ? List.of() : List.of(EmiIngredient.of(list));
    }
}

package com.minecolonies.core.compatibility.emi;

import com.minecolonies.api.crafting.IGenericRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * EMI recipe wrapper for a MineColonies worker/profession recipe (or any other simple ingredients-to-outputs
 * display, e.g. composting).  Lays inputs out in a row on the left and outputs in a row on the right, exactly
 * matching what {@link dev.emi.emi.api.EmiApi}'s "Recipes"/"Uses" lookups need: input slots for the ingredient
 * roles and output slots for the result roles.
 */
public class MineColoniesEmiRecipe extends BasicEmiRecipe
{
    private IGenericRecipe sourceRecipe;

    /** Typed source for addon teaching handlers; null for displays constructed without a generic recipe. */
    public IGenericRecipe getSourceRecipe()
    {
        return sourceRecipe;
    }

    private static final int SLOT_SIZE  = 18;
    private static final int SLOT_GAP   = 2;
    private static final int ARROW_GAP  = 22;
    private static final int MAX_PER_ROW = 4;

    public MineColoniesEmiRecipe(@NotNull final EmiRecipeCategory category,
                                  @NotNull final Identifier id,
                                  @NotNull final List<EmiIngredient> inputs,
                                  @NotNull final List<EmiStack> outputs)
    {
        super(category, id, computeWidth(inputs, outputs), computeHeight(inputs, outputs));
        this.inputs = inputs;
        this.outputs = outputs;
    }

    private static int computeWidth(final List<EmiIngredient> inputs, final List<EmiStack> outputs)
    {
        final int inCols = Math.min(MAX_PER_ROW, Math.max(1, inputs.size()));
        final int outCols = Math.min(MAX_PER_ROW, Math.max(1, outputs.size()));
        return inCols * (SLOT_SIZE + SLOT_GAP) + ARROW_GAP + outCols * (SLOT_SIZE + SLOT_GAP);
    }

    private static int computeHeight(final List<EmiIngredient> inputs, final List<EmiStack> outputs)
    {
        final int inRows = Math.max(1, (inputs.size() + MAX_PER_ROW - 1) / MAX_PER_ROW);
        final int outRows = Math.max(1, (outputs.size() + MAX_PER_ROW - 1) / MAX_PER_ROW);
        return Math.max(inRows, outRows) * (SLOT_SIZE + SLOT_GAP);
    }

    @Override
    public void addWidgets(@NotNull final WidgetHolder widgets)
    {
        for (int i = 0; i < inputs.size(); i++)
        {
            final int col = i % MAX_PER_ROW;
            final int row = i / MAX_PER_ROW;
            widgets.addSlot(inputs.get(i), col * (SLOT_SIZE + SLOT_GAP), row * (SLOT_SIZE + SLOT_GAP)).drawBack(true);
        }

        final int outX = Math.min(MAX_PER_ROW, Math.max(1, inputs.size())) * (SLOT_SIZE + SLOT_GAP) + ARROW_GAP;
        for (int i = 0; i < outputs.size(); i++)
        {
            final int col = i % MAX_PER_ROW;
            final int row = i / MAX_PER_ROW;
            widgets.addSlot(outputs.get(i), outX + col * (SLOT_SIZE + SLOT_GAP), row * (SLOT_SIZE + SLOT_GAP))
              .drawBack(true)
              .large(i == 0);
        }
    }

    /**
     * Converts a MineColonies {@link IGenericRecipe} (the same normalized recipe DTO the old JEI integration used,
     * built by {@link com.minecolonies.core.colony.crafting.RecipeAnalyzer}) into an EMI recipe.
     *
     * @param category the EMI category this recipe belongs to.
     * @param recipe   the source recipe.
     * @return the EMI recipe.
     */
    @NotNull
    public static MineColoniesEmiRecipe fromGeneric(@NotNull final EmiRecipeCategory category, @NotNull final IGenericRecipe recipe)
    {
        final List<EmiIngredient> inputs = new ArrayList<>();
        for (final List<ItemStack> alternatives : recipe.getInputs())
        {
            if (alternatives.isEmpty())
            {
                continue;
            }
            inputs.add(toIngredient(alternatives));
        }

        for (final var fluid : recipe.getFluidInputs())
        {
            if (!fluid.isEmpty())
            {
                inputs.add(EmiStack.of(fluid.getFluid(), fluid.getComponentsPatch(), fluid.getAmount()));
            }
        }

        final List<EmiStack> outputs = new ArrayList<>();
        for (final ItemStack stack : recipe.getAllMultiOutputs())
        {
            if (!stack.isEmpty())
            {
                outputs.add(EmiStack.of(stack));
            }
        }
        if (outputs.isEmpty() && !recipe.getPrimaryOutput().isEmpty())
        {
            outputs.add(EmiStack.of(recipe.getPrimaryOutput()));
        }

        final Identifier id = Identifier.fromNamespaceAndPath(category.getId().getNamespace(),
          category.getId().getPath() + "/" + deterministicPath(recipe));

        final var result = new MineColoniesEmiRecipe(category, id, inputs, outputs);
        result.sourceRecipe = recipe;
        return result;
    }

    /**
     * Builds a deterministic identifier path segment for a recipe: the datapack/custom-recipe id's own
     * path when present, otherwise a stable hash of the recipe's actual content (output + input item
     * registry names). {@link IGenericRecipe} does not override {@link Object#hashCode()} itself, and
     * {@link IGenericRecipe#getRecipeId()} is {@code null} for taught/dynamic recipes (a common case),
     * so neither {@code recipe.hashCode()} nor object identity may be used here - both would mint a
     * different id every reload for the exact same recipe, which then shows up as a "different" recipe
     * to EMI (breaking pinned/favourited entries and its own duplicate-id detection).
     *
     * @param recipe the source recipe.
     * @return a path segment that is identical across reloads for the same underlying recipe.
     */
    @NotNull
    private static String deterministicPath(@NotNull final IGenericRecipe recipe)
    {
        // Fluid recipes may have identical item ids (and source ids) but differ in volume or components.
        if (!recipe.getFluidInputs().isEmpty())
        {
            final var registries = net.minecraft.client.Minecraft.getInstance().level.registryAccess();
            final var ops = registries.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
            final var content = new net.minecraft.nbt.CompoundTag();
            content.put("fluids", net.neoforged.neoforge.fluids.FluidStack.CODEC.listOf()
                .encodeStart(ops, recipe.getFluidInputs()).getOrThrow());
            content.put("items", ItemStack.OPTIONAL_CODEC.listOf().listOf()
                .encodeStart(ops, recipe.getInputs()).getOrThrow());
            content.put("outputs", ItemStack.OPTIONAL_CODEC.listOf()
                .encodeStart(ops, recipe.getAllMultiOutputs()).getOrThrow());
            // CompoundTag's SNBT visitor sorts keys; the digest is stable across reloads.
            return "fluid/" + com.google.common.hash.Hashing.sha256()
                .hashString(content.toString(), java.nio.charset.StandardCharsets.UTF_8);
        }
        final Identifier recipeId = recipe.getRecipeId();
        if (recipeId != null)
        {
            return recipeId.getPath();
        }

        final StringBuilder canonical = new StringBuilder();
        canonical.append(itemPath(recipe.getPrimaryOutput()));
        for (final ItemStack extra : recipe.getAdditionalOutputs())
        {
            canonical.append(';').append(itemPath(extra));
        }
        canonical.append('|');
        for (final List<ItemStack> alternatives : recipe.getInputs())
        {
            for (final ItemStack stack : alternatives)
            {
                canonical.append(itemPath(stack)).append(',');
            }
            canonical.append(';');
        }
        return "hash" + Integer.toHexString(canonical.toString().hashCode());
    }

    @NotNull
    private static String itemPath(@NotNull final ItemStack stack)
    {
        return stack.isEmpty() ? "empty" : net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
    }

    @NotNull
    private static EmiIngredient toIngredient(@NotNull final List<ItemStack> alternatives)
    {
        if (alternatives.size() == 1)
        {
            return EmiStack.of(alternatives.get(0));
        }

        final List<EmiStack> stacks = new ArrayList<>(alternatives.size());
        for (final ItemStack stack : alternatives)
        {
            stacks.add(EmiStack.of(stack));
        }
        return EmiIngredient.of(stacks);
    }
}

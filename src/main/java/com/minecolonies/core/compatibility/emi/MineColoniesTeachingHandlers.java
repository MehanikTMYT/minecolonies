package com.minecolonies.core.compatibility.emi;

import com.minecolonies.api.inventory.ModContainers;
import com.minecolonies.api.inventory.container.ContainerCrafting;
import com.minecolonies.api.inventory.container.ContainerCraftingBrewingstand;
import com.minecolonies.api.inventory.container.ContainerCraftingFurnace;
import com.minecolonies.core.client.gui.containers.WindowBrewingstandCrafting;
import com.minecolonies.core.client.gui.containers.WindowCrafting;
import com.minecolonies.core.client.gui.containers.WindowFurnaceCrafting;
import com.minecolonies.core.network.messages.server.TransferRecipeCraftingTeachingMessage;
import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EMI support for teaching recipes to MineColonies workers (replaces the JEI Private*TeachingTransferHandler and
 * *CraftingGuiHandler classes): the "+" transfer button fills the teaching grid with ghost items from the recipe, and
 * items can be dragged from EMI onto the teaching slots. Nothing is taken from the player's inventory - teaching only
 * records the recipe, exactly as before.
 */
public final class MineColoniesTeachingHandlers
{
    private MineColoniesTeachingHandlers()
    {
    }

    public static void register(@NotNull final EmiRegistry registry)
    {
        registry.addRecipeHandler(ModContainers.craftingGrid.get(), new CraftingTeaching());
        registry.addRecipeHandler(ModContainers.craftingFurnace.get(), new FurnaceTeaching());
        registry.addRecipeHandler(ModContainers.craftingBrewingstand.get(), new BrewingTeaching());

        registry.addDragDropHandler(WindowCrafting.class, new EmiDragDropHandler.SlotBased<>(
          (gui, slot) -> slot.isActive() && slot.container instanceof CraftingContainer,
          (gui, slot, ingredient) -> {
              slot.set(first(ingredient));
              updateCrafting(gui);
          }));
        registry.addDragDropHandler(WindowFurnaceCrafting.class, new EmiDragDropHandler.SlotBased<>(
          (gui, slot) -> slot.isActive() && slot.getSlotIndex() == 0,
          (gui, slot, ingredient) -> {
              slot.set(first(ingredient));
              final Map<Integer, ItemStack> matrix = new HashMap<>();
              matrix.put(0, gui.getMenu().getSlot(0).getItem());
              new TransferRecipeCraftingTeachingMessage(matrix, false).sendToServer();
          }));
        registry.addDragDropHandler(WindowBrewingstandCrafting.class, new EmiDragDropHandler.SlotBased<>(
          (gui, slot) -> slot.isActive() && slot.index >= 0 && slot.index <= 3,
          (gui, slot, ingredient) -> {
              slot.set(first(ingredient));
              final ContainerCraftingBrewingstand menu = gui.getMenu();
              final Map<Integer, ItemStack> matrix = new HashMap<>();
              matrix.put(0, menu.getSlot(0).getItem());
              for (int index = 1; index <= 3; ++index)
              {
                  // Upstream read getSlot(1) on every iteration; use the slot being scanned.
                  final ItemStack container = menu.getSlot(index).getItem();
                  if (!container.isEmpty())
                  {
                      matrix.put(1, container);
                      break;
                  }
              }
              new TransferRecipeCraftingTeachingMessage(matrix, false).sendToServer();
          }));
    }

    @NotNull
    private static ItemStack first(@NotNull final EmiIngredient ingredient)
    {
        for (final EmiStack stack : ingredient.getEmiStacks())
        {
            final ItemStack item = stack.getItemStack();
            if (!item.isEmpty())
            {
                return item.copyWithCount(1);
            }
        }
        return ItemStack.EMPTY;
    }

    private static void updateCrafting(@NotNull final WindowCrafting gui)
    {
        final Map<Integer, ItemStack> matrix = new HashMap<>();
        final CraftingContainer inventory = gui.getMenu().getInv();
        if (gui.isCompleteCrafting())
        {
            for (int i = 0; i < 9; ++i)
            {
                matrix.put(i, inventory.getItem(i));
            }
        }
        else
        {
            matrix.put(0, inventory.getItem(0));
            matrix.put(1, inventory.getItem(1));
            matrix.put(3, inventory.getItem(2));
            matrix.put(4, inventory.getItem(3));
        }
        new TransferRecipeCraftingTeachingMessage(matrix, gui.isCompleteCrafting()).sendToServer();
    }

    /** Teaching never consumes items, so the "inventory" EMI checks against is irrelevant. */
    private abstract static class Teaching<T extends AbstractContainerMenu> implements EmiRecipeHandler<T>
    {
        @Override
        public EmiPlayerInventory getInventory(@NotNull final AbstractContainerScreen<T> screen)
        {
            return new EmiPlayerInventory(List.of());
        }

        @Override
        public boolean canCraft(@NotNull final EmiRecipe recipe, @NotNull final EmiCraftContext<T> context)
        {
            return true;
        }
    }

    private static final class CraftingTeaching extends Teaching<ContainerCrafting>
    {
        @Override
        public boolean supportsRecipe(@NotNull final EmiRecipe recipe)
        {
            return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING && recipe.supportsRecipeTree();
        }

        @Override
        public boolean canCraft(@NotNull final EmiRecipe recipe, @NotNull final EmiCraftContext<ContainerCrafting> context)
        {
            if (context.getScreenHandler().isComplete())
            {
                return true;
            }
            // The 2x2 grid cannot hold ingredients outside its top-left corner.
            final List<EmiIngredient> inputs = recipe.getInputs();
            for (final int index : new int[] {2, 5, 6, 7, 8})
            {
                if (index < inputs.size() && !inputs.get(index).isEmpty())
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean craft(@NotNull final EmiRecipe recipe, @NotNull final EmiCraftContext<ContainerCrafting> context)
        {
            final ContainerCrafting menu = context.getScreenHandler();
            final List<EmiIngredient> inputs = recipe.getInputs();
            final Map<Integer, ItemStack> grid = new HashMap<>();
            for (int i = 0; i < 9; ++i)
            {
                grid.put(i, i < inputs.size() ? first(inputs.get(i)) : ItemStack.EMPTY);
            }
            final CraftingContainer craftMatrix = menu.getInv();
            if (menu.isComplete())
            {
                for (int i = 0; i < 9; ++i)
                {
                    craftMatrix.setItem(i, grid.get(i));
                }
            }
            else
            {
                craftMatrix.setItem(0, grid.get(0));
                craftMatrix.setItem(1, grid.get(1));
                craftMatrix.setItem(2, grid.get(3));
                craftMatrix.setItem(3, grid.get(4));
            }
            new TransferRecipeCraftingTeachingMessage(grid, menu.isComplete()).sendToServer();
            return true;
        }
    }

    private static final class FurnaceTeaching extends Teaching<ContainerCraftingFurnace>
    {
        @Override
        public boolean supportsRecipe(@NotNull final EmiRecipe recipe)
        {
            return recipe.getCategory() == VanillaEmiRecipeCategories.SMELTING;
        }

        @Override
        public boolean craft(@NotNull final EmiRecipe recipe, @NotNull final EmiCraftContext<ContainerCraftingFurnace> context)
        {
            final ItemStack input = recipe.getInputs().isEmpty() ? ItemStack.EMPTY : first(recipe.getInputs().get(0));
            if (input.isEmpty())
            {
                return false;
            }
            context.getScreenHandler().setFurnaceInput(input);
            final Map<Integer, ItemStack> matrix = new HashMap<>();
            matrix.put(0, input);
            new TransferRecipeCraftingTeachingMessage(matrix, false).sendToServer();
            return true;
        }
    }

    private static final class BrewingTeaching extends Teaching<ContainerCraftingBrewingstand>
    {
        @Override
        public boolean supportsRecipe(@NotNull final EmiRecipe recipe)
        {
            return recipe.getCategory() == VanillaEmiRecipeCategories.BREWING && recipe.getInputs().size() >= 2;
        }

        @Override
        public boolean craft(@NotNull final EmiRecipe recipe, @NotNull final EmiCraftContext<ContainerCraftingBrewingstand> context)
        {
            // EMI brewing recipes list the input potion first and the brewing ingredient second.
            final ItemStack container = first(recipe.getInputs().get(0));
            final ItemStack ingredient = first(recipe.getInputs().get(1));
            final ContainerCraftingBrewingstand menu = context.getScreenHandler();
            menu.setInput(ingredient);
            menu.setContainer(container);
            final Map<Integer, ItemStack> matrix = new HashMap<>();
            matrix.put(0, ingredient);
            matrix.put(1, container);
            new TransferRecipeCraftingTeachingMessage(matrix, false).sendToServer();
            return true;
        }
    }
}

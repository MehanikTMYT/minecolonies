package com.minecolonies.core.compatibility.emi;

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.loot.ModLootTables;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.blocks.MinecoloniesCropBlock;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingFlorist;
import com.minecolonies.core.colony.crafting.CustomRecipeManager;
import com.minecolonies.core.colony.crafting.LootTableAnalyzer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.minecolonies.api.util.constant.Constants.MAX_BUILDING_LEVEL;

/**
 * Recipe data for the loot-table based EMI categories (crops, fishing, flowers).
 * <p>
 * Moved here from the former JEI categories (CropRecipeCategory, FishermanRecipeCategory, FloristRecipeCategory) when the
 * fleet dropped JEI in favour of native EMI; the data sources are unchanged.
 */
public final class MineColoniesEmiLoot
{
    private static final int MAX_FLOWER_SLOTS = 24;

    private MineColoniesEmiLoot()
    {
    }

    public record CropRecipe(@NotNull Block source) {}

    public record FishingRecipe(@NotNull ResourceKey<LootTable> id, int level, @NotNull List<LootTableAnalyzer.LootDrop> drops) {}

    public record FloristRecipe(int level, @NotNull List<List<ItemStack>> flowers) {}

    @NotNull
    public static List<CropRecipe> findCropRecipes()
    {
        final Set<Block> sourceBlocks = new HashSet<>();
        for (final MinecoloniesCropBlock crop : ModBlocks.getCrops())
        {
            sourceBlocks.addAll(crop.getDroppedFrom());
        }
        return sourceBlocks.stream().map(CropRecipe::new).toList();
    }

    @NotNull
    public static List<FishingRecipe> findFishingRecipes()
    {
        final List<LootTableAnalyzer.LootDrop> commonDrops = CustomRecipeManager.getInstance().getLootDrops(ModLootTables.FISHING);
        final List<FishingRecipe> recipes = new ArrayList<>();
        for (final Map.Entry<Integer, ResourceKey<LootTable>> level : ModLootTables.FISHERMAN_BONUS.entrySet())
        {
            final List<LootTableAnalyzer.LootDrop> drops = new ArrayList<>(commonDrops);
            drops.addAll(CustomRecipeManager.getInstance().getLootDrops(level.getValue()));
            recipes.add(new FishingRecipe(level.getValue(), level.getKey(), drops.size() > 18 ? LootTableAnalyzer.consolidate(drops) : drops));
        }
        return recipes;
    }

    @NotNull
    public static List<FloristRecipe> findFloristRecipes()
    {
        final List<FloristRecipe> recipes = new ArrayList<>();
        for (int level = 1; level <= MAX_BUILDING_LEVEL; ++level)
        {
            recipes.add(new FloristRecipe(level, compactify(BuildingFlorist.getPlantablesForBuildingLevel(level))));
        }
        return recipes;
    }

    /**
     * Spreads the plantable flowers over at most {@value MAX_FLOWER_SLOTS} display slots (cycling slots when there are more).
     */
    @NotNull
    private static List<List<ItemStack>> compactify(@NotNull final Set<ItemStorage> flowers)
    {
        final List<List<ItemStack>> slots = new ArrayList<>();
        final List<ItemStack> flowerList = flowers.stream().map(ItemStorage::getItemStack).toList();
        if (flowerList.size() < MAX_FLOWER_SLOTS)
        {
            for (final ItemStack item : flowerList)
            {
                slots.add(List.of(item));
            }
            return slots;
        }
        final int itemsPerList = flowerList.size() / MAX_FLOWER_SLOTS;
        int extraItems = flowerList.size() % MAX_FLOWER_SLOTS;
        int currentIndex = 0;
        for (int i = 0; i < MAX_FLOWER_SLOTS; ++i)
        {
            final List<ItemStack> sublist = new ArrayList<>();
            for (int j = 0; j < itemsPerList; ++j)
            {
                sublist.add(flowerList.get(currentIndex++));
            }
            if (extraItems > 0)
            {
                sublist.add(flowerList.get(currentIndex++));
                --extraItems;
            }
            slots.add(sublist);
        }
        return slots;
    }

    /**
     * Tooltip lines for a loot drop (chance, skill dependence, conditions, and the loot table id with advanced tooltips).
     */
    @NotNull
    public static List<Component> dropTooltip(@NotNull final LootTableAnalyzer.LootDrop drop, @NotNull final ResourceKey<LootTable> table)
    {
        final List<Component> lines = new ArrayList<>();
        final String key = TranslationConstants.PARTIAL_JEI_INFO +
                             (drop.getQuality() < 0 ? "chancenegskill.tip" : drop.getQuality() > 0 ? "chanceskill.tip" : "chance.tip");
        final float probability = drop.getProbability() * 100;
        lines.add(probability >= 1
                    ? Component.translatableEscape(key, Math.round(probability))
                    : Component.translatableEscape(key, Math.round(probability * 100) / 100f));
        if (drop.getConditional())
        {
            lines.add(Component.translatableEscape(TranslationConstants.PARTIAL_JEI_INFO + "conditions.tip"));
        }
        if (Minecraft.getInstance().options.advancedItemTooltips || Minecraft.getInstance().hasShiftDown())
        {
            final MutableComponent id = Component.translatableEscape("com.minecolonies.coremod.jei.loottableid", table.identifier().toString());
            lines.add(id.withStyle(ChatFormatting.DARK_GRAY));
        }
        return lines;
    }
}

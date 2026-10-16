package com.minecolonies.core.compatibility.emi;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.modules.ICraftingBuildingModule;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.crafting.CompostRecipe;
import com.minecolonies.api.crafting.IGenericRecipe;
import com.minecolonies.api.crafting.registry.CraftingType;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.colony.buildings.modules.AnimalHerdingModule;
import com.minecolonies.core.colony.crafting.RecipeAnalyzer;
import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.core.colony.crafting.CustomRecipeManager;
import com.minecolonies.core.colony.crafting.LootTableAnalyzer;
import com.minecolonies.core.colony.crafting.ToolUsage;
import com.minecolonies.core.colony.crafting.ToolsAnalyzer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Native EMI plugin entrypoint for MineColonies.
 * <p>
 * Reuses exactly the same recipe data sources as the (now disabled) JEI integration -
 * {@link RecipeAnalyzer}, {@link IGenericRecipe} and the building/module registry - so both
 * are always in sync with the single runtime source of truth; there is no separate hardcoded
 * recipe list for EMI.
 * <p>
 * One {@link EmiRecipeCategory} is created per distinct MineColonies job/profession that has a
 * crafting or animal-herding building module (Baker, Blacksmith, Sawmill, ...), plus a fixed
 * category for composting. The corresponding hut/barrel block is registered as that category's
 * workstation (EMI's equivalent of a JEI recipe catalyst).
 */
@EmiEntrypoint
public class MineColoniesEmiPlugin implements EmiPlugin
{
    private final Map<JobEntry, EmiRecipeCategory> categories = new HashMap<>();
    private final Map<JobEntry, ItemStack>         catalysts  = new HashMap<>();

    private EmiRecipeCategory compostCategory;
    private EmiRecipeCategory cropsCategory;
    private EmiRecipeCategory toolsCategory;
    private EmiRecipeCategory fishingCategory;
    private EmiRecipeCategory flowersCategory;


    @Override
    public void register(@NotNull final EmiRegistry registry)
    {
        this.categories.clear();
        this.catalysts.clear();

        buildCategories();

        for (final EmiRecipeCategory category : this.categories.values())
        {
            registry.addCategory(category);
        }
        registry.addCategory(compostCategory);
        registry.addCategory(cropsCategory);
        registry.addCategory(toolsCategory);
        registry.addCategory(fishingCategory);
        registry.addCategory(flowersCategory);

        for (final Map.Entry<JobEntry, ItemStack> entry : this.catalysts.entrySet())
        {
            registry.addWorkstation(this.categories.get(entry.getKey()), EmiStack.of(entry.getValue()));
        }
        registry.addWorkstation(compostCategory, EmiStack.of(new ItemStack(ModBlocks.blockBarrel)));
        registry.addWorkstation(compostCategory, EmiStack.of(ModBlocks.blockHutComposter));
        registry.addWorkstation(fishingCategory, EmiStack.of(ModBlocks.blockHutFisherman));
        registry.addWorkstation(flowersCategory, EmiStack.of(ModBlocks.blockHutFlorist));

        // Was the JEI transfer/ghost-ingredient handlers: teach recipes to workers from EMI.
        MineColoniesTeachingHandlers.register(registry);

        registry.addDeferredRecipes(
          recipeConsumer -> populateRecipes(recipeConsumer::accept));
    }

    /**
     * Walks the building/module registry to discover every distinct job that has a crafting or
     * animal-herding module, mirroring exactly what the old {@code JEIPlugin.registerCategories} did.
     */
    private void buildCategories()
    {
        for (final BuildingEntry building : IMinecoloniesAPI.getInstance().getBuildingRegistry())
        {
            for (final BuildingEntry.ModuleProducer<?, ?> producer : building.getModuleProducers())
            {
                if (!producer.hasServerModule())
                {
                    continue;
                }

                final var module = BuildingEntry.produceModuleWithoutBuilding(producer.key);
                if (module == null)
                {
                    continue;
                }

                IJob<?> job = null;
                if (module instanceof final ICraftingBuildingModule crafting)
                {
                    job = crafting.getCraftingJob();
                }
                else if (module instanceof final AnimalHerdingModule herding)
                {
                    job = herding.getHerdingJob();
                }

                if (job == null)
                {
                    continue;
                }

                final JobEntry jobEntry = job.getJobRegistryEntry();
                this.categories.computeIfAbsent(jobEntry, key -> new MineColoniesEmiRecipeCategory(
                  Identifier.fromNamespaceAndPath(Constants.MOD_ID, key.getKey().getPath()),
                  EmiStack.of(new ItemStack(building.getBuildingBlock())),
                  Component.translatableEscape(key.getTranslationKey())));
                this.catalysts.putIfAbsent(jobEntry, new ItemStack(building.getBuildingBlock()));
            }
        }

        this.cropsCategory = new MineColoniesEmiRecipeCategory(
          Identifier.fromNamespaceAndPath(Constants.MOD_ID, "crops"),
          EmiStack.of(new ItemStack(Items.DIAMOND_HOE)),
          Component.translatable(TranslationConstants.PARTIAL_JEI_INFO + "crops"));
        this.toolsCategory = new MineColoniesEmiRecipeCategory(
          Identifier.fromNamespaceAndPath(Constants.MOD_ID, "tools"),
          EmiStack.of(new ItemStack(ModItems.plateArmorChest)),
          Component.translatableEscape(TranslationConstants.PARTIAL_JEI_INFO + "tools"));
        this.fishingCategory = new MineColoniesEmiRecipeCategory(
          Identifier.fromNamespaceAndPath(Constants.MOD_ID, "fishing"),
          EmiStack.of(new ItemStack(ModBlocks.blockHutFisherman)),
          Component.translatableEscape(ModJobs.fisherman.get().getTranslationKey()));
        this.flowersCategory = new MineColoniesEmiRecipeCategory(
          Identifier.fromNamespaceAndPath(Constants.MOD_ID, "flowers"),
          EmiStack.of(new ItemStack(ModBlocks.blockHutFlorist)),
          Component.translatableEscape(ModJobs.florist.get().getTranslationKey()));

        this.compostCategory = new MineColoniesEmiRecipeCategory(
          Identifier.fromNamespaceAndPath(Constants.MOD_ID, "composting"),
          EmiStack.of(new ItemStack(ModBlocks.blockBarrel)),
          Component.translatableEscape(ModBlocks.blockBarrel.getDescriptionId()));
    }

    /**
     * Gathers every recipe from the current game state during EMI's
     * deferred-recipe phase, before EMI performs its recipe bake.
     */
    private void populateRecipes(@NotNull final java.util.function.Consumer<EmiRecipe> consumer)
    {
        final ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
        {
            return;
        }

        try
        {
            populateLootRecipes(level, consumer);
        }
        catch (final Exception e)
        {
            Log.getLogger().error("Failed to process MineColonies EMI loot/tool recipes", e);
        }

        for (final Map.Entry<Item, CompostRecipe> entry : findCompostRecipes().entrySet())
        {
            final CompostRecipe compost = entry.getValue();
            final List<EmiIngredient> inputs = List.of(EmiIngredient.of(compost.getInput()));
            final List<EmiStack> outputs = List.of(EmiStack.of(compost.getResultItem()));
            final Identifier itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(entry.getKey());
            final Identifier id = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "composting/" + itemId.getPath());
            consumer.accept(new MineColoniesEmiRecipe(compostCategory, id, inputs, outputs));
        }

        // Minecraft 26.1's client no longer exposes a full server-side RecipeManager (recipe display
        // data is synced separately from actual Recipe objects after the network refactor), so there is
        // no client-safe way to gather "vanilla recipes this worker can also be taught". That's fine:
        // EMI already displays ordinary vanilla recipes natively, and we must not duplicate them here
        // (see class javadoc) - only MineColonies' own custom/teachable recipes are gathered below.
        final Map<CraftingType, List<IGenericRecipe>> vanilla =
            RecipeAnalyzer.buildVanillaRecipesMap(
              com.minecolonies.core.client.recipe.ClientRecipeCache.getRecipes(),
              level);
        final List<Animal> animals = RecipeAnalyzer.createAnimals(level);

        for (final BuildingEntry building : IMinecoloniesAPI.getInstance().getBuildingRegistry())
        {
            for (final BuildingEntry.ModuleProducer<?, ?> producer : building.getModuleProducers())
            {
                if (!producer.hasServerModule())
                {
                    continue;
                }

                final var module = BuildingEntry.produceModuleWithoutBuilding(producer.key);
                if (module == null)
                {
                    continue;
                }

                try
                {
                    if (module instanceof final ICraftingBuildingModule crafting)
                    {
                        addRecipesForJob(crafting.getCraftingJob(), RecipeAnalyzer.findRecipes(vanilla, crafting, level), consumer);
                    }
                    else if (module instanceof final AnimalHerdingModule herding)
                    {
                        addRecipesForJob(herding.getHerdingJob(), RecipeAnalyzer.findRecipes(animals, herding), consumer);
                    }
                }
                catch (final Exception e)
                {
                    Log.getLogger().error("Failed to process EMI recipes for building " + building.getRegistryName(), e);
                }
            }
        }
    }

    /**
     * Crops, fishing, flowers and tools - the former JEI CropRecipeCategory, FishermanRecipeCategory, FloristRecipeCategory
     * and ToolRecipeCategory, on the same data sources.
     */
    private void populateLootRecipes(@NotNull final Level level, @NotNull final java.util.function.Consumer<EmiRecipe> consumer)
    {
        final List<EmiIngredient> hoes = equipment(ModEquipmentTypes.hoe.get());
        for (final MineColoniesEmiLoot.CropRecipe crop : MineColoniesEmiLoot.findCropRecipes())
        {
            final ResourceKey<LootTable> table = crop.source().getLootTable().orElse(null);
            if (table == null)
            {
                continue;
            }
            final List<MineColoniesLootEmiRecipe.Output> outputs = lootOutputs(CustomRecipeManager.getInstance().getLootDrops(table), table);
            final Identifier blockId = BuiltInRegistries.BLOCK.getKey(crop.source());
            consumer.accept(new MineColoniesLootEmiRecipe(cropsCategory,
              Identifier.fromNamespaceAndPath(Constants.MOD_ID, "/crops/" + blockId.getNamespace() + "/" + blockId.getPath()),
              List.of(EmiStack.of(crop.source())), hoes, outputs, null));
        }

        final List<EmiIngredient> rods = equipment(ModEquipmentTypes.fishing_rod.get());
        for (final MineColoniesEmiLoot.FishingRecipe fishing : MineColoniesEmiLoot.findFishingRecipes())
        {
            consumer.accept(new MineColoniesLootEmiRecipe(fishingCategory,
              Identifier.fromNamespaceAndPath(Constants.MOD_ID, "/fishing/level_" + fishing.level()),
              List.of(), rods, lootOutputs(fishing.drops(), fishing.id()), levelLabel(fishing.level())));
        }

        final List<EmiIngredient> compost = List.of(EmiStack.of(ModItems.compost));
        for (final MineColoniesEmiLoot.FloristRecipe florist : MineColoniesEmiLoot.findFloristRecipes())
        {
            final List<MineColoniesLootEmiRecipe.Output> outputs = new java.util.ArrayList<>();
            for (final List<ItemStack> flowers : florist.flowers())
            {
                MineColoniesLootEmiRecipe.stacks(flowers).forEach(stack -> outputs.add(new MineColoniesLootEmiRecipe.Output(stack, List.of())));
            }
            consumer.accept(new MineColoniesLootEmiRecipe(flowersCategory,
              Identifier.fromNamespaceAndPath(Constants.MOD_ID, "/flowers/level_" + florist.level()),
              List.of(), compost, outputs, levelLabel(florist.level())));
        }

        for (final ToolUsage tool : ToolsAnalyzer.findTools(level))
        {
            final Identifier toolId = tool.tool().getRegistryName();
            consumer.accept(new MineColoniesToolEmiRecipe(toolsCategory,
              Identifier.fromNamespaceAndPath(Constants.MOD_ID, "/tools/" + toolId.getNamespace() + "/" + toolId.getPath()), tool));
        }
    }

    @NotNull
    private static Component levelLabel(final int level)
    {
        return Component.translatableEscape(TranslationConstants.PARTIAL_JEI_INFO + "onelevelrestriction", level);
    }

    @NotNull
    private static List<MineColoniesLootEmiRecipe.Output> lootOutputs(@NotNull final List<LootTableAnalyzer.LootDrop> drops,
                                                                       @NotNull final ResourceKey<LootTable> table)
    {
        final List<MineColoniesLootEmiRecipe.Output> outputs = new java.util.ArrayList<>();
        for (final LootTableAnalyzer.LootDrop drop : drops)
        {
            MineColoniesLootEmiRecipe.stacks(drop.getItemStacks())
              .forEach(stack -> outputs.add(new MineColoniesLootEmiRecipe.Output(stack, MineColoniesEmiLoot.dropTooltip(drop, table))));
        }
        return outputs;
    }

    /** Every known item of an equipment type, weakest first (the JEI categories' tool slot). */
    @NotNull
    private static List<EmiIngredient> equipment(@NotNull final EquipmentTypeEntry type)
    {
        final List<EmiStack> stacks = IColonyManager.getInstance().getCompatibilityManager().getListOfAllItems().stream()
                                        .filter(type::checkIsEquipment)
                                        .sorted(java.util.Comparator.comparing(type::getMiningLevel))
                                        .map(EmiStack::of)
                                        .toList();
        return stacks.isEmpty() ? List.of() : List.of(EmiIngredient.of(stacks));
    }

    private void addRecipesForJob(@Nullable final IJob<?> job,
                                   @NotNull final List<IGenericRecipe> recipes,
                                   @NotNull final java.util.function.Consumer<EmiRecipe> consumer)
    {
        if (job == null)
        {
            return;
        }

        final EmiRecipeCategory category = this.categories.get(job.getJobRegistryEntry());
        if (category == null)
        {
            return;
        }

        for (final IGenericRecipe recipe : recipes)
        {
            consumer.accept(MineColoniesEmiRecipe.fromGeneric(category, recipe));
        }
    }

    /**
     * @return the current compost recipes, keyed by their input item (the same key
     *         {@link com.minecolonies.api.compatibility.CompatibilityManager} uses internally, so it
     *         gives a stable, content-derived id instead of relying on object identity).
     */
    @NotNull
    private static Map<Item, CompostRecipe> findCompostRecipes()
    {
        return IColonyManager.getInstance().getCompatibilityManager().getCopyOfCompostRecipes().entrySet().stream()
                 .collect(Collectors.toMap(Map.Entry::getKey, entry -> CompostRecipe.individualize(entry.getKey(), entry.getValue())));
    }

}


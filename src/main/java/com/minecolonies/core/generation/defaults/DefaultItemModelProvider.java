package com.minecolonies.core.generation.defaults;

import com.minecolonies.api.items.ModItems;
import com.minecolonies.core.client.model.BlueprintRenderDisabled;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static com.minecolonies.api.util.constant.Constants.MOD_ID;

/**
 * Datagen for item models.
 * <p>
 * Block models, block states and most item inventory models (including every hut block's hand-held
 * variant) are shipped as static resources under {@code assets/minecolonies}; this provider only has to
 * emit the client-item descriptors ({@code assets/minecolonies/items/<id>.json}) that Minecraft 26.1
 * requires for every registered item, plus the handful of items whose model is actually generated
 * (food/ingredients, the build goggles conditional model, and the camp spawn eggs).
 */
public class DefaultItemModelProvider extends ModelProvider
{
    /**
     * Items that already ship a fully hand-authored client-item descriptor (including a custom
     * {@code SpecialModelRenderer}) and must never be touched by this provider.
     */
    private static final Set<String> STATIC_ONLY_ITEMS = Set.of("spear");

    /**
     * Registry paths of the camp spawn eggs, whose model and flat icon texture are generated here since
     * Minecraft 26.1 no longer ships the shared tinted {@code item/template_spawn_egg} parent that
     * MineColonies previously relied on.
     */
    private static final String[] SPAWN_EGGS = {
      "barbarianegg", "barbarcheregg", "barbchiefegg",
      "pirateegg", "piratearcheregg", "piratecaptainegg",
      "mummyegg", "mummyarcheregg", "pharaoegg",
      "shieldmaidenegg", "norsemenarcheregg", "norsemenchiefegg",
      "amazonegg", "amazonspearmanegg", "amazonchiefegg",
      "drownedpirateegg", "drownedpiratearcheregg", "drownedpiratecaptainegg"
    };

    /**
     * Constructor
     */
    public DefaultItemModelProvider(final PackOutput packOutput)
    {
        super(packOutput, MOD_ID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks()
    {
        // This provider only generates item models; block models and block states are shipped as static resources.
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems()
    {
        return super.getKnownItems().filter(holder -> !STATIC_ONLY_ITEMS.contains(holder.getKey().identifier().getPath()));
    }

    @Override
    protected void registerModels(final BlockModelGenerators blockModels, final ItemModelGenerators itemModels)
    {
        final Set<Item> handled = new HashSet<>();

        final Identifier enabledGoggles = ModelLocationUtils.getModelLocation(ModItems.buildGoggles);
        final Identifier disabledGoggles = Identifier.fromNamespaceAndPath(MOD_ID, "item/build_goggles_disabled");

        itemModels.createFlatItemModel(ModItems.buildGoggles, ModelTemplates.FLAT_ITEM);
        ModelTemplates.FLAT_ITEM.create(disabledGoggles,
          TextureMapping.layer0(new Material(Identifier.fromNamespaceAndPath(MOD_ID, "item/build_goggles_disabled"))),
          itemModels.modelOutput);

        itemModels.itemModelOutput.accept(ModItems.buildGoggles,
          ItemModelUtils.conditional(new BlueprintRenderDisabled(),
            ItemModelUtils.plainModel(disabledGoggles),
            ItemModelUtils.plainModel(enabledGoggles)));
        handled.add(ModItems.buildGoggles);

        for (final Item foodItem : ModItems.getAllIngredients())
        {
            generateFoodItem(itemModels, foodItem);
            handled.add(foodItem);
        }

        for (final Item foodItem : ModItems.getAllFoods())
        {
            generateFoodItem(itemModels, foodItem);
            handled.add(foodItem);
        }

        for (final String eggName : SPAWN_EGGS)
        {
            final Item egg = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(MOD_ID, eggName));
            generateSpawnEgg(itemModels, egg, eggName);
            handled.add(egg);
        }

        // Every other registered item either already ships a static item/block inventory model under
        // assets/minecolonies/models, or is a BlockItem whose client-item descriptor the base ModelProvider
        // fills in automatically (pointing at the block model) once it's left unregistered here.
        getKnownItems().map(Holder::value).forEach(item -> {
            if (handled.contains(item))
            {
                return;
            }

            final Identifier id = BuiltInRegistries.ITEM.getKey(item);
            if (id == null)
            {
                return;
            }

            final String path = id.getPath();
            if (hasStaticItemModel(path))
            {
                itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(MOD_ID, "item/" + path)));
            }
        });
    }

    /**
     * Whether a static (hand-authored) model already exists on the classpath for the given registry path.
     *
     * @param path the item's registry path.
     * @return {@code true} if {@code assets/minecolonies/models/item/<path>.json} is shipped as a resource.
     */
    private static boolean hasStaticItemModel(final String path)
    {
        return DefaultItemModelProvider.class.getClassLoader().getResource("assets/" + MOD_ID + "/models/item/" + path + ".json") != null;
    }

    /**
     * Generate a flat item model taking its texture from the shared {@code item/food} folder.
     *
     * @param itemModels the item model generator to write to.
     * @param foodItem   the item to generate the model for.
     */
    private static void generateFoodItem(final ItemModelGenerators itemModels, final Item foodItem)
    {
        final Identifier model = ModelLocationUtils.getModelLocation(foodItem);
        final Identifier texture = Identifier.fromNamespaceAndPath(MOD_ID,
          "item/food/" + BuiltInRegistries.ITEM.wrapAsHolder(foodItem).getKey().identifier().getPath());

        ModelTemplates.FLAT_ITEM.create(model, new TextureMapping().put(TextureSlot.LAYER0, new Material(texture)), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(foodItem, ItemModelUtils.plainModel(model));
    }

    /**
     * Generate a flat item model for a camp spawn egg, taking its texture from the {@code item/spawnegg}
     * folder. Minecraft 26.1 removed the shared tinted {@code item/template_spawn_egg} parent that
     * MineColonies' spawn eggs previously relied on, so each egg now ships its own baked flat icon, in
     * line with how vanilla spawn eggs are generated in this version.
     *
     * @param itemModels the item model generator to write to.
     * @param egg        the spawn egg item to generate the model for.
     * @param name       the egg's registry path.
     */
    private static void generateSpawnEgg(final ItemModelGenerators itemModels, final Item egg, final String name)
    {
        final Identifier model = ModelLocationUtils.getModelLocation(egg);
        final Identifier texture = Identifier.fromNamespaceAndPath(MOD_ID, "item/spawnegg/" + name);

        ModelTemplates.FLAT_ITEM.create(model, new TextureMapping().put(TextureSlot.LAYER0, new Material(texture)), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(egg, ItemModelUtils.plainModel(model));
    }
}

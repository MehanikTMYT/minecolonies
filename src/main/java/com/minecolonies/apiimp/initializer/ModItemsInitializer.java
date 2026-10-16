package com.minecolonies.apiimp.initializer;

import com.ldtteam.blockui.Color;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.entity.ModEntities;
import com.minecolonies.api.items.ModEquipmentAssets;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.items.ModTags;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.items.*;
import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.EnumMap;
import java.util.List;

import static com.minecolonies.api.blocks.decorative.AbstractBlockGate.IRON_GATE;
import static com.minecolonies.api.blocks.decorative.AbstractBlockGate.WOODEN_GATE;

@EventBusSubscriber(modid = Constants.MOD_ID)
public final class ModItemsInitializer
{
    /**
     * Spawn egg colors.
     */
    private static final int PRIMARY_COLOR_BARBARIAN   = 5;
    private static final int SECONDARY_COLOR_BARBARIAN = 700;
    private static final int PRIMARY_COLOR_PIRATE      = 7;
    private static final int SECONDARY_COLOR_PIRATE    = 600;
    private static final int PRIMARY_COLOR_EG          = 10;
    private static final int SECONDARY_COLOR_EG        = 400;

    private ModItemsInitializer()
    {
        throw new IllegalStateException("Tried to initialize: ModItemsInitializer but this is a Utility class.");
    }

    @SubscribeEvent
    public static void registerItems(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(Registries.ITEM))
        {
            ModItemsInitializer.init(event.getRegistry(Registries.ITEM));
        }
    }

    /**
     * Initates all the blocks. At the correct time.
     *
     * @param registry the registry.
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public static void init(final Registry<Item> registry)
    {
        ModItems.scepterLumberjack = new ItemScepterLumberjack(propertiesForItem("scepterlumberjack"));
        ModItems.supplyChest = new ItemSupplyChestDeployer(propertiesForItem("supplychestdeployer"));
        ModItems.permTool = new ItemScepterPermission(propertiesForItem("scepterpermission"));
        ModItems.scepterGuard = new ItemScepterGuard(propertiesForItem("scepterguard"));
        ModItems.assistantHammer_Gold = new ItemAssistantHammer("assistanthammer_gold", propertiesForItem("assistanthammer_gold").durability(200), 1);
        ModItems.assistantHammer_Iron = new ItemAssistantHammer("assistanthammer_iron", propertiesForItem("assistanthammer_iron").durability(400), 2);
        ModItems.assistantHammer_Diamond = new ItemAssistantHammer("assistanthammer_diamond", propertiesForItem("assistanthammer_diamond").durability(1000), 3);
        ModItems.bannerRallyGuards = new ItemBannerRallyGuards(propertiesForItem("banner_rally_guards"));
        ModItems.supplyCamp = new ItemSupplyCampDeployer(propertiesForItem("supplycampdeployer"));
        ModItems.ancientTome = new ItemAncientTome(propertiesForItem("ancienttome"));
        ModItems.chiefSword = new ItemChiefSword(propertiesForItem("chiefsword"));
        ModItems.scimitar = new ItemIronScimitar(propertiesForItem("iron_scimitar"));
        ModItems.clipboard = new ItemClipboard(propertiesForItem("clipboard"));
        ModItems.compost = new ItemCompost(propertiesForItem("compost"));
        ModItems.resourceScroll = new ItemResourceScroll(propertiesForItem("resourcescroll"));
        ModItems.pharaoscepter = new ItemPharaoScepter(propertiesForItem("pharaoscepter").durability(400));
        ModItems.firearrow = new ItemFireArrow(propertiesForItem("firearrow"));
        ModItems.scepterBeekeeper = new ItemScepterBeekeeper(propertiesForItem("scepterbeekeeper"));
        ModItems.mistletoe = new ItemMistletoe(propertiesForItem("mistletoe"));
        ModItems.spear = new ItemSpear(propertiesForItem("spear"));
        ModItems.questLog = new ItemQuestLog(propertiesForItem("questlog"));

        ModItems.breadDough = new ItemBreadDough(propertiesForItem("bread_dough"));
        ModItems.cookieDough = new ItemCookieDough(propertiesForItem("cookie_dough"));
        ModItems.cakeBatter = new ItemCakeBatter(propertiesForItem("cake_batter"));
        ModItems.rawPumpkinPie = new ItemRawPumpkinPie(propertiesForItem("raw_pumpkin_pie"));

        ModItems.milkyBread = new ItemMilkyBread(propertiesForItem("milky_bread"));
        ModItems.registerFoodProperties("milky_bread", ItemMilkyBread.milkBread);
        ModItems.sugaryBread = new ItemSugaryBread(propertiesForItem("sugary_bread"));
        ModItems.registerFoodProperties("sugary_bread", ItemSugaryBread.sweetBread);
        ModItems.goldenBread = new ItemGoldenBread(propertiesForItem("golden_bread"));
        ModItems.registerFoodProperties("golden_bread", ItemGoldenBread.goldenBread);
        ModItems.chorusBread = new ItemChorusBread(propertiesForItem("chorus_bread"));
        ModItems.registerFoodProperties("chorus_bread", ItemChorusBread.chorusBread);

        ModItems.adventureToken = new ItemAdventureToken(propertiesForItem("adventure_token"));

        ModItems.scrollColonyTP = new ItemScrollColonyTP(propertiesForItem("scroll_tp").stacksTo(16));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "scroll_tp"), ModItems.scrollColonyTP);

        ModItems.scrollColonyAreaTP = new ItemScrollColonyAreaTP(propertiesForItem("scroll_area_tp").stacksTo(16));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "scroll_area_tp"), ModItems.scrollColonyAreaTP);

        ModItems.scrollBuff = new ItemScrollBuff(propertiesForItem("scroll_buff").stacksTo(16));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "scroll_buff"), ModItems.scrollBuff);

        ModItems.scrollGuardHelp = new ItemScrollGuardHelp(propertiesForItem("scroll_guard_help").stacksTo(16));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "scroll_guard_help"), ModItems.scrollGuardHelp);

        ModItems.scrollHighLight = new ItemScrollHighlight(propertiesForItem("scroll_highlight").stacksTo(16));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "scroll_highlight"), ModItems.scrollHighLight);

        ModItems.santaHat = new ItemSantaHead("santa_hat", SANTA_HAT, ArmorType.HELMET, propertiesForItem("santa_hat"));
        ModItems.irongate = new ItemGate(IRON_GATE, ModBlocks.blockIronGate, propertiesForItem("gate_iron").useBlockDescriptionPrefix());
        ModItems.woodgate = new ItemGate(WOODEN_GATE, ModBlocks.blockWoodenGate, propertiesForItem("gate_wood").useBlockDescriptionPrefix());

        ModItems.flagBanner = new ItemColonyFlagBanner("colony_banner", propertiesForItem("colony_banner"));
        ModItems.pirateHelmet_1 = new ItemPirateGear("pirate_hat", PIRATE_ARMOR_1, ArmorType.HELMET, propertiesForItem("pirate_hat").durability(350));
        ModItems.pirateChest_1 = new ItemPirateGear("pirate_top", PIRATE_ARMOR_1, ArmorType.CHESTPLATE, propertiesForItem("pirate_top").durability(550));
        ModItems.pirateLegs_1 = new ItemPirateGear("pirate_leggins", PIRATE_ARMOR_1, ArmorType.LEGGINGS, propertiesForItem("pirate_leggins").durability(500));
        ModItems.pirateBoots_1 = new ItemPirateGear("pirate_boots", PIRATE_ARMOR_1, ArmorType.BOOTS, propertiesForItem("pirate_boots").durability(400));

        ModItems.pirateHelmet_2 = new ItemPirateGear("pirate_cap", PIRATE_ARMOR_2, ArmorType.HELMET, propertiesForItem("pirate_cap").durability(200));
        ModItems.pirateChest_2 = new ItemPirateGear("pirate_chest", PIRATE_ARMOR_2, ArmorType.CHESTPLATE, propertiesForItem("pirate_chest").durability(350));
        ModItems.pirateLegs_2 = new ItemPirateGear("pirate_legs", PIRATE_ARMOR_2, ArmorType.LEGGINGS, propertiesForItem("pirate_legs").durability(300));
        ModItems.pirateBoots_2 = new ItemPirateGear("pirate_shoes", PIRATE_ARMOR_2, ArmorType.BOOTS, propertiesForItem("pirate_shoes").durability(250));

        ModItems.plateArmorHelmet = new ItemPlateArmor("plate_armor_helmet", PLATE_ARMOR, ArmorType.HELMET, propertiesForItem("plate_armor_helmet").durability(350));
        ModItems.plateArmorChest = new ItemPlateArmor("plate_armor_chest", PLATE_ARMOR, ArmorType.CHESTPLATE, propertiesForItem("plate_armor_chest").durability(500));
        ModItems.plateArmorLegs = new ItemPlateArmor("plate_armor_legs", PLATE_ARMOR, ArmorType.LEGGINGS, propertiesForItem("plate_armor_legs").durability(450));
        ModItems.plateArmorBoots = new ItemPlateArmor("plate_armor_boots", PLATE_ARMOR, ArmorType.BOOTS, propertiesForItem("plate_armor_boots").durability(400));

        ModItems.sifterMeshString = new ItemSifterMesh("sifter_mesh_string", propertiesForItem("sifter_mesh_string").durability(500).setNoCombineRepair());
        ModItems.sifterMeshFlint = new ItemSifterMesh("sifter_mesh_flint", propertiesForItem("sifter_mesh_flint").durability(1000).setNoCombineRepair());
        ModItems.sifterMeshIron = new ItemSifterMesh("sifter_mesh_iron", propertiesForItem("sifter_mesh_iron").durability(1500).setNoCombineRepair());
        ModItems.sifterMeshDiamond = new ItemSifterMesh("sifter_mesh_diamond", propertiesForItem("sifter_mesh_diamond").durability(2000).setNoCombineRepair());

        ModItems.magicpotion = new ItemMagicPotion("magicpotion", propertiesForItem("magicpotion"));
        ModItems.buildGoggles = new ItemBuildGoggles("build_goggles", propertiesForItem("build_goggles"));
        ModItems.scanAnalyzer = new ItemScanAnalyzer("scan_analyzer", propertiesForItem("scan_analyzer"));
        ModItems.colonyMap = new ItemColonyMap(propertiesForItem("colonymap"));

        // All Biomes
        // Tier 1 Food Items
        ModItems.cheddar_cheese = new ItemFood(foodItem("cheddar_cheese", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()), 1);
        ModItems.feta_cheese = new ItemFood(foodItem("feta_cheese", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()), 1);
        ModItems.cooked_rice = new ItemFood(foodItem("cooked_rice", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()).usingConvertsTo(Items.BOWL), 1);
        ModItems.tofu = new ItemFood(foodItem("tofu", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()), 1);
        ModItems.flatbread = new ItemFood(foodItem("flatbread", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()), 1);
        ModItems.cheese_ravioli = new ItemFood(foodItem("cheese_ravioli", new FoodProperties.Builder().nutrition(7).saturationModifier(0.1F).build()), 1);
        ModItems.chicken_broth = new ItemFood(foodItem("chicken_broth", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()), 1);
        ModItems.meat_ravioli = new ItemFood(foodItem("meat_ravioli", new FoodProperties.Builder().nutrition(7).saturationModifier(0.1F).build()), 1);
        ModItems.mint_jelly = new ItemFood(foodItem("mint_jelly", new FoodProperties.Builder().nutrition(7).saturationModifier(0.1F).build()), 1);
        ModItems.mint_tea = new ItemFood(foodItem("mint_tea", new FoodProperties.Builder().nutrition(7).saturationModifier(0.1F).build()), 1);
        ModItems.polenta = new ItemFood(foodItem("polenta", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()), 1);
        ModItems.potato_soup = new ItemFood(foodItem("potato_soup", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()), 1);
        ModItems.veggie_ravioli = new ItemFood(foodItem("veggie_ravioli", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()), 1);
        ModItems.yogurt = new ItemFood(foodItem("yogurt", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()), 1);
        ModItems.manchet_bread = new ItemFood(foodItem("manchet_bread", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()), 1);

        // Tier 2 Food Items
        ModItems.lembas_scone = new ItemFood(foodItem("lembas_scone", new FoodProperties.Builder().nutrition(8).saturationModifier(0.25F).build()), 2);
        ModItems.muffin = new ItemFood(foodItem("muffin", new FoodProperties.Builder().nutrition(8).saturationModifier(0.25F).build()), 2);
        ModItems.pottage = new ItemFood(foodItem("pottage", new FoodProperties.Builder().nutrition(9).saturationModifier(0.25F).build()).usingConvertsTo(Items.BOWL), 2);
        ModItems.pasta_plain = new ItemFood(foodItem("pasta_plain", new FoodProperties.Builder().nutrition(11).saturationModifier(0.25F).build()).usingConvertsTo(Items.BOWL), 2);
        ModItems.apple_pie = new ItemFood(foodItem("apple_pie", new FoodProperties.Builder().nutrition(7).saturationModifier(0.25F).build()), 2);
        ModItems.plain_cheesecake = new ItemFood(foodItem("plain_cheesecake", new FoodProperties.Builder().nutrition(11).saturationModifier(0.25F).build()), 2);
        ModItems.baked_salmon = new ItemFood(foodItem("baked_salmon", new FoodProperties.Builder().nutrition(9).saturationModifier(0.25F).build()), 2);
        ModItems.eggdrop_soup = new ItemFood(foodItem("eggdrop_soup", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 2);
        ModItems.fish_n_chips = new ItemFood(foodItem("fish_n_chips", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 2);
        ModItems.pierogi = new ItemFood(foodItem("pierogi", new FoodProperties.Builder().nutrition(11).saturationModifier(0.25F).build()), 2);
        ModItems.veggie_soup = new ItemFood(foodItem("veggie_soup", new FoodProperties.Builder().nutrition(11).saturationModifier(0.25F).build()), 2);
        ModItems.yogurt_with_berries = new ItemFood(foodItem("yogurt_with_berries", new FoodProperties.Builder().nutrition(9).saturationModifier(0.25F).build()).usingConvertsTo(Items.BOWL), 2);
        ModItems.borscht = new ItemFood(foodItem("borscht", new FoodProperties.Builder().nutrition(9).saturationModifier(0.25F).build()), 2);

        // Tier 3 Food items
        ModItems.hand_pie = new ItemFood(foodItem("hand_pie", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 3);
        ModItems.mintchoco_cheesecake = new ItemFood(foodItem("mintchoco_cheesecake", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 3);
        ModItems.schnitzel = new ItemFood(foodItem("schnitzel", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 3);
        ModItems.steak_dinner = new ItemFood(foodItem("steak_dinner", new FoodProperties.Builder().nutrition(12).saturationModifier(0.25F).build()), 3);

        // Cold Biomes
        // Tier 1
        ModItems.squash_soup = new ItemFood(foodItem("squash_soup", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()), 1);
        // Tier 2
        ModItems.cabochis = new ItemFood(foodItem("cabochis", new FoodProperties.Builder().nutrition(11).saturationModifier(0.25F).build()).usingConvertsTo(Items.BOWL), 2);
        ModItems.veggie_quiche = new ItemFood(foodItem("veggie_quiche", new FoodProperties.Builder().nutrition(7).saturationModifier(0.25F).build()), 2);
        // Tier 3
        ModItems.lamb_stew = new ItemFood(foodItem("lamb_stew", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()).usingConvertsTo(Items.BOWL), 3);
        ModItems.fish_dinner = new ItemFood(foodItem("fish_dinner", new FoodProperties.Builder().nutrition(12).saturationModifier(0.25F).build()), 3);

        // Hot Humid Biomes
        // Tier 1
        ModItems.pea_soup = new ItemFood(foodItem("pea_soup", new FoodProperties.Builder().nutrition(7).saturationModifier(0.1F).build()), 1);
        // Tier 2
        ModItems.rice_ball = new ItemFood(foodItem("rice_ball", new FoodProperties.Builder().nutrition(9).saturationModifier(0.25F).build()), 2);
        ModItems.mutton_dinner = new ItemFood(foodItem("mutton_dinner", new FoodProperties.Builder().nutrition(8).saturationModifier(0.25F).build()), 2);
        // Tier 3
        ModItems.sushi_roll = new ItemFood(foodItem("sushi_roll", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 3);
        ModItems.ramen = new ItemFood(foodItem("ramen", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 3);
        ModItems.fried_rice = new ItemFood(foodItem("fried_rice", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 3);

        // Temperate Biomes
        // Tier 1
        ModItems.corn_chowder = new ItemFood(foodItem("corn_chowder", new FoodProperties.Builder().nutrition(7).saturationModifier(0.1F).build()), 1);
        ModItems.tortillas = new ItemFood(foodItem("tortillas", new FoodProperties.Builder().nutrition(6).saturationModifier(0.1F).build()), 1);
        // Tier 2
        ModItems.pasta_tomato = new ItemFood(foodItem("pasta_tomato", new FoodProperties.Builder().nutrition(11).saturationModifier(0.25F).build()).usingConvertsTo(Items.BOWL), 2);
        ModItems.cheese_pizza = new ItemFood(foodItem("cheese_pizza", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 2);
        // Tier 3
        ModItems.eggplant_dolma = new ItemFood(foodItem("eggplant_dolma", new FoodProperties.Builder().nutrition(12).saturationModifier(0.25F).build()), 3);
        ModItems.stuffed_pita = new ItemFood(foodItem("stuffed_pita", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 3);
        ModItems.mushroom_pizza = new ItemFood(foodItem("mushroom_pizza", new FoodProperties.Builder().nutrition(12).saturationModifier(0.25F).build()), 3);

        // Hot Dry Biomes
        // Tier 1
        ModItems.spicy_grilled_chicken = new ItemFood(foodItem("spicy_grilled_chicken", new FoodProperties.Builder().nutrition(7).saturationModifier(0.1F).build()), 1);
        // Tier 2
        ModItems.pepper_hummus = new ItemFood(foodItem("pepper_hummus", new FoodProperties.Builder().nutrition(9).saturationModifier(0.25F).build()), 2);
        ModItems.kebab = new ItemFood(foodItem("kebab", new FoodProperties.Builder().nutrition(8).saturationModifier(0.25F).build()), 2);
        // Tier 3
        ModItems.pita_hummus = new ItemFood(foodItem("pita_hummus", new FoodProperties.Builder().nutrition(12).saturationModifier(0.25F).build()), 3);
        ModItems.spicy_eggplant = new ItemFood(foodItem("spicy_eggplant", new FoodProperties.Builder().nutrition(12).saturationModifier(0.25F).build()), 3);

        // Require trading
        // Tier 2
        ModItems.congee = new ItemFood(foodItem("congee", new FoodProperties.Builder().nutrition(9).saturationModifier(0.25F).build()).usingConvertsTo(Items.BOWL), 2);
        ModItems.kimchi = new ItemFood(foodItem("kimchi", new FoodProperties.Builder().nutrition(11).saturationModifier(0.25F).build()), 2);
        // Tier 3
        ModItems.stew_trencher = new ItemFood(foodItem("stew_trencher", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 3);
        ModItems.stuffed_pepper = new ItemFood(foodItem("stuffed_pepper", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 3);
        ModItems.tacos = new ItemFood(foodItem("tacos", new FoodProperties.Builder().nutrition(13).saturationModifier(0.25F).build()), 3);

        // Just dough
        ModItems.muffin_dough = new Item((propertiesForItem("muffin_dough")));
        ModItems.manchet_dough = new Item((propertiesForItem("manchet_dough")));
        ModItems.raw_noodle = new Item((propertiesForItem("raw_noodle")));
        ModItems.butter = new Item((propertiesForItem("butter")));
        ModItems.cornmeal = new Item((propertiesForItem("cornmeal")));
        ModItems.creamcheese = new Item((propertiesForItem("creamcheese")));
        ModItems.soysauce = new Item((propertiesForItem("soysauce")));

        ModItems.large_empty_bottle = new ItemLargeBottle((propertiesForItem("large_empty_bottle")));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "large_empty_bottle"), ModItems.large_empty_bottle);
        ModItems.large_milk_bottle = new ItemLargeBottle((propertiesForItem("large_milk_bottle").craftRemainder(ModItems.large_empty_bottle)));
        ModItems.large_water_bottle = new ItemLargeBottle((propertiesForItem("large_water_bottle").craftRemainder(ModItems.large_empty_bottle)));
        ModItems.large_soy_milk_bottle = new ItemLargeBottle((propertiesForItem("large_soy_milk_bottle").craftRemainder(ModItems.large_empty_bottle)));

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "supplychestdeployer"), ModItems.supplyChest);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "scan_analyzer"), ModItems.scanAnalyzer);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "scepterpermission"), ModItems.permTool);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "scepterguard"), ModItems.scepterGuard);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "banner_rally_guards"), ModItems.bannerRallyGuards);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "supplycampdeployer"), ModItems.supplyCamp);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "ancienttome"), ModItems.ancientTome);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "chiefsword"), ModItems.chiefSword);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "clipboard"), ModItems.clipboard);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "compost"), ModItems.compost);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "resourcescroll"), ModItems.resourceScroll);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "iron_scimitar"), ModItems.scimitar);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "scepterlumberjack"), ModItems.scepterLumberjack);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pharaoscepter"), ModItems.pharaoscepter);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "firearrow"), ModItems.firearrow);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "scepterbeekeeper"), ModItems.scepterBeekeeper);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "mistletoe"), ModItems.mistletoe);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "spear"), ModItems.spear);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "questlog"), ModItems.questLog);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "colonymap"), ModItems.colonyMap);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "assistanthammer_gold"), ModItems.assistantHammer_Gold);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "assistanthammer_iron"), ModItems.assistantHammer_Iron);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "assistanthammer_diamond"), ModItems.assistantHammer_Diamond);

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "bread_dough"), ModItems.breadDough);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "cookie_dough"), ModItems.cookieDough);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "cake_batter"), ModItems.cakeBatter);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "raw_pumpkin_pie"), ModItems.rawPumpkinPie);

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "milky_bread"), ModItems.milkyBread);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sugary_bread"), ModItems.sugaryBread);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "golden_bread"), ModItems.goldenBread);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "chorus_bread"), ModItems.chorusBread);

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "adventure_token"), ModItems.adventureToken);

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pirate_hat"), ModItems.pirateHelmet_1);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pirate_top"), ModItems.pirateChest_1);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pirate_leggins"), ModItems.pirateLegs_1);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pirate_boots"), ModItems.pirateBoots_1);

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pirate_cap"), ModItems.pirateHelmet_2);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pirate_chest"), ModItems.pirateChest_2);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pirate_legs"), ModItems.pirateLegs_2);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pirate_shoes"), ModItems.pirateBoots_2);

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "plate_armor_helmet"), ModItems.plateArmorHelmet);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "plate_armor_chest"), ModItems.plateArmorChest);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "plate_armor_legs"), ModItems.plateArmorLegs);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "plate_armor_boots"), ModItems.plateArmorBoots);


        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "santa_hat"), ModItems.santaHat);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, IRON_GATE), ModItems.irongate);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, WOODEN_GATE), ModItems.woodgate);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "colony_banner"), ModItems.flagBanner);


        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sifter_mesh_string"), ModItems.sifterMeshString);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sifter_mesh_flint"), ModItems.sifterMeshFlint);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sifter_mesh_iron"), ModItems.sifterMeshIron);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sifter_mesh_diamond"), ModItems.sifterMeshDiamond);

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "magicpotion"), ModItems.magicpotion);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "build_goggles"), ModItems.buildGoggles);

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "butter"), ModItems.butter);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "cabochis"), ModItems.cabochis);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "cheddar_cheese"), ModItems.cheddar_cheese);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "congee"), ModItems.congee);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "cooked_rice"), ModItems.cooked_rice);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "eggplant_dolma"), ModItems.eggplant_dolma);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "feta_cheese"), ModItems.feta_cheese);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "flatbread"), ModItems.flatbread);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "hand_pie"), ModItems.hand_pie);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "lamb_stew"), ModItems.lamb_stew);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "lembas_scone"), ModItems.lembas_scone);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "manchet_bread"), ModItems.manchet_bread);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "manchet_dough"), ModItems.manchet_dough);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "muffin"), ModItems.muffin);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "muffin_dough"), ModItems.muffin_dough);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pasta_plain"), ModItems.pasta_plain);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pasta_tomato"), ModItems.pasta_tomato);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pepper_hummus"), ModItems.pepper_hummus);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pita_hummus"), ModItems.pita_hummus);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pottage"), ModItems.pottage);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "raw_noodle"), ModItems.raw_noodle);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "rice_ball"), ModItems.rice_ball);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "stew_trencher"), ModItems.stew_trencher);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "stuffed_pepper"), ModItems.stuffed_pepper);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "stuffed_pita"), ModItems.stuffed_pita);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sushi_roll"), ModItems.sushi_roll);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "tofu"), ModItems.tofu);

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "cheese_ravioli"), ModItems.cheese_ravioli);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "chicken_broth"), ModItems.chicken_broth);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "corn_chowder"), ModItems.corn_chowder);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "spicy_grilled_chicken"), ModItems.spicy_grilled_chicken);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "kebab"), ModItems.kebab);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "meat_ravioli"), ModItems.meat_ravioli);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "mint_jelly"), ModItems.mint_jelly);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "mint_tea"), ModItems.mint_tea);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pea_soup"), ModItems.pea_soup);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "polenta"), ModItems.polenta);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "potato_soup"), ModItems.potato_soup);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "squash_soup"), ModItems.squash_soup);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "veggie_ravioli"), ModItems.veggie_ravioli);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "yogurt"), ModItems.yogurt);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "baked_salmon"), ModItems.baked_salmon);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "eggdrop_soup"), ModItems.eggdrop_soup);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "fish_n_chips"), ModItems.fish_n_chips);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "kimchi"), ModItems.kimchi);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pierogi"), ModItems.pierogi);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "veggie_quiche"), ModItems.veggie_quiche);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "veggie_soup"), ModItems.veggie_soup);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "yogurt_with_berries"), ModItems.yogurt_with_berries);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "borscht"), ModItems.borscht);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "fish_dinner"), ModItems.fish_dinner);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "mutton_dinner"), ModItems.mutton_dinner);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "ramen"), ModItems.ramen);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "fried_rice"), ModItems.fried_rice);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "schnitzel"), ModItems.schnitzel);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "steak_dinner"), ModItems.steak_dinner);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "tacos"), ModItems.tacos);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "cornmeal"), ModItems.cornmeal);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "creamcheese"), ModItems.creamcheese);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "soysauce"), ModItems.soysauce);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "tortillas"), ModItems.tortillas);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "apple_pie"), ModItems.apple_pie);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "cheese_pizza"), ModItems.cheese_pizza);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "mushroom_pizza"), ModItems.mushroom_pizza);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "plain_cheesecake"), ModItems.plain_cheesecake);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "mintchoco_cheesecake"), ModItems.mintchoco_cheesecake);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "spicy_eggplant"), ModItems.spicy_eggplant);

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "large_water_bottle"), ModItems.large_water_bottle);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "large_milk_bottle"), ModItems.large_milk_bottle);
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "large_soy_milk_bottle"), ModItems.large_soy_milk_bottle);

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "barbarianegg"), new SpawnEggItem(propertiesForItem("barbarianegg").spawnEgg(ModEntities.CAMP_BARBARIAN)));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "barbarcheregg"), new SpawnEggItem(propertiesForItem("barbarcheregg").spawnEgg(ModEntities.CAMP_ARCHERBARBARIAN)));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "barbchiefegg"), new SpawnEggItem(propertiesForItem("barbchiefegg").spawnEgg(ModEntities.CAMP_CHIEFBARBARIAN)));

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pirateegg"), new SpawnEggItem(propertiesForItem("pirateegg").spawnEgg(ModEntities.CAMP_PIRATE)));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "piratearcheregg"), new SpawnEggItem(propertiesForItem("piratearcheregg").spawnEgg(ModEntities.CAMP_ARCHERPIRATE)));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "piratecaptainegg"), new SpawnEggItem(propertiesForItem("piratecaptainegg").spawnEgg(ModEntities.CAMP_CHIEFPIRATE)));

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "mummyegg"), new SpawnEggItem(propertiesForItem("mummyegg").spawnEgg(ModEntities.CAMP_MUMMY)));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "mummyarcheregg"), new SpawnEggItem(propertiesForItem("mummyarcheregg").spawnEgg(ModEntities.CAMP_ARCHERMUMMY)));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "pharaoegg"), new SpawnEggItem(propertiesForItem("pharaoegg").spawnEgg(ModEntities.CAMP_PHARAO)));

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "shieldmaidenegg"), new SpawnEggItem(propertiesForItem("shieldmaidenegg").spawnEgg(ModEntities.CAMP_SHIELDMAIDEN)));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "norsemenarcheregg"), new SpawnEggItem(propertiesForItem("norsemenarcheregg").spawnEgg(ModEntities.CAMP_NORSEMEN_ARCHER)));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "norsemenchiefegg"), new SpawnEggItem(propertiesForItem("norsemenchiefegg").spawnEgg(ModEntities.CAMP_NORSEMEN_CHIEF)));

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "amazonegg"), new SpawnEggItem(propertiesForItem("amazonegg").spawnEgg(ModEntities.CAMP_AMAZON)));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "amazonspearmanegg"), new SpawnEggItem(propertiesForItem("amazonspearmanegg").spawnEgg(ModEntities.CAMP_AMAZONSPEARMAN)));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "amazonchiefegg"), new SpawnEggItem(propertiesForItem("amazonchiefegg").spawnEgg(ModEntities.CAMP_AMAZONCHIEF)));

        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "drownedpirateegg"), new SpawnEggItem(propertiesForItem("drownedpirateegg").spawnEgg(ModEntities.CAMP_DROWNED_PIRATE)));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "drownedpiratearcheregg"), new SpawnEggItem(propertiesForItem("drownedpiratearcheregg").spawnEgg(ModEntities.CAMP_DROWNED_ARCHERPIRATE)));
        Registry.register(registry, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "drownedpiratecaptainegg"), new SpawnEggItem(propertiesForItem("drownedpiratecaptainegg").spawnEgg(ModEntities.CAMP_DROWNED_CHIEFPIRATE)));
    }

    private static Item.Properties propertiesForItem(final String name)
    {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name)));
    }

    /**
     * Like {@link #propertiesForItem}, but for food items: also records the {@link FoodProperties} against the
     * item's name in {@link ModItems#registerFoodProperties}, so that datagen can look the same values back up
     * later without needing the item's data components to be bound.
     */
    private static Item.Properties foodItem(final String name, final FoodProperties food)
    {
        ModItems.registerFoodProperties(name, food);
        return propertiesForItem(name).food(food);
    }

    /**
     * Armour materials. Since 26.1 {@link ArmorMaterial} is a plain record rather than a registered object; the layer
     * textures moved to {@link ModEquipmentAssets} and the repair ingredient became an item tag.
     */
    public static final ArmorMaterial SANTA_HAT = new ArmorMaterial(
      // Base durability; every santa hat overrides this with its own explicit durability.
      0,
      // Determines the defense value of this armor material, depending on what armor piece it is.
      Util.make(new EnumMap<>(ArmorType.class), map -> {
          map.put(ArmorType.BOOTS, 0);
          map.put(ArmorType.LEGGINGS, 0);
          map.put(ArmorType.CHESTPLATE, 0);
          map.put(ArmorType.HELMET, 0);
      }),
      500,
      SoundEvents.ARMOR_EQUIP_LEATHER,
      0,
      0,
      ModTags.repairsNothing,
      ModEquipmentAssets.SANTA_HAT
    );

    public static final ArmorMaterial PLATE_ARMOR = new ArmorMaterial(
      0,
      Util.make(new EnumMap<>(ArmorType.class), map -> {
          map.put(ArmorType.BOOTS, 3);
          map.put(ArmorType.LEGGINGS, 6);
          map.put(ArmorType.CHESTPLATE, 8);
          map.put(ArmorType.HELMET, 3);
      }),
      37,
      SoundEvents.ARMOR_EQUIP_IRON,
      0,
      0,
      ItemTags.REPAIRS_IRON_ARMOR,
      ModEquipmentAssets.PLATE_ARMOR
    );

    public static final ArmorMaterial GOGGLES = new ArmorMaterial(
      0,
      Util.make(new EnumMap<>(ArmorType.class), map -> {
          map.put(ArmorType.BOOTS, 0);
          map.put(ArmorType.LEGGINGS, 0);
          map.put(ArmorType.CHESTPLATE, 0);
          map.put(ArmorType.HELMET, 0);
      }),
      20,
      SoundEvents.ARMOR_EQUIP_LEATHER,
      0,
      0,
      ModTags.repairsNothing,
      ModEquipmentAssets.BUILD_GOGGLES
    );

    public static final ArmorMaterial PIRATE_ARMOR_1 = new ArmorMaterial(
      0,
      Util.make(new EnumMap<>(ArmorType.class), map -> {
          map.put(ArmorType.BOOTS, 2);
          map.put(ArmorType.LEGGINGS, 5);
          map.put(ArmorType.CHESTPLATE, 6);
          map.put(ArmorType.HELMET, 2);
      }),
      5,
      SoundEvents.ARMOR_EQUIP_LEATHER,
      0,
      0,
      ItemTags.REPAIRS_DIAMOND_ARMOR,
      ModEquipmentAssets.PIRATE
    );

    public static final ArmorMaterial PIRATE_ARMOR_2 = new ArmorMaterial(
      0,
      Util.make(new EnumMap<>(ArmorType.class), map -> {
          map.put(ArmorType.BOOTS, 3);
          map.put(ArmorType.LEGGINGS, 6);
          map.put(ArmorType.CHESTPLATE, 8);
          map.put(ArmorType.HELMET, 3);
      }),
      5,
      SoundEvents.ARMOR_EQUIP_LEATHER,
      2,
      0,
      ItemTags.REPAIRS_DIAMOND_ARMOR,
      ModEquipmentAssets.PIRATE_2
    );
}

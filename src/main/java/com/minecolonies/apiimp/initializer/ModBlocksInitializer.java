package com.minecolonies.apiimp.initializer;

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.items.ModTags;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.blocks.*;
import com.minecolonies.core.blocks.decorative.BlockColonyFlagBanner;
import com.minecolonies.core.blocks.decorative.BlockColonyFlagWallBanner;
import com.minecolonies.core.blocks.decorative.BlockConstructionTape;
import com.minecolonies.core.blocks.decorative.BlockGate;
import com.minecolonies.core.blocks.huts.*;
import com.minecolonies.core.blocks.schematic.BlockWaypoint;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.List;

import static com.minecolonies.api.blocks.decorative.AbstractBlockGate.IRON_GATE;
import static com.minecolonies.api.blocks.decorative.AbstractBlockGate.WOODEN_GATE;
import static com.minecolonies.core.blocks.MinecoloniesCropBlock.*;
import static com.minecolonies.core.blocks.MinecoloniesFarmland.FARMLAND;
import static com.minecolonies.core.blocks.MinecoloniesFarmland.FLOODED_FARMLAND;

/**
 * This class deals with the initialization of blocks and their items.
 */
@EventBusSubscriber(modid = Constants.MOD_ID)
public final class ModBlocksInitializer
{

    private ModBlocksInitializer()
    {
        throw new IllegalStateException("Tried to initialize: ModBlockInitializer but this is a Utility class.");
    }

    @SubscribeEvent
    public static void registerBlocks(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(Registries.BLOCK))
        {
            ModBlocksInitializer.init(event.getRegistry(Registries.BLOCK));
        }
    }

    /**
     * Initializes {@link ModBlocks} with the block instances.
     *
     * @param registry The registry to register the new blocks.
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public static void init(final Registry<Block> registry)
    {
        ModBlocks.blockHutBaker = registerBlock(registry, "blockhutbaker", () -> new BlockHutBaker());
        ModBlocks.blockHutBlacksmith = registerBlock(registry, "blockhutblacksmith", () -> new BlockHutBlacksmith());
        ModBlocks.blockHutBuilder = registerBlock(registry, "blockhutbuilder", () -> new BlockHutBuilder());
        ModBlocks.blockHutHome = registerBlock(registry, "blockhutcitizen", () -> new BlockHutCitizen());
        ModBlocks.blockHutDeliveryman = registerBlock(registry, "blockhutdeliveryman", () -> new BlockHutDeliveryman());
        ModBlocks.blockHutFarmer = registerBlock(registry, "blockhutfarmer", () -> new BlockHutFarmer());
        ModBlocks.blockScarecrow = registerBlock(registry, "blockhutfield", () -> new BlockScarecrow());
        ModBlocks.blockHutFisherman = registerBlock(registry, "blockhutfisherman", () -> new BlockHutFisherman());
        ModBlocks.blockHutGuardTower = registerBlock(registry, "blockhutguardtower", () -> new BlockHutGuardTower());
        ModBlocks.blockHutLumberjack = registerBlock(registry, "blockhutlumberjack", () -> new BlockHutLumberjack());
        ModBlocks.blockHutMiner = registerBlock(registry, "blockhutminer", () -> new BlockHutMiner());
        ModBlocks.blockHutStonemason = registerBlock(registry, "blockhutstonemason", () -> new BlockHutStonemason());
        ModBlocks.blockHutTownHall = registerBlock(registry, "blockhuttownhall", () -> new BlockHutTownHall());
        ModBlocks.blockHutWareHouse = registerBlock(registry, "blockhutwarehouse", () -> new BlockHutWareHouse());
        ModBlocks.blockHutShepherd = registerBlock(registry, "blockhutshepherd", () -> new BlockHutShepherd());
        ModBlocks.blockHutCowboy = registerBlock(registry, "blockhutcowboy", () -> new BlockHutCowboy());
        ModBlocks.blockHutSwineHerder = registerBlock(registry, "blockhutswineherder", () -> new BlockHutSwineHerder());
        ModBlocks.blockHutChickenHerder = registerBlock(registry, "blockhutchickenherder", () -> new BlockHutChickenHerder());
        ModBlocks.blockHutBarracks = registerBlock(registry, "blockhutbarracks", () -> new BlockHutBarracks());
        ModBlocks.blockHutBarracksTower = registerBlock(registry, "blockhutbarrackstower", () -> new BlockHutBarracksTower());
        ModBlocks.blockHutCook = registerBlock(registry, "blockhutcook", () -> new BlockHutCook());
        ModBlocks.blockHutSmeltery = registerBlock(registry, "blockhutsmeltery", () -> new BlockHutSmeltery());
        ModBlocks.blockHutComposter = registerBlock(registry, "blockhutcomposter", () -> new BlockHutComposter());
        ModBlocks.blockHutLibrary = registerBlock(registry, "blockhutlibrary", () -> new BlockHutLibrary());
        ModBlocks.blockHutArchery = registerBlock(registry, "blockhutarchery", () -> new BlockHutArchery());
        ModBlocks.blockHutSawmill = registerBlock(registry, "blockhutsawmill", () -> new BlockHutSawmill());
        ModBlocks.blockHutCombatAcademy = registerBlock(registry, "blockhutcombatacademy", () -> new BlockHutCombatAcademy());
        ModBlocks.blockHutStoneSmeltery = registerBlock(registry, "blockhutstonesmeltery", () -> new BlockHutStoneSmeltery());
        ModBlocks.blockHutCrusher = registerBlock(registry, "blockhutcrusher", () -> new BlockHutCrusher());
        ModBlocks.blockHutSifter = registerBlock(registry, "blockhutsifter", () -> new BlockHutSifter());
        ModBlocks.blockHutFlorist = registerBlock(registry, "blockhutflorist", () -> new BlockHutFlorist());
        ModBlocks.blockHutEnchanter = registerBlock(registry, "blockhutenchanter", () -> new BlockHutEnchanter());
        ModBlocks.blockHutUniversity = registerBlock(registry, "blockhutuniversity", () -> new BlockHutUniversity());
        ModBlocks.blockHutHospital = registerBlock(registry, "blockhuthospital", () -> new BlockHutHospital());
        ModBlocks.blockHutSchool = registerBlock(registry, "blockhutschool", () -> new BlockHutSchool());
        ModBlocks.blockHutGlassblower = registerBlock(registry, "blockhutglassblower", () -> new BlockHutGlassblower());
        ModBlocks.blockHutDyer = registerBlock(registry, "blockhutdyer", () -> new BlockHutDyer());
        ModBlocks.blockHutFletcher = registerBlock(registry, "blockhutfletcher", () -> new BlockHutFletcher());
        ModBlocks.blockHutMechanic = registerBlock(registry, "blockhutmechanic", () -> new BlockHutMechanic());
        ModBlocks.blockHutTavern = registerBlock(registry, "blockhuttavern", () -> new BlockHutTavern());
        ModBlocks.blockHutPlantation = registerBlock(registry, "blockhutplantation", () -> new BlockHutPlantation());
        ModBlocks.blockPlantationField = registerBlock(registry, "blockhutplantationfield", () -> new BlockPlantationField());
        ModBlocks.blockHutRabbitHutch = registerBlock(registry, "blockhutrabbithutch", () -> new BlockHutRabbitHutch());
        ModBlocks.blockHutConcreteMixer = registerBlock(registry, "blockhutconcretemixer", () -> new BlockHutConcreteMixer());
        ModBlocks.blockHutBeekeeper = registerBlock(registry, "blockhutbeekeeper", () -> new BlockHutBeekeeper());
        ModBlocks.blockHutMysticalSite = registerBlock(registry, "blockhutmysticalsite", () -> new BlockHutMysticalSite());
        ModBlocks.blockHutGraveyard = registerBlock(registry, "blockhutgraveyard", () -> new BlockHutGraveyard());
        ModBlocks.blockHutNetherWorker = registerBlock(registry, "blockhutnetherworker", () -> new BlockHutNetherWorker());
        ModBlocks.blockHutAlchemist = registerBlock(registry, "blockhutalchemist", () -> new BlockHutAlchemist());
        ModBlocks.blockHutKitchen = registerBlock(registry, "blockhutkitchen", () -> new BlockHutKitchen());
        ModBlocks.blockHutGateHouse = registerBlock(registry, "blockhutgatehouse", () -> new BlockHutGateHouse());
        ModBlocks.blockHutStable = registerBlock(registry, "blockhutstable", () -> new BlockHutStable());

        ModBlocks.blockConstructionTape = registerBlock(registry, "blockconstructiontape", () -> new BlockConstructionTape());
        ModBlocks.blockRack = registerBlock(registry, "blockminecoloniesrack", () -> new BlockMinecoloniesRack());
        ModBlocks.blockGrave = registerBlock(registry, "blockminecoloniesgrave", () -> new BlockMinecoloniesGrave());
        ModBlocks.blockNamedGrave = registerBlock(registry, "blockminecoloniesnamedgrave", () -> new BlockMinecoloniesNamedGrave());
        ModBlocks.blockWayPoint = registerBlock(registry, "blockwaypoint", () -> new BlockWaypoint());
        ModBlocks.blockPostBox = registerBlock(registry, "blockpostbox", () -> new BlockPostBox());
        ModBlocks.blockStash = registerBlock(registry, "blockstash", () -> new BlockStash());
        ModBlocks.blockDecorationPlaceholder = registerBlock(registry, "decorationcontroller", () -> new BlockDecorationController());
        ModBlocks.blockBarrel = registerBlock(registry, "barrel_block", () -> new BlockBarrel());
        ModBlocks.blockCompostedDirt = registerBlock(registry, "composted_dirt", () -> new BlockCompostedDirt());
        ModBlocks.blockColonyBanner = registerBlock(registry, "colony_banner", () -> new BlockColonyFlagBanner());
        ModBlocks.blockColonyWallBanner = registerBlock(registry, "colony_wall_banner", () -> new BlockColonyFlagWallBanner());
        ModBlocks.blockIronGate = registerBlock(registry, "gate_iron", () -> new BlockGate(IRON_GATE, 10f, 6, 8));
        ModBlocks.blockWoodenGate = registerBlock(registry, "gate_wood", () -> new BlockGate(WOODEN_GATE, 7f, 6, 5));
        ModBlocks.farmland = registerBlock(registry, "farmland", () -> new MinecoloniesFarmland(FARMLAND, false, 15.0));
        ModBlocks.floodedFarmland = registerBlock(registry, "floodedfarmland", () -> new MinecoloniesFarmland(FLOODED_FARMLAND, true, 13.0));
        ModBlocks.blockColonySign = registerBlock(registry, "colonysign", () -> new BlockColonySign());

        // Could in the future add alternative versions of these crops that can be planted by the player and grow at a slower rate.
        ModBlocks.blockBellPepper = registerBlock(registry, "bell_pepper", () -> new MinecoloniesCropBlock(BELL_PEPPER, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS), ModTags.temperateBiomes));
        ModBlocks.blockCabbage = registerBlock(registry, "cabbage", () -> new MinecoloniesCropBlock(CABBAGE, ModBlocks.farmland, List.of(Blocks.FERN), ModTags.coldBiomes));
        ModBlocks.blockChickpea = registerBlock(registry, "chickpea", () -> new MinecoloniesCropBlock(CHICKPEA, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.DEAD_BUSH), ModTags.dryBiomes));
        ModBlocks.blockDurum = registerBlock(registry, "durum", () -> new MinecoloniesCropBlock(DURUM, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS), null));
        ModBlocks.blockEggplant = registerBlock(registry, "eggplant", () -> new MinecoloniesCropBlock(EGGPLANT, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS), null));
        ModBlocks.blockGarlic = registerBlock(registry, "garlic", () -> new MinecoloniesCropBlock(GARLIC, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS), null));
        ModBlocks.blockOnion = registerBlock(registry, "onion", () -> new MinecoloniesCropBlock(ONION, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS), null));
        ModBlocks.blockSoyBean = registerBlock(registry, "soybean", () -> new MinecoloniesCropBlock(SOYBEAN, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.FERN), ModTags.humidBiomes));
        ModBlocks.blockTomato = registerBlock(registry, "tomato", () -> new MinecoloniesCropBlock(TOMATO, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS), ModTags.temperateBiomes));
        ModBlocks.blockRice = registerBlock(registry, "rice", () -> new MinecoloniesCropBlock(RICE, ModBlocks.floodedFarmland, List.of(Blocks.SEAGRASS, Blocks.SMALL_DRIPLEAF), ModTags.humidBiomes));

        ModBlocks.blockButternutSquash = registerBlock(registry, "butternut_squash", () -> new MinecoloniesCropBlock(BUTTERNUT_SQUASH, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS), ModTags.coldBiomes));
        ModBlocks.blockCorn = registerBlock(registry, "corn", () -> new MinecoloniesCropBlock(CORN, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS), ModTags.temperateBiomes));
        ModBlocks.blockMint = registerBlock(registry, "mint", () -> new MinecoloniesCropBlock(MINT, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS), null));
        ModBlocks.blockNetherPepper = registerBlock(registry, "nether_pepper", () -> new MinecoloniesCropBlock(NETHER_PEPPER, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS), ModTags.dryBiomes));
        ModBlocks.blockPeas = registerBlock(registry, "peas", () -> new MinecoloniesCropBlock(PEAS, ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS), ModTags.humidBiomes));

        ModBlocks.blockSimpleQuarry = registerBlock(registry, "simplequarry", () -> new SimpleQuarry());
        ModBlocks.blockMediumQuarry = registerBlock(registry, "mediumquarry", () -> new MediumQuarry());
        //ModBlocks.blockLargeQuarry = new LargeQuarry().registerBlock(registry);
    }

    private static <B extends Block> B registerBlock(final Registry<Block> registry, final String name, final java.util.function.Supplier<B> factory)
    {
        final Identifier id = Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
        return Registry.register(registry, id, com.minecolonies.api.util.RegistryConstructionContext.constructBlock(id, factory));
    }

    private static Item.Properties propertiesForItem(final String name)
    {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name))).useBlockDescriptionPrefix();
    }

    @SubscribeEvent
    public static void registerItems(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(Registries.ITEM))
        {
            ModBlocksInitializer.registerBlockItem(event.getRegistry(Registries.ITEM));
        }
    }

    /**
     * Initializes the registry with the relevant item produced by the relevant blocks.
     *
     * @param registry The item registry to add the items too.
     */
    public static void registerBlockItem(final Registry<Item> registry)
    {
        ModBlocks.blockHutBaker.registerBlockItem(registry, propertiesForItem("blockhutbaker"));
        ModBlocks.blockHutBlacksmith.registerBlockItem(registry, propertiesForItem("blockhutblacksmith"));
        ModBlocks.blockHutBuilder.registerBlockItem(registry, propertiesForItem("blockhutbuilder"));
        ModBlocks.blockHutHome.registerBlockItem(registry, propertiesForItem("blockhutcitizen"));
        ModBlocks.blockHutDeliveryman.registerBlockItem(registry, propertiesForItem("blockhutdeliveryman"));
        ModBlocks.blockHutFarmer.registerBlockItem(registry, propertiesForItem("blockhutfarmer"));
        ModBlocks.blockScarecrow.registerBlockItem(registry, propertiesForItem("blockhutfield"));
        ModBlocks.blockHutFisherman.registerBlockItem(registry, propertiesForItem("blockhutfisherman"));
        ModBlocks.blockHutGuardTower.registerBlockItem(registry, propertiesForItem("blockhutguardtower"));
        ModBlocks.blockHutLumberjack.registerBlockItem(registry, propertiesForItem("blockhutlumberjack"));
        ModBlocks.blockHutMiner.registerBlockItem(registry, propertiesForItem("blockhutminer"));
        ModBlocks.blockHutStonemason.registerBlockItem(registry, propertiesForItem("blockhutstonemason"));
        ModBlocks.blockHutTownHall.registerBlockItem(registry, propertiesForItem("blockhuttownhall"));
        ModBlocks.blockHutWareHouse.registerBlockItem(registry, propertiesForItem("blockhutwarehouse"));
        ModBlocks.blockHutShepherd.registerBlockItem(registry, propertiesForItem("blockhutshepherd"));
        ModBlocks.blockHutCowboy.registerBlockItem(registry, propertiesForItem("blockhutcowboy"));
        ModBlocks.blockHutSwineHerder.registerBlockItem(registry, propertiesForItem("blockhutswineherder"));
        ModBlocks.blockHutChickenHerder.registerBlockItem(registry, propertiesForItem("blockhutchickenherder"));
        ModBlocks.blockHutBarracksTower.registerBlockItem(registry, propertiesForItem("blockhutbarrackstower"));
        ModBlocks.blockHutBarracks.registerBlockItem(registry, propertiesForItem("blockhutbarracks"));
        ModBlocks.blockHutCook.registerBlockItem(registry, propertiesForItem("blockhutcook"));
        ModBlocks.blockHutSmeltery.registerBlockItem(registry, propertiesForItem("blockhutsmeltery"));
        ModBlocks.blockHutComposter.registerBlockItem(registry, propertiesForItem("blockhutcomposter"));
        ModBlocks.blockHutLibrary.registerBlockItem(registry, propertiesForItem("blockhutlibrary"));
        ModBlocks.blockHutArchery.registerBlockItem(registry, propertiesForItem("blockhutarchery"));
        ModBlocks.blockHutCombatAcademy.registerBlockItem(registry, propertiesForItem("blockhutcombatacademy"));
        ModBlocks.blockHutSawmill.registerBlockItem(registry, propertiesForItem("blockhutsawmill"));
        ModBlocks.blockHutStoneSmeltery.registerBlockItem(registry, propertiesForItem("blockhutstonesmeltery"));
        ModBlocks.blockHutCrusher.registerBlockItem(registry, propertiesForItem("blockhutcrusher"));
        ModBlocks.blockHutSifter.registerBlockItem(registry, propertiesForItem("blockhutsifter"));
        ModBlocks.blockHutFlorist.registerBlockItem(registry, propertiesForItem("blockhutflorist"));
        ModBlocks.blockHutEnchanter.registerBlockItem(registry, propertiesForItem("blockhutenchanter"));
        ModBlocks.blockHutUniversity.registerBlockItem(registry, propertiesForItem("blockhutuniversity"));
        ModBlocks.blockHutHospital.registerBlockItem(registry, propertiesForItem("blockhuthospital"));
        ModBlocks.blockHutSchool.registerBlockItem(registry, propertiesForItem("blockhutschool"));
        ModBlocks.blockHutGlassblower.registerBlockItem(registry, propertiesForItem("blockhutglassblower"));
        ModBlocks.blockHutDyer.registerBlockItem(registry, propertiesForItem("blockhutdyer"));
        ModBlocks.blockHutFletcher.registerBlockItem(registry, propertiesForItem("blockhutfletcher"));
        ModBlocks.blockHutMechanic.registerBlockItem(registry, propertiesForItem("blockhutmechanic"));
        ModBlocks.blockHutTavern.registerBlockItem(registry, propertiesForItem("blockhuttavern"));
        ModBlocks.blockHutPlantation.registerBlockItem(registry, propertiesForItem("blockhutplantation"));
        ModBlocks.blockPlantationField.registerBlockItem(registry, propertiesForItem("blockhutplantationfield"));
        ModBlocks.blockHutRabbitHutch.registerBlockItem(registry, propertiesForItem("blockhutrabbithutch"));
        ModBlocks.blockHutConcreteMixer.registerBlockItem(registry, propertiesForItem("blockhutconcretemixer"));
        ModBlocks.blockHutBeekeeper.registerBlockItem(registry, propertiesForItem("blockhutbeekeeper"));
        ModBlocks.blockHutMysticalSite.registerBlockItem(registry, propertiesForItem("blockhutmysticalsite"));
        ModBlocks.blockHutGraveyard.registerBlockItem(registry, propertiesForItem("blockhutgraveyard"));
        ModBlocks.blockHutNetherWorker.registerBlockItem(registry, propertiesForItem("blockhutnetherworker"));
        ModBlocks.blockHutAlchemist.registerBlockItem(registry, propertiesForItem("blockhutalchemist"));
        ModBlocks.blockHutKitchen.registerBlockItem(registry, propertiesForItem("blockhutkitchen"));
        ModBlocks.blockHutGateHouse.registerBlockItem(registry, propertiesForItem("blockhutgatehouse"));
        ModBlocks.blockHutStable.registerBlockItem(registry, propertiesForItem("blockhutstable"));

        ModBlocks.blockConstructionTape.registerBlockItem(registry, propertiesForItem("blockconstructiontape"));
        ModBlocks.blockRack.registerBlockItem(registry, propertiesForItem("blockminecoloniesrack"));
        ModBlocks.blockGrave.registerBlockItem(registry, propertiesForItem("blockminecoloniesgrave"));
        ModBlocks.blockNamedGrave.registerBlockItem(registry, propertiesForItem("blockminecoloniesnamedgrave"));
        ModBlocks.blockWayPoint.registerBlockItem(registry, propertiesForItem("blockwaypoint"));
        ModBlocks.blockBarrel.registerBlockItem(registry, propertiesForItem("barrel_block"));
        ModBlocks.blockPostBox.registerBlockItem(registry, propertiesForItem("blockpostbox"));
        ModBlocks.blockStash.registerBlockItem(registry, propertiesForItem("blockstash"));
        ModBlocks.blockDecorationPlaceholder.registerBlockItem(registry, propertiesForItem("decorationcontroller"));
        ModBlocks.blockCompostedDirt.registerBlockItem(registry, propertiesForItem("composted_dirt"));
        ModBlocks.farmland.registerBlockItem(registry, propertiesForItem("farmland"));
        ModBlocks.floodedFarmland.registerBlockItem(registry, propertiesForItem("floodedfarmland"));
        ModBlocks.blockColonySign.registerBlockItem(registry, propertiesForItem("colonysign"));

        ModBlocks.blockBellPepper.registerBlockItem(registry, propertiesForItem("bell_pepper"));
        ModBlocks.blockCabbage.registerBlockItem(registry, propertiesForItem("cabbage"));
        ModBlocks.blockChickpea.registerBlockItem(registry, propertiesForItem("chickpea"));
        ModBlocks.blockDurum.registerBlockItem(registry, propertiesForItem("durum"));
        ModBlocks.blockEggplant.registerBlockItem(registry, propertiesForItem("eggplant"));
        ModBlocks.blockGarlic.registerBlockItem(registry, propertiesForItem("garlic"));
        ModBlocks.blockOnion.registerBlockItem(registry, propertiesForItem("onion"));
        ModBlocks.blockSoyBean.registerBlockItem(registry, propertiesForItem("soybean"));
        ModBlocks.blockTomato.registerBlockItem(registry, propertiesForItem("tomato"));
        ModBlocks.blockRice.registerBlockItem(registry, propertiesForItem("rice"));
        ModBlocks.blockButternutSquash.registerBlockItem(registry, propertiesForItem("butternut_squash"));
        ModBlocks.blockCorn.registerBlockItem(registry, propertiesForItem("corn"));
        ModBlocks.blockMint.registerBlockItem(registry, propertiesForItem("mint"));
        ModBlocks.blockNetherPepper.registerBlockItem(registry, propertiesForItem("nether_pepper"));
        ModBlocks.blockPeas.registerBlockItem(registry, propertiesForItem("peas"));

        ModBlocks.blockSimpleQuarry.registerBlockItem(registry, propertiesForItem("simplequarry"));
        ModBlocks.blockMediumQuarry.registerBlockItem(registry, propertiesForItem("mediumquarry"));
        //ModBlocks.blockLargeQuarry.registerBlockItem(registry, new Item.Properties());
    }
}

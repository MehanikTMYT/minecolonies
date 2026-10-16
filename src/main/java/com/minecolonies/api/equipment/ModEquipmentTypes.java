package com.minecolonies.api.equipment;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.compatibility.Compatibility;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.translation.ToolTranslationConstants;
import com.minecolonies.apiimp.CommonMinecoloniesAPIImpl;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Tool;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Class used for storing and registering any EquipmentTypes.
 */
public class ModEquipmentTypes
{
    public final static DeferredRegister<EquipmentTypeEntry> DEFERRED_REGISTER = DeferredRegister.create(CommonMinecoloniesAPIImpl.EQUIPMENT_TYPES, Constants.MOD_ID);

    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> none;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> pickaxe;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> shovel;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> axe;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> hoe;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> sword;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> bow;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> fishing_rod;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> shears;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> shield;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> helmet;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> leggings;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> chestplate;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> boots;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> flint_and_steel;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> lead;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> spear;
    public static final DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> crossbow;

    static
    {
        none = register("none",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_NONE))
                       .setIsEquipment((itemStack, equipmentType) -> true)
                       .setEquipmentLevel((itemStack, equipmentType) -> -1)
                   .build());

        pickaxe = register("pickaxe",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_PICKAXE))
                       .setIsEquipment((itemStack, equipmentType) -> canPerformDefaultActions(itemStack, Set.of(ItemAbility.get("pickaxe_dig"))) || Compatibility.isTinkersTool(
                         itemStack,
                         equipmentType))
                       .setEquipmentLevel(ModEquipmentTypes::vanillaToolLevel)
                  .build());

        shovel = register("shovel",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_SHOVEL))
                       .setIsEquipment((itemStack, equipmentType) -> canPerformDefaultActions(itemStack, ItemAbilities.DEFAULT_SHOVEL_ACTIONS) || Compatibility.isTinkersTool(
                         itemStack,
                         equipmentType))
                       .setEquipmentLevel(ModEquipmentTypes::vanillaToolLevel)
                  .build());

        axe = register("axe",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_AXE))
                       .setIsEquipment((itemStack, equipmentType) -> canPerformDefaultActions(itemStack, ItemAbilities.DEFAULT_AXE_ACTIONS) || Compatibility.isTinkersTool(itemStack,
                         equipmentType))
                       .setEquipmentLevel(ModEquipmentTypes::vanillaToolLevel)
                  .build());

        hoe = register("hoe",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_HOE))
                       .setIsEquipment((itemStack, equipmentType) -> canPerformDefaultActions(itemStack, ItemAbilities.DEFAULT_HOE_ACTIONS) || Compatibility.isTinkersTool(itemStack,
                         equipmentType))
                       .setEquipmentLevel(ModEquipmentTypes::vanillaToolLevel)
                  .build());

        sword = register("sword",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_SWORD))
                       .setIsEquipment((itemStack, equipmentType) -> canPerformDefaultActions(itemStack, Set.of(ItemAbility.get("sword_dig"), ItemAbility.get("sword_sweep")))
                                                                     || Compatibility.isTinkersWeapon(itemStack)
                                                                     || Compatibility.isCustomWeapon(itemStack))
                       .setEquipmentLevel(ModEquipmentTypes::vanillaToolLevel)
                  .build());

        bow = register("bow",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_BOW))
                       .setIsEquipment((itemStack, equipmentType) -> itemStack.getItem() instanceof BowItem)
                       .setEquipmentLevel((itemStack, equipmentType) -> Compatibility.getItemLevel(itemStack))
                  .build());

        crossbow = register("crossbow",
            builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_CROSSBOW))
                .setIsEquipment((itemStack, equipmentType) -> itemStack.getItem() instanceof CrossbowItem)
                .setEquipmentLevel((itemStack, equipmentType) -> durabilityBasedLevel(itemStack, Items.CROSSBOW.getMaxDamage(itemStack)))
                .build());

        fishing_rod = register("rod",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_FISHING_ROD))
                       .setIsEquipment((itemStack, equipmentType) -> canPerformDefaultActions(itemStack, ItemAbilities.DEFAULT_FISHING_ROD_ACTIONS))
                       .setEquipmentLevel((itemStack, equipmentType) -> Compatibility.getItemLevel(itemStack))
                  .build());

        shears = register("shears",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_SHEARS))
                       .setIsEquipment((itemStack, equipmentType) -> canPerformDefaultActions(itemStack, ItemAbilities.DEFAULT_SHEARS_ACTIONS))
                       .setEquipmentLevel((itemStack, equipmentType) -> Compatibility.getItemLevel(itemStack))
                  .build());

        shield = register("shield",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_SHIELD))
                       .setIsEquipment((itemStack, equipmentType) -> canPerformDefaultActions(itemStack, Set.of(ItemAbility.get("shield_block"))))
                       .setEquipmentLevel((itemStack, equipmentType) -> Compatibility.getItemLevel(itemStack))
                  .build());

        helmet = register("helmet",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_HELMET))
                       .setIsEquipment((itemStack, equipmentType) -> ItemStackUtils.isArmorForSlot(itemStack, EquipmentSlot.HEAD))
                       .setEquipmentLevel((itemStack, equipmentType) -> Compatibility.getItemLevel(itemStack))
                  .build());

        leggings = register("leggings",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_LEGGINGS))
                       .setIsEquipment((itemStack, equipmentType) -> ItemStackUtils.isArmorForSlot(itemStack, EquipmentSlot.LEGS))
                       .setEquipmentLevel((itemStack, equipmentType) -> Compatibility.getItemLevel(itemStack))
                  .build());

        chestplate = register("chestplate",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_CHEST_PLATE))
                       .setIsEquipment((itemStack, equipmentType) -> ItemStackUtils.isArmorForSlot(itemStack, EquipmentSlot.CHEST))
                       .setEquipmentLevel((itemStack, equipmentType) -> Compatibility.getItemLevel(itemStack))
                  .build());

        boots = register("boots",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_BOOTS))
                       .setIsEquipment((itemStack, equipmentType) -> ItemStackUtils.isArmorForSlot(itemStack, EquipmentSlot.FEET))
                       .setEquipmentLevel((itemStack, equipmentType) -> Compatibility.getItemLevel(itemStack))
                  .build());

        flint_and_steel = register("flintandsteel",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_LIGHTER))
                       .setIsEquipment((itemStack, equipmentType) -> itemStack.getItem() instanceof FlintAndSteelItem)
                       .setEquipmentLevel((itemStack, equipmentType) -> Compatibility.getItemLevel(itemStack))
                  .build());

        lead = register("lead",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_LEAD))
                       .setIsEquipment((itemStack, equipmentType) -> itemStack.getItem() instanceof LeadItem)
                       .setEquipmentLevel((itemStack, equipmentType) -> -1)
                  .build());

        spear = register("spear",
          builder -> builder.setDisplayName(Component.translatable(ToolTranslationConstants.TOOL_TYPE_SPEAR))
                      .setIsEquipment((itemStack, equipmentType) -> itemStack.is(ModItems.spear))
                      .setEquipmentLevel((itemStack, equipmentType) -> durabilityBasedLevel(itemStack, ModItems.spear.getMaxDamage(itemStack)))
                  .build());

    }

    /**
     * Get the equipmentType registry.
     *
     * @return The equipmentType registry
     */
    public static Registry<EquipmentTypeEntry> getRegistry()
    {
        return IMinecoloniesAPI.getInstance().getEquipmentTypeRegistry();
    }

    /**
     * Register a new equipmentType to the registry.
     *
     * @param id The unique ID of the equipment type
     * @param consumer The consumer that builds the equipment type
     * @return The registry entry
     */
    private static DeferredHolder<EquipmentTypeEntry, EquipmentTypeEntry> register(final String id, final Consumer<EquipmentTypeEntry.Builder> consumer)
    {
        EquipmentTypeEntry.Builder equipmentType = new EquipmentTypeEntry.Builder()
                                           .setRegistryName(Identifier.fromNamespaceAndPath(Constants.MOD_ID, id));
        consumer.accept(equipmentType);
        return DEFERRED_REGISTER.register(id, equipmentType::build);
    }

    /**
     * Get the equipment level for vanilla tools.
     *
     * @param equipmentType  The type of vanilla tool
     * @param itemStack The item stack to check
     * @return The tool level
     */
    public static int vanillaToolLevel(final ItemStack itemStack, final EquipmentTypeEntry equipmentType)
    {
        if (Compatibility.isTinkersTool(itemStack, equipmentType) || Compatibility.isTinkersWeapon(itemStack))
        {
            return Compatibility.getToolLevel(itemStack);
        }
        return Compatibility.getItemLevel(itemStack);
    }

    /**
     * Get the durability based item level.
     *
     * @param itemStack The item stack to check
     * @return The item level
     */
    public static int durabilityBasedLevel(ItemStack itemStack, int vanillaItemDurability)
    {
        if (!itemStack.isDamageableItem())
        {
            return 5;
        }

        return Math.min(itemStack.getMaxDamage() / vanillaItemDurability, 5);
    }

    /**
     * Populate the tier registry with every item currently in the game.
     * Called once during FMLCommonSetupEvent via MineColonies.preInit.
     */
    @SuppressWarnings("null")
    public static void initRegisterEquipmentTiers()
    {
        int bowRef    = 0;
        int rodRef    = 0;
        int shearsRef = 0;
        int shieldRef = 0;
        int flintRef  = 0;
        try
        {
            bowRef    = new ItemStack(Items.BOW).getMaxDamage();
            rodRef    = new ItemStack(Items.FISHING_ROD).getMaxDamage();
            shearsRef = new ItemStack(Items.SHEARS).getMaxDamage();
            shieldRef = new ItemStack(Items.SHIELD).getMaxDamage();
            flintRef  = new ItemStack(Items.FLINT_AND_STEEL).getMaxDamage();
        }
        catch (Exception e)
        {
            // In case something goes wrong with fetching durability references, we can still continue and just won't have durability based tiers for those items.
            Log.getLogger().error("Failed to fetch getMaxDamage references for equipment tier registration, durability based tiers for certain items will not be registered.", e);
            return;
        }
        

        for (final Item item : BuiltInRegistries.ITEM)
        {
            try
            {
                final ItemStack dummy = new ItemStack(item);

                final ToolMaterial material = getToolMaterial(dummy);
                if (material != null)
                {
                    Compatibility.registerItemTierIfAbsent(item, material, (int) material.attackDamageBonus());
                }
                else if (ItemStackUtils.isArmor(dummy))
                {
                    final int level = ItemStackUtils.getArmorLevel(dummy);
                    if (level > 0)
                    {
                        Compatibility.registerItemTierIfAbsent(item, level);
                    }
                }
                else if (item instanceof BowItem)
                {
                    Compatibility.registerItemTierIfAbsent(item, durabilityBasedLevel(dummy, bowRef));
                }
                else if (canPerformDefaultActions(dummy, ItemAbilities.DEFAULT_FISHING_ROD_ACTIONS))
                {
                    Compatibility.registerItemTierIfAbsent(item, durabilityBasedLevel(dummy, rodRef));
                }
                else if (canPerformDefaultActions(dummy, ItemAbilities.DEFAULT_SHEARS_ACTIONS))
                {
                    Compatibility.registerItemTierIfAbsent(item, durabilityBasedLevel(dummy, shearsRef));
                }
                else if (canPerformDefaultActions(dummy, Set.of(ItemAbility.get("shield_block"))))
                {
                    Compatibility.registerItemTierIfAbsent(item, durabilityBasedLevel(dummy, shieldRef));
                }
                else if (item instanceof FlintAndSteelItem)
                {
                    Compatibility.registerItemTierIfAbsent(item, durabilityBasedLevel(dummy, flintRef));
                }
            }
            catch (Exception e)
            {
                Log.getLogger().error("Failed to register equipment tiers for item: " + BuiltInRegistries.ITEM.getKey(item), e);
            }
        }
    }

    /**
     * All vanilla tool materials, used to recognize the tier of an arbitrary tool.
     */
    private static final ToolMaterial[] VANILLA_TOOL_MATERIALS =
      {ToolMaterial.WOOD, ToolMaterial.STONE, ToolMaterial.COPPER, ToolMaterial.IRON, ToolMaterial.DIAMOND, ToolMaterial.GOLD, ToolMaterial.NETHERITE};

    /**
     * Determine the tool material of a stack from its {@link Tool} component.
     * <p>
     * Replaces the removed vanilla {@code TieredItem} class: the tier of a tool is only expressed through the block tag
     * the tool is denied drops for, so the stack is matched back against the vanilla materials by that tag.
     *
     * @param stack the stack to inspect.
     * @return the matching vanilla tool material, or null if the stack is not a tiered tool.
     */
    @Nullable
    private static ToolMaterial getToolMaterial(final ItemStack stack)
    {
        final Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null)
        {
            return null;
        }

        for (final Tool.Rule rule : tool.rules())
        {
            if (!rule.correctForDrops().orElse(true))
            {
                final Optional<TagKey<Block>> deniedTag = rule.blocks().unwrapKey();
                if (deniedTag.isPresent())
                {
                    for (final ToolMaterial material : VANILLA_TOOL_MATERIALS)
                    {
                        if (material.incorrectBlocksForDrops().equals(deniedTag.get()))
                        {
                            return material;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Determine whether an item stack can perform the default actions of a given tool.
     *
     * @param itemStack The item stack to check
     * @param actions   The set of actions to compare
     * @return Whether the item stack can perform the actions
     */
    public static boolean canPerformDefaultActions(ItemStack itemStack, Set<ItemAbility> actions)
    {
        for (final ItemAbility toolAction : actions)
        {
            if (!itemStack.canPerformAction(toolAction))
            {
                return false;
            }
        }
        return true;
    }
}

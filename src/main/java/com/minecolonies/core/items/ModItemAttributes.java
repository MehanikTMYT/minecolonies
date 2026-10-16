package com.minecolonies.core.items;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Helpers to build the melee attribute modifiers of MineColonies weapons.
 * <p>
 * The tool material based helpers on {@link Item.Properties} always add the attack damage bonus of the material on top
 * of the baseline. MineColonies weapons instead declare their final values directly, so their combat behaviour stays
 * independent of the material that supplies durability, repair items and enchantability.
 */
public final class ModItemAttributes
{
    private ModItemAttributes()
    {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Build the main hand melee modifiers of a weapon.
     *
     * @param attackDamage the total attack damage bonus of the weapon.
     * @param attackSpeed  the attack speed modifier of the weapon.
     * @return the attribute modifiers to apply to the item.
     */
    public static ItemAttributeModifiers meleeAttributes(final float attackDamage, final float attackSpeed)
    {
        return ItemAttributeModifiers.builder()
                 .add(Attributes.ATTACK_DAMAGE,
                   new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE),
                   EquipmentSlotGroup.MAINHAND)
                 .add(Attributes.ATTACK_SPEED,
                   new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                   EquipmentSlotGroup.MAINHAND)
                 .build();
    }
}

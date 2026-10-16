package com.minecolonies.core.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

/**
 * Class handling the Scimitar item.
 */
public class ItemIronScimitar extends Item
{
    /**
     * Durability of the scimitar, kept independent of the tool material.
     */
    private static final int SCIMITAR_DURABILITY = 250;

    /**
     * Constructor method for the Scimitar Item
     *
     * @param properties the properties.
     */
    public ItemIronScimitar(final Item.Properties properties)
    {
        // Iron supplies the repair items, enchantability and mining rules; durability and melee values are explicit,
        // so that they keep the exact numbers the scimitar had before the tool material API replaced the sword item.
        super(properties.sword(ToolMaterial.IRON, 3, -2.4F)
                .durability(SCIMITAR_DURABILITY)
                .attributes(ModItemAttributes.meleeAttributes(3.0F, -2.4F)));
    }
}

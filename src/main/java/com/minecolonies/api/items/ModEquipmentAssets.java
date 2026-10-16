package com.minecolonies.api.items;

import com.minecolonies.api.util.constant.Constants;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

/**
 * Equipment assets for the armour added by MineColonies.
 * <p>
 * Since 26.1 the armour layer textures are no longer declared on the {@code ArmorMaterial} itself but referenced
 * through an {@link EquipmentAsset} key, which resolves to {@code assets/minecolonies/equipment/&lt;path&gt;.json}.
 */
public final class ModEquipmentAssets
{
    public static final ResourceKey<EquipmentAsset> SANTA_HAT     = create("santa_hat");
    public static final ResourceKey<EquipmentAsset> PLATE_ARMOR   = create("plate_armor");
    public static final ResourceKey<EquipmentAsset> BUILD_GOGGLES = create("build_goggles");
    public static final ResourceKey<EquipmentAsset> PIRATE        = create("pirate");
    public static final ResourceKey<EquipmentAsset> PIRATE_2      = create("pirate2");

    private ModEquipmentAssets()
    {
        throw new IllegalStateException("Tried to initialize: ModEquipmentAssets but this is a Utility class.");
    }

    private static ResourceKey<EquipmentAsset> create(final String name)
    {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
    }
}

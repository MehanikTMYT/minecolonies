package com.minecolonies.core.generation.defaults;

import com.minecolonies.api.entity.ModEntities;
import com.minecolonies.api.util.DamageSourceKeys;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/**
 * Registers MineColonies' custom damage types into the datapack registry set used by datagen, so that other
 * datagen providers (e.g. damage type tags) can see and validate references to them.
 */
public class DefaultDamageTypeProvider
{
    public static void bootstrap(final BootstrapContext<DamageType> context)
    {
        context.register(DamageSourceKeys.CONSOLE, damage("console"));
        context.register(DamageSourceKeys.DEFAULT, damage("default"));
        context.register(DamageSourceKeys.DESPAWN, damage("despawn"));
        context.register(DamageSourceKeys.NETHER, damage("nether"));
        context.register(DamageSourceKeys.GUARD, damage("entity.minecolonies.guard"));
        context.register(DamageSourceKeys.GUARD_PVP, damage("entity.minecolonies.guardpvp"));
        context.register(DamageSourceKeys.SLAP, damage("entity.minecolonies.slap"));
        context.register(DamageSourceKeys.STUCK_DAMAGE, damage("entity.minecolonies.stuckdamage"));
        context.register(DamageSourceKeys.TRAINING, damage("entity.minecolonies.training"));
        context.register(DamageSourceKeys.WAKEY, damage("entity.minecolonies.wakeywakey"));
        context.register(DamageSourceKeys.PIERCE, damage("entity.minecolonies.pierce"));

        context.register(DamageSourceKeys.AMAZON, entityDamage(ModEntities.AMAZON));
        context.register(DamageSourceKeys.AMAZONCHIEF, entityDamage(ModEntities.AMAZONCHIEF));
        context.register(DamageSourceKeys.AMAZONSPEARMAN, entityDamage(ModEntities.AMAZONSPEARMAN));
        context.register(DamageSourceKeys.ARCHERBARBARIAN, entityDamage(ModEntities.ARCHERBARBARIAN));
        context.register(DamageSourceKeys.ARCHERMUMMY, entityDamage(ModEntities.ARCHERMUMMY));
        context.register(DamageSourceKeys.ARCHERPIRATE, entityDamage(ModEntities.ARCHERPIRATE));
        context.register(DamageSourceKeys.BARBARIAN, entityDamage(ModEntities.BARBARIAN));
        context.register(DamageSourceKeys.CHIEFBARBARIAN, entityDamage(ModEntities.CHIEFBARBARIAN));
        context.register(DamageSourceKeys.CHIEFPIRATE, entityDamage(ModEntities.CHIEFPIRATE));
        context.register(DamageSourceKeys.MERCENARY, entityDamage(ModEntities.MERCENARY));
        context.register(DamageSourceKeys.MUMMY, entityDamage(ModEntities.MUMMY));
        context.register(DamageSourceKeys.NORSEMENARCHER, entityDamage(ModEntities.NORSEMEN_ARCHER));
        context.register(DamageSourceKeys.NORSEMENCHIEF, entityDamage(ModEntities.NORSEMEN_CHIEF));
        context.register(DamageSourceKeys.PHARAO, entityDamage(ModEntities.PHARAO));
        context.register(DamageSourceKeys.PIRATE, entityDamage(ModEntities.PIRATE));
        context.register(DamageSourceKeys.SHIELDMAIDEN, entityDamage(ModEntities.SHIELDMAIDEN));
        context.register(DamageSourceKeys.SPEAR, entityDamage(ModEntities.SPEAR));
        context.register(DamageSourceKeys.VISITOR, entityDamage(ModEntities.VISITOR));
        context.register(DamageSourceKeys.DROWNED_PIRATE, entityDamage(ModEntities.DROWNED_PIRATE));
        context.register(DamageSourceKeys.DROWNED_ARCHERPIRATE, entityDamage(ModEntities.DROWNED_ARCHERPIRATE));
        context.register(DamageSourceKeys.DROWNED_CHIEFPIRATE, entityDamage(ModEntities.DROWNED_CHIEFPIRATE));

        context.register(DamageSourceKeys.CAMP_AMAZON, entityDamage(ModEntities.CAMP_AMAZON));
        context.register(DamageSourceKeys.CAMP_AMAZONCHIEF, entityDamage(ModEntities.CAMP_AMAZONCHIEF));
        context.register(DamageSourceKeys.CAMP_AMAZONSPEARMAN, entityDamage(ModEntities.CAMP_AMAZONSPEARMAN));

        context.register(DamageSourceKeys.CAMP_BARBARIAN, entityDamage(ModEntities.CAMP_BARBARIAN));
        context.register(DamageSourceKeys.CAMP_CHIEFBARBARIAN, entityDamage(ModEntities.CAMP_CHIEFBARBARIAN));
        context.register(DamageSourceKeys.CAMP_ARCHERBARBARIAN, entityDamage(ModEntities.CAMP_ARCHERBARBARIAN));

        context.register(DamageSourceKeys.CAMP_MUMMY, entityDamage(ModEntities.CAMP_MUMMY));
        context.register(DamageSourceKeys.CAMP_ARCHERMUMMY, entityDamage(ModEntities.CAMP_ARCHERMUMMY));
        context.register(DamageSourceKeys.CAMP_PHARAO, entityDamage(ModEntities.CAMP_PHARAO));

        context.register(DamageSourceKeys.CAMP_PIRATE, entityDamage(ModEntities.CAMP_PIRATE));
        context.register(DamageSourceKeys.CAMP_ARCHERPIRATE, entityDamage(ModEntities.CAMP_ARCHERPIRATE));
        context.register(DamageSourceKeys.CAMP_CHIEFPIRATE, entityDamage(ModEntities.CAMP_CHIEFPIRATE));

        context.register(DamageSourceKeys.CAMP_NORSEMENARCHER, entityDamage(ModEntities.CAMP_NORSEMEN_ARCHER));
        context.register(DamageSourceKeys.CAMP_NORSEMENCHIEF, entityDamage(ModEntities.CAMP_NORSEMEN_CHIEF));
        context.register(DamageSourceKeys.CAMP_SHIELDMAIDEN, entityDamage(ModEntities.CAMP_SHIELDMAIDEN));
    }

    @NotNull
    private static DamageType entityDamage(@NotNull final EntityType<?> entityType)
    {
        return damage(entityType.getDescriptionId());
    }

    @NotNull
    private static DamageType damage(@NotNull final String msgId)
    {
        return new DamageType(msgId, DamageScaling.ALWAYS, 0.1F);
    }
}

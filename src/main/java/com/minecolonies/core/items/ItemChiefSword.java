package com.minecolonies.core.items;

import com.minecolonies.api.entity.mobs.RaiderMobUtils;
import com.minecolonies.api.entity.mobs.barbarians.AbstractEntityBarbarianRaider;
import com.minecolonies.api.items.IChiefSwordItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.Constants.GLOW_EFFECT_DISTANCE;
import static com.minecolonies.api.util.constant.Constants.GLOW_EFFECT_DURATION;

/**
 * Class handling the Chief Sword item.
 */
public class ItemChiefSword extends Item implements IChiefSwordItem
{
    private static final int LEVITATION_EFFECT_DURATION   = 20 * 10;
    private static final int LEVITATION_EFFECT_MULTIPLIER = 2;

    /**
     * Durability of the chief sword, kept independent of the tool material.
     */
    private static final int CHIEF_SWORD_DURABILITY = 1500;

    /**
     * Constructor method for the Chief Sword Item
     *
     * @param properties the properties.
     */
    public ItemChiefSword(final Properties properties)
    {
        // Diamond supplies the repair items, enchantability and mining rules; durability and melee values are explicit,
        // so that they keep the exact numbers the chief sword had before the tool material API replaced the sword item.
        super(properties.sword(ToolMaterial.DIAMOND, 3, -2.4F)
                .durability(CHIEF_SWORD_DURABILITY)
                .attributes(ModItemAttributes.meleeAttributes(3.0F, -2.4F)));
    }

    @Override
    public void inventoryTick(final ItemStack stack, final ServerLevel level, final Entity entityIn, @Nullable final EquipmentSlot slot)
    {
        if (entityIn instanceof Player && slot == EquipmentSlot.MAINHAND)
        {
            RaiderMobUtils.getBarbariansCloseToEntity(entityIn, GLOW_EFFECT_DISTANCE)
                .forEach(entity -> entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOW_EFFECT_DURATION, 0)));
        }
    }

    @Override
    public void hurtEnemy(final ItemStack stack, final LivingEntity target, @NotNull final LivingEntity attacker)
    {
        if (attacker instanceof Player && target instanceof AbstractEntityBarbarianRaider)
        {
            target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, LEVITATION_EFFECT_DURATION, LEVITATION_EFFECT_MULTIPLIER));
        }

        super.hurtEnemy(stack, target, attacker);
    }
}

package com.minecolonies.api.entity.other;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Short-lived, fast MineColonies transport cart that does not accept interaction or collisions.
 * Rail movement, activator rails, interpolation, fluids and persistence are delegated to the
 * current vanilla minecart behavior implementation.
 */
public class MinecoloniesMinecart extends Minecart
{
    private static final Vec3 LOWERED_PASSENGER_ATTACHMENT = Vec3.ZERO;

    /** Maximum rail speed retained from the legacy custom rail implementation. */
    private static final double MAX_RAIL_SPEED = 2.0D;

    public MinecoloniesMinecart(final EntityType<?> type, final Level world)
    {
        super(type, world);
    }

    @Override
    protected double getMaxSpeed(final ServerLevel level)
    {
        return MAX_RAIL_SPEED;
    }

    @Override
    protected void destroy(final ServerLevel level, final DamageSource source)
    {
        // This is a transient carrier: the legacy implementation deliberately removed it
        // without producing a vanilla minecart item.
        this.kill(level);
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand, final Vec3 location)
    {
        return InteractionResult.FAIL;
    }

    @Override
    public boolean isPickable()
    {
        return false;
    }

    @Override
    public void push(@NotNull final Entity entity)
    {
        // MineColonies carriers intentionally do not collide with other entities.
    }

    @Override
    public void playerTouch(final Player player)
    {
        // MineColonies carriers intentionally do not pick up or push nearby players.
    }

    @Override
    public boolean isPushable()
    {
        return false;
    }

    @Override
    public boolean canCollideWith(final Entity entity)
    {
        return false;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.tickCount % 20 == 19 && this.getPassengers().isEmpty())
        {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    @NotNull
    protected Vec3 getPassengerAttachmentPoint(
      @NotNull final Entity passenger,
      @NotNull final EntityDimensions dimensions,
      final float scale)
    {
        return LOWERED_PASSENGER_ATTACHMENT;
    }
}

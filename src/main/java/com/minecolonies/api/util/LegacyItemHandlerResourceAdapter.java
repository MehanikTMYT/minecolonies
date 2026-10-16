package com.minecolonies.api.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Transactional bridge from MineColonies' existing item handlers to the 26.1 transfer capability API.
 */
public final class LegacyItemHandlerResourceAdapter implements ResourceHandler<ItemResource>
{
    private static final Map<IItemHandler, WeakReference<ResourceHandler<ItemResource>>> CACHE = new WeakHashMap<>();

    private final IItemHandlerModifiable handler;
    private final SnapshotJournal<List<ItemStack>> snapshots = new SnapshotJournal<>()
    {
        @Override
        protected List<ItemStack> createSnapshot()
        {
            final List<ItemStack> snapshot = new ArrayList<>(handler.getSlots());
            for (int slot = 0; slot < handler.getSlots(); slot++)
            {
                snapshot.add(handler.getStackInSlot(slot).copy());
            }
            return snapshot;
        }

        @Override
        protected void revertToSnapshot(final List<ItemStack> snapshot)
        {
            for (int slot = 0; slot < snapshot.size(); slot++)
            {
                handler.setStackInSlot(slot, snapshot.get(slot));
            }
        }
    };

    private LegacyItemHandlerResourceAdapter(final IItemHandlerModifiable handler)
    {
        this.handler = handler;
    }

    /**
     * Returns the stable adapter associated with an existing handler.
     */
    public static synchronized ResourceHandler<ItemResource> of(final IItemHandler handler)
    {
        if (!(handler instanceof final IItemHandlerModifiable modifiable))
        {
            throw new IllegalArgumentException("Item capability handlers must support transactional rollback");
        }

        final WeakReference<ResourceHandler<ItemResource>> reference = CACHE.get(handler);
        ResourceHandler<ItemResource> adapter = reference == null ? null : reference.get();
        if (adapter == null)
        {
            adapter = new LegacyItemHandlerResourceAdapter(modifiable);
            CACHE.put(handler, new WeakReference<>(adapter));
        }
        return adapter;
    }

    @Override
    public int size()
    {
        return handler.getSlots();
    }

    @Override
    public ItemResource getResource(final int index)
    {
        return ItemResource.of(handler.getStackInSlot(index));
    }

    @Override
    public long getAmountAsLong(final int index)
    {
        return handler.getStackInSlot(index).getCount();
    }

    @Override
    public long getCapacityAsLong(final int index, final ItemResource resource)
    {
        return resource.isEmpty() ? handler.getSlotLimit(index) : Math.min(handler.getSlotLimit(index), resource.getMaxStackSize());
    }

    @Override
    public boolean isValid(final int index, final ItemResource resource)
    {
        return !resource.isEmpty() && handler.isItemValid(index, resource.toStack());
    }

    @Override
    public int insert(final int index, final ItemResource resource, final int amount, final TransactionContext transaction)
    {
        if (resource.isEmpty() || amount < 0)
        {
            throw new IllegalArgumentException("Cannot insert an empty resource or a negative amount");
        }
        if (amount == 0)
        {
            return 0;
        }

        snapshots.updateSnapshots(transaction);
        final ItemStack remainder = handler.insertItem(index, resource.toStack(amount), false);
        return amount - remainder.getCount();
    }

    @Override
    public int extract(final int index, final ItemResource resource, final int amount, final TransactionContext transaction)
    {
        if (resource.isEmpty() || amount < 0)
        {
            throw new IllegalArgumentException("Cannot extract an empty resource or a negative amount");
        }
        if (amount == 0 || !resource.matches(handler.getStackInSlot(index)))
        {
            return 0;
        }

        snapshots.updateSnapshots(transaction);
        return handler.extractItem(index, amount, false).getCount();
    }
}

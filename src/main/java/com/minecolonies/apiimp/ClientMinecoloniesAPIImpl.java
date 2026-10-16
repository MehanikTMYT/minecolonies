package com.minecolonies.apiimp;

import com.minecolonies.api.client.render.modeltype.registry.IModelTypeRegistry;
import com.minecolonies.core.client.render.modeltype.registry.ModelTypeRegistry;
import com.minecolonies.core.client.gui.WindowReactivateBuilding;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.registries.NewRegistryEvent;

public class ClientMinecoloniesAPIImpl extends CommonMinecoloniesAPIImpl
{
    private final IModelTypeRegistry modelTypeRegistry = new ModelTypeRegistry();

    @Override
    public IModelTypeRegistry getModelTypeRegistry()
    {
        return modelTypeRegistry;
    }

    @Override
    public void onRegistryNewRegistry(final NewRegistryEvent event)
    {
        super.onRegistryNewRegistry(event);
    }

    @Override
    public void openReactivationWindow(final BlockPos pos)
    {
        new WindowReactivateBuilding(pos).open();
    }
}

package dev.mgcode.sablejade;

import dev.mgcode.sablejade.jade.SubLevelAccessorRemapper;
import dev.mgcode.sablejade.jade.tooltip.SablePhysicsTooltipProvider;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class SableJadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(final IWailaClientRegistration registration) {
        registration.registerBlockComponent(SablePhysicsTooltipProvider.INSTANCE, Block.class);
        SubLevelAccessorRemapper.register(registration);
    }
}

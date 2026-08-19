package dev.mgcode.sablejade;

import dev.mgcode.sablejade.compat.SimulatedHoldInteractionCompat;
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

        // The Simulated steering wheel HUD renders behind Jade's overlay in the same screen area,
        // so hide Jade while the wheel (or any hold interaction) is being dragged.
        registration.addBeforeRenderCallback((box, rect, guiGraphics, accessor) ->
                SimulatedHoldInteractionCompat.isHoldInteractionActive());
    }
}

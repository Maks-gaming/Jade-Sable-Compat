package dev.mgcode.sablejade.physics;

import dev.mgcode.sablejade.compat.SimulatedTooltipCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public final class SablePhysicsTooltipResolver {
    private SablePhysicsTooltipResolver() {
    }

    public static PhysicsTooltipData resolve(final BlockGetter level,
            final BlockPos pos,
            final BlockState blockState) {
        return new PhysicsTooltipData(SimulatedTooltipCompat.getBlockPropertyLines(blockState));
    }
}

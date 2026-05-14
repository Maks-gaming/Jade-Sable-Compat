package dev.mgcode.sablejade.jade.tooltip;

import dev.mgcode.sablejade.compat.AeronauticsTooltipCompat;
import dev.mgcode.sablejade.SableJadeMod;
import dev.mgcode.sablejade.compat.CreateGogglesCompat;
import dev.mgcode.sablejade.physics.PhysicsTooltipData;
import dev.mgcode.sablejade.physics.SablePhysicsTooltipResolver;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;

public final class SablePhysicsTooltipProvider implements IBlockComponentProvider {
    public static final SablePhysicsTooltipProvider INSTANCE = new SablePhysicsTooltipProvider();

    private static final ResourceLocation UID = SableJadeMod.id("physics_tooltip");

    private SablePhysicsTooltipProvider() {
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public void appendTooltip(final ITooltip tooltip,
            final BlockAccessor blockAccessor,
            final IPluginConfig config) {
        if (!shouldShowPhysicsInfo(blockAccessor)) {
            return;
        }

        final BlockState blockState = blockAccessor.getBlockState();
        if (blockState.isAir()) {
            return;
        }

        final PhysicsTooltipData tooltipData = SablePhysicsTooltipResolver.resolve(
                blockAccessor.getLevel(),
                blockAccessor.getPosition(),
                blockState);

        if (tooltipData.lines().isEmpty()) {
            return;
        }

        for (final Component line : tooltipData.lines()) {
            addLine(tooltip, TooltipPosition.TAIL, line);
        }

        final Component extraDescription = AeronauticsTooltipCompat.getAdditionalDescription(blockState);
        if (extraDescription != null) {
            addLine(tooltip, TooltipPosition.TAIL, extraDescription);
        }
    }

    private static boolean shouldShowPhysicsInfo(final BlockAccessor accessor) {
        final Player player = accessor.getPlayer();
        return player != null
                && (player.isCreative() || player.isSpectator() || CreateGogglesCompat.isWearingCreateGoggles(player));
    }

    private static void addLine(final ITooltip tooltip, final int preferredIndex, final Component component) {
        if (component == null) {
            return;
        }
        final int safeIndex = Math.max(0, Math.min(preferredIndex, tooltip.size()));
        tooltip.add(safeIndex, component);
    }
}
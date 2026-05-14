package dev.mgcode.sablejade.compat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class SimulatedTooltipCompat {
    private static final String BLOCK_PROPERTIES_TOOLTIP = "dev.simulated_team.simulated.client.BlockPropertiesTooltip";
    private static final String BLOCK_STATE_EXTENSION = "dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension";

    private static final TooltipMethod MASS = resolveTooltipMethod("getMassComponent");
    private static final TooltipMethod FRICTION = resolveTooltipMethod("getFrictionComponent");
    private static final TooltipMethod RESTITUTION = resolveTooltipMethod("getRestitutionComponent");
    private static final TooltipMethod FRAGILE = resolveTooltipMethod("getFragileComponent");
    private static final TooltipMethod AIRTIGHT = resolveTooltipMethod("getAirtightComponent");
    private static final TooltipMethod FLOATING = resolveTooltipMethod("getFloatingComponent");

    private SimulatedTooltipCompat() {
    }

    public static List<Component> getBlockPropertyLines(final BlockState blockState) {
        if (blockState == null) {
            return List.of();
        }

        final Item item = blockState.getBlock().asItem();
        if (!(item instanceof BlockItem blockItem)) {
            return List.of();
        }

        final List<Component> lines = new ArrayList<>(6);
        addIfPresent(lines, MASS.invoke(blockState, blockItem));
        addIfPresent(lines, FRICTION.invoke(blockState, blockItem));
        addIfPresent(lines, RESTITUTION.invoke(blockState, blockItem));
        addIfPresent(lines, FRAGILE.invoke(blockState, blockItem));
        addIfPresent(lines, AIRTIGHT.invoke(blockState, blockItem));
        addIfPresent(lines, FLOATING.invoke(blockState, blockItem));
        return List.copyOf(lines);
    }

    private static void addIfPresent(final List<Component> lines, final Component line) {
        if (line != null) {
            lines.add(line);
        }
    }

    private static TooltipMethod resolveTooltipMethod(final String methodName) {
        try {
            final Class<?> tooltipClass = Class.forName(BLOCK_PROPERTIES_TOOLTIP);
            final Class<?> extensionClass = Class.forName(BLOCK_STATE_EXTENSION);
            final Method method = tooltipClass.getMethod(methodName, extensionClass, BlockItem.class, boolean.class);
            return new TooltipMethod(method, extensionClass);
        } catch (final ReflectiveOperationException exception) {
            return TooltipMethod.unavailable();
        }
    }

    private record TooltipMethod(Method method, Class<?> extensionClass) {
        private static TooltipMethod unavailable() {
            return new TooltipMethod(null, null);
        }

        private Component invoke(final BlockState blockState, final BlockItem blockItem) {
            if (method == null || extensionClass == null || !extensionClass.isInstance(blockState)) {
                return null;
            }
            try {
                return (Component) method.invoke(null, extensionClass.cast(blockState), blockItem, true);
            } catch (final ReflectiveOperationException exception) {
                return null;
            }
        }
    }
}
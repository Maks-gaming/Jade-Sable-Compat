package dev.mgcode.sablejade.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;
import java.util.Objects;

public final class CreateGogglesCompat {
    private static final ResourceLocation CREATE_GOGGLES_ID = Objects
            .requireNonNull(ResourceLocation.fromNamespaceAndPath("create", "goggles"));

    private static Method isWearingGogglesMethod;
    private static boolean resolved;

    private CreateGogglesCompat() {
    }

    public static boolean isWearingCreateGoggles(final Player player) {
        final Boolean reflectedResult = invokeCreateGogglesCheck(player);
        if (reflectedResult != null) {
            return reflectedResult;
        }

        return BuiltInRegistries.ITEM.getOptional(CREATE_GOGGLES_ID)
                .map(gogglesItem -> player.getItemBySlot(EquipmentSlot.HEAD).is(Objects.requireNonNull(gogglesItem)))
                .orElse(false);
    }

    private static Boolean invokeCreateGogglesCheck(final Player player) {
        if (!ModList.get().isLoaded("create")) {
            return null;
        }

        final Method method = resolveCreateMethod();
        if (method == null) {
            return null;
        }

        try {
            return (Boolean) method.invoke(null, player);
        } catch (final ReflectiveOperationException | RuntimeException exception) {
            return null;
        }
    }

    private static Method resolveCreateMethod() {
        if (resolved) {
            return isWearingGogglesMethod;
        }

        resolved = true;
        try {
            isWearingGogglesMethod = Class
                    .forName("com.simibubi.create.content.equipment.goggles.GogglesItem")
                    .getMethod("isWearingGoggles", Player.class);
        } catch (final ReflectiveOperationException exception) {
            isWearingGogglesMethod = null;
        }
        return isWearingGogglesMethod;
    }
}
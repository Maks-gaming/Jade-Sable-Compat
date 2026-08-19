package dev.mgcode.sablejade.compat;

import java.lang.reflect.Method;

public final class SimulatedHoldInteractionCompat {
    private static final String HOLD_INTERACTION_MANAGER = "dev.simulated_team.simulated.util.hold_interaction.HoldInteractionManager";

    private static final Method IS_ACTIVE = resolveIsActive();

    private SimulatedHoldInteractionCompat() {
    }

    /**
     * True while the player is dragging a Simulated hold interaction (e.g. the steering wheel),
     * whose HUD overlay occupies the same screen area as Jade's tooltip.
     */
    public static boolean isHoldInteractionActive() {
        if (IS_ACTIVE == null) {
            return false;
        }
        try {
            return (boolean) IS_ACTIVE.invoke(null);
        } catch (final ReflectiveOperationException exception) {
            return false;
        }
    }

    private static Method resolveIsActive() {
        try {
            return Class.forName(HOLD_INTERACTION_MANAGER).getMethod("isActive");
        } catch (final ReflectiveOperationException exception) {
            return null;
        }
    }
}

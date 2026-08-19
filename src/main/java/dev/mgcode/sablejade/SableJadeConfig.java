package dev.mgcode.sablejade;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SableJadeConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.EnumValue<TooltipInfoMode> TOOLTIP_INFO_MODE;
    public static final ModConfigSpec.BooleanValue TOOLTIP_INFO_ONLY_IN_SUB_LEVELS;

    static {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("tooltips");
        TOOLTIP_INFO_MODE = builder
                .comment(
                        "Controls whether Sable Jade adds extra tooltip lines beyond fixing Jade's targeting.",
                        "Allowed values: NEVER, ALWAYS, WITH_GOGGLES")
                .defineEnum("tooltipInfoMode", TooltipInfoMode.WITH_GOGGLES);
        TOOLTIP_INFO_ONLY_IN_SUB_LEVELS = builder
                .comment("If true, physics tooltip lines are only shown for blocks that are part of a Sable sub-level (physics object).")
                .define("tooltipInfoOnlyInSubLevels", true);
        builder.pop();

        SPEC = builder.build();
    }

    private SableJadeConfig() {
    }

    public static TooltipInfoMode tooltipInfoMode() {
        return TOOLTIP_INFO_MODE.get();
    }

    public static boolean tooltipInfoOnlyInSubLevels() {
        return TOOLTIP_INFO_ONLY_IN_SUB_LEVELS.get();
    }
}

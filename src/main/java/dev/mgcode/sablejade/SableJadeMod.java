package dev.mgcode.sablejade;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.Objects;

@Mod(SableJadeMod.MOD_ID)
public final class SableJadeMod {
    public static final String MOD_ID = "sablejade";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation id(final String path) {
        return Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(MOD_ID, path));
    }

    public SableJadeMod(final ModContainer modContainer) {
        LOGGER.info("Loading {}", MOD_ID);
        modContainer.registerConfig(ModConfig.Type.CLIENT, SableJadeConfig.SPEC);
    }
}

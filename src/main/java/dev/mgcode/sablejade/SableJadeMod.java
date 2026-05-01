package dev.mgcode.sablejade;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(SableJadeMod.MOD_ID)
public final class SableJadeMod {
    public static final String MOD_ID = "sablejade";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SableJadeMod() {
        LOGGER.info("Loading {}", MOD_ID);
    }
}
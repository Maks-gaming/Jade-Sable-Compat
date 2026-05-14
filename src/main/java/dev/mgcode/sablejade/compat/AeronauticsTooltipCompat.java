package dev.mgcode.sablejade.compat;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import java.util.Objects;

public final class AeronauticsTooltipCompat {
    private static final TagKey<Block> ENVELOPE = blockTag("envelope");

    private AeronauticsTooltipCompat() {
    }

    public static Component getAdditionalDescription(final BlockState blockState) {
        if (!ModList.get().isLoaded("aeronautics")) {
            return null;
        }

        if (hasTag(blockState, ENVELOPE)) {
            return Component.translatable("aeronautics.ponder.envelope.text_1")
                    .withStyle(ChatFormatting.DARK_GRAY);
        }

        return null;
    }

    private static TagKey<Block> blockTag(final String path) {
        final ResourceLocation id = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath("aeronautics", path));
        return Objects.requireNonNull(TagKey.create(Registries.BLOCK, id));
    }

    private static boolean hasTag(final BlockState blockState, final TagKey<Block> tag) {
        return blockState.is(Objects.requireNonNull(tag));
    }
}
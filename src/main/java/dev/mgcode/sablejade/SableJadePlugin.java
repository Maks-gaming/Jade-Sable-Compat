package dev.mgcode.sablejade;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.LevelPoseProviderExtension;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

import java.util.Objects;

@WailaPlugin
public final class SableJadePlugin implements IWailaPlugin {
    private static final String HONEY_GLUE_ENTITY_ID = "simulated:honey_glue";

    @Override
    public void registerClient(final IWailaClientRegistration registration) {
        registration.addRayTraceCallback(1000,
                (hitResult, accessor, originalAccessor) -> remapAccessor(registration, accessor));
    }

    private static Accessor<?> remapAccessor(final IWailaClientRegistration registration,
            final Accessor<?> accessor) {
        if (accessor == null) {
            return null;
        }

        if (isHoneyGlueAccessor(accessor)) {
            return passThroughHoneyGlue(registration, accessor);
        }

        return remapSubLevelBlockAccessor(registration, accessor);
    }

    private static boolean isHoneyGlueAccessor(final Accessor<?> accessor) {
        return accessor instanceof EntityAccessor entityAccessor
                && HONEY_GLUE_ENTITY_ID
                        .equals(EntityType.getKey(entityAccessor.getEntity().getType()).toString());
    }

    private static Accessor<?> passThroughHoneyGlue(final IWailaClientRegistration registration,
            final Accessor<?> accessor) {
        final BlockHitResult correctedHitResult = retraceSubLevelBlock(accessor);
        if (correctedHitResult == null || correctedHitResult.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        return buildBlockAccessor(registration, accessor, correctedHitResult);
    }

    private static Accessor<?> remapSubLevelBlockAccessor(final IWailaClientRegistration registration,
            final Accessor<?> accessor) {
        if (!(accessor.getHitResult() instanceof BlockHitResult blockHitResult)
                || blockHitResult.getType() != HitResult.Type.BLOCK) {
            return accessor;
        }

        final BlockHitResult correctedHitResult = retraceSubLevelBlock(accessor);
        if (correctedHitResult == null || correctedHitResult.getType() != HitResult.Type.BLOCK) {
            return accessor;
        }

        if (!(Sable.HELPER.getContaining(accessor.getLevel(), correctedHitResult.getBlockPos()) instanceof SubLevel)) {
            return accessor;
        }

        final Accessor<?> correctedAccessor = buildBlockAccessor(registration, accessor, correctedHitResult);
        return correctedAccessor == null ? accessor : correctedAccessor;
    }

    private static Accessor<?> buildBlockAccessor(final IWailaClientRegistration registration,
            final Accessor<?> accessor,
            final BlockHitResult hitResult) {
        final BlockPos plotPos = Objects.requireNonNull(hitResult.getBlockPos());
        final BlockState blockState = accessor.getLevel().getBlockState(plotPos);
        if (blockState.isAir()) {
            return null;
        }

        final BlockEntity blockEntity = blockState.hasBlockEntity() ? accessor.getLevel().getBlockEntity(plotPos)
                : null;
        if (accessor instanceof BlockAccessor blockAccessor
                && blockAccessor.getPosition().equals(plotPos)
                && blockAccessor.getBlockState() == blockState
                && blockAccessor.getBlockEntity() == blockEntity) {
            return accessor;
        }

        if (accessor instanceof BlockAccessor blockAccessor) {
            return registration.blockAccessor()
                    .from(blockAccessor)
                    .hit(hitResult)
                    .blockState(blockState)
                    .blockEntity(blockEntity)
                    .build();
        }

        return registration.blockAccessor()
                .serverData(accessor.getServerData().copy())
                .serverConnected(accessor.isServerConnected())
                .showDetails(accessor.showDetails())
                .hit(hitResult)
                .blockState(blockState)
                .blockEntity(blockEntity)
                .requireVerification()
                .build();
    }

    private static BlockHitResult retraceSubLevelBlock(final Accessor<?> accessor) {
        final Minecraft minecraft = Minecraft.getInstance();
        final Camera camera = minecraft.gameRenderer.getMainCamera();
        final float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(true);

        final Vec3 eyePosition = Objects.requireNonNull(accessor.getPlayer().getEyePosition(partialTick));
        Vec3 traceStart = Objects.requireNonNull(camera.getPosition());
        double reach = accessor.getPlayer().blockInteractionRange();
        final double cameraOffset = eyePosition.distanceTo(traceStart);
        if (cameraOffset > 1.0E-5D) {
            reach += cameraOffset;
        } else {
            traceStart = eyePosition;
        }

        Vec3 lookVector = new Vec3(Objects.requireNonNull(camera.getLookVector()));
        if (lookVector.lengthSqr() < 1.0E-7D) {
            lookVector = accessor.getPlayer().getViewVector(partialTick);
        }

        final Vec3 normalizedLookVector = Objects.requireNonNull(lookVector.normalize());
        final Vec3 traceOffset = Objects.requireNonNull(normalizedLookVector.scale(reach * 1.001D));
        final Vec3 traceEnd = Objects.requireNonNull(traceStart.add(traceOffset));
        final ClipContext clipContext = new ClipContext(
                traceStart,
                traceEnd,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                Objects.requireNonNull(CollisionContext.of(Objects.requireNonNull(accessor.getPlayer()))));

        if (!(accessor.getLevel() instanceof LevelPoseProviderExtension poseProvider)) {
            final BlockHitResult hitResult = accessor.getLevel().clip(clipContext);
            return hitResult.getType() == HitResult.Type.BLOCK ? hitResult : null;
        }

        poseProvider.sable$pushPoseSupplier(new it.unimi.dsi.fastutil.Function<>() {
            @Override
            public Pose3dc get(final Object key) {
                final SubLevel subLevel = (SubLevel) key;
                if (subLevel instanceof ClientSubLevel clientSubLevel) {
                    return clientSubLevel.renderPose(partialTick);
                }
                return subLevel.logicalPose();
            }
        });

        try {
            final BlockHitResult hitResult = accessor.getLevel().clip(clipContext);
            if (hitResult.getType() != HitResult.Type.BLOCK) {
                return null;
            }

            final Vec3 globalLocation = Objects
                    .requireNonNull(Sable.HELPER.projectOutOfSubLevel(accessor.getLevel(), hitResult.getLocation()));
            return new BlockHitResult(
                    globalLocation,
                    Objects.requireNonNull(hitResult.getDirection()),
                    Objects.requireNonNull(hitResult.getBlockPos()),
                    hitResult.isInside());
        } finally {
            poseProvider.sable$popPoseSupplier();
        }
    }
}

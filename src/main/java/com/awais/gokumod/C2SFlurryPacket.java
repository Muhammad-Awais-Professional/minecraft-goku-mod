package com.awais.gokumod;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class C2SFlurryPacket {
    public C2SFlurryPacket() {}
    public static void encode(C2SFlurryPacket p, net.minecraft.network.FriendlyByteBuf buf) {}
    public C2SFlurryPacket(net.minecraft.network.FriendlyByteBuf buf) {}

    public static void handle(C2SFlurryPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || !player.getPersistentData().getBoolean("is_goku")) return;

            ServerLevel level = player.serverLevel();
            Vec3 look = player.getLookAngle().normalize();

            /* do three shallow boxes, slightly further each time */
            for (int i = 0; i < 3; i++) {
                double dist = 1.2 + i * 0.6;
                AABB area = player.getBoundingBox().inflate(0.8).move(look.scale(dist));
                List<LivingEntity> hits = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);

                hits.forEach(t -> t.hurt(player.damageSources().playerAttack(player), 3f)); // small dmg each tap

                level.sendParticles(ParticleTypes.CRIT,
                        player.getX() + look.x * dist,
                        player.getEyeY() - .2,
                        player.getZ() + look.z * dist,
                        5, 0, 0, 0, .1);  // a few particles
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

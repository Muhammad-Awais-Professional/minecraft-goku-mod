package com.awais.gokumod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SPunchPacket {
    public C2SPunchPacket() {}
    public static void encode(C2SPunchPacket p, net.minecraft.network.FriendlyByteBuf buf) {}
    public C2SPunchPacket(net.minecraft.network.FriendlyByteBuf buf) {}

    public static void handle(C2SPunchPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer p = ctx.get().getSender();
            if (p == null || !p.getPersistentData().getBoolean("is_goku")) return;

            Vec3 look = p.getLookAngle().normalize();
            AABB hitBox = p.getBoundingBox().inflate(1).move(look.scale(2)); // ≈2 blocks ahead

            p.level().getEntitiesOfClass(LivingEntity.class, hitBox, e -> e != p)
                     .forEach(target -> {
                         target.hurt(p.damageSources().playerAttack(p), 8f);
                         target.push(look.x * .5, 0.3, look.z * .5);          // knock-back
                     });

            p.level().addParticle(ParticleTypes.CRIT,                   // little punch spark
                    p.getX() + look.x * 1.5, p.getEyeY(), p.getZ() + look.z * 1.5,
                    0, 0.1, 0);
        });
        ctx.get().setPacketHandled(true);
    }
}

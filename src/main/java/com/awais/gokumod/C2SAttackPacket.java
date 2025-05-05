package com.awais.gokumod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SAttackPacket {
    /* empty payload */
    public C2SAttackPacket() {}
    public static void encode(C2SAttackPacket pkt, net.minecraft.network.FriendlyByteBuf buf) {}

    public C2SAttackPacket(net.minecraft.network.FriendlyByteBuf buf) { /* nothing to read */ }

    public static void handle(C2SAttackPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer p = ctx.get().getSender();
            if (p == null || !p.getPersistentData().getBoolean("is_goku")) return;

            KamehamehaEntity wave = GokuMod.KAME_ENTITY.get().create(p.level());
            if (wave == null) return;
            wave.setItem(GokuMod.KAME_ITEM.get().getDefaultInstance());
            wave.setPos(p.getEyePosition().add(p.getLookAngle().scale(0.5)));
            wave.shoot(p, p.getXRot(), p.getYRot(), 0, 2.5f, 0);
            p.level().addFreshEntity(wave);
        });
        ctx.get().setPacketHandled(true);
    }
}

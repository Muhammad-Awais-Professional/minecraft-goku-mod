package com.awais.gokumod.network;

import com.awais.gokumod.GokuMod;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record ActivateGokuS2C(UUID playerId) {

    /* ----------  codec ---------- */
    public static void encode(ActivateGokuS2C pkt, FriendlyByteBuf buf) {
        buf.writeUUID(pkt.playerId());
    }
    public static ActivateGokuS2C decode(FriendlyByteBuf buf) {
        return new ActivateGokuS2C(buf.readUUID());
    }

    /* ----------  client handler ---------- */
    public static void handle(ActivateGokuS2C pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer cp = Minecraft.getInstance().player;
            if (cp != null && cp.getUUID().equals(pkt.playerId())) {
                cp.getPersistentData().putBoolean(GokuMod.KEY_TAG, true);
                cp.displayClientMessage(Component.literal("Player texture changed successfully!"), true);
                LogUtils.getLogger().info("Goku texture flag set on client");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

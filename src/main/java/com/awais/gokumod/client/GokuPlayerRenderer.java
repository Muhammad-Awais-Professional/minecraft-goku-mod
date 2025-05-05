package com.awais.gokumod.client;

import com.awais.gokumod.GokuMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;

public class GokuPlayerRenderer extends PlayerRenderer {
    public GokuPlayerRenderer(EntityRendererProvider.Context ctx, boolean slim) {
        super(ctx, slim);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
        return player.getPersistentData().getBoolean(GokuMod.KEY_TAG)
                ? GokuMod.GOKU_TEXTURE
                : super.getTextureLocation(player);
    }

    @Override
    public void render(AbstractClientPlayer p, float yaw, float pt,
                       PoseStack pose, MultiBufferSource buf, int light) {
        super.render(p, yaw, pt, pose, buf, light);
    }
}

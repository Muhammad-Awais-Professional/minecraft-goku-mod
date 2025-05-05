package com.awais.gokumod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class KamehamehaEntity extends ThrowableItemProjectile {
    public KamehamehaEntity(EntityType<? extends KamehamehaEntity> type, Level lvl) {
        super(type, lvl);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        hit.getEntity().hurt(damageSources().magic(), 10f);
        if (!level().isClientSide) discard();
    }

    @Override protected Item getDefaultItem() { return GokuMod.KAME_ITEM.get(); }
    @Override protected void defineSynchedData() {}
    @Override public void readAdditionalSaveData(CompoundTag tag) {}
    @Override public void addAdditionalSaveData(CompoundTag tag) {}

    /* Forge networking */
    @Override public Packet<?> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}

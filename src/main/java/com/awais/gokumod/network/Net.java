package com.awais.gokumod.network;

import com.awais.gokumod.GokuMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.SimpleChannel;

public class Net {
    private static final String PROTO = "1";
    public  static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GokuMod.MODID, "main"),
            () -> PROTO, PROTO::equals, PROTO::equals);

    private static int id = 0;
    public static void init() {
        CHANNEL.registerMessage(id++, ActivateGokuS2C.class,
                ActivateGokuS2C::encode,
                ActivateGokuS2C::decode,
                ActivateGokuS2C::handle);
    }
}

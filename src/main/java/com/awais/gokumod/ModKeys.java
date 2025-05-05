package com.awais.gokumod;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/** All key-bindings used by the mod */
public final class ModKeys {
    public static final KeyMapping KAME_KEY = new KeyMapping(
            "key.gokumod.kame", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J, "key.categories.gokumod");

    public static final KeyMapping PUNCH_KEY = new KeyMapping(
            "key.gokumod.punch", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K, "key.categories.gokumod");

    public static final KeyMapping FLURRY_KEY = new KeyMapping(
            "key.gokumod.flurry", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_L, "key.categories.gokumod");

    public static void registerAll() {
        net.minecraftforge.client.ClientRegistry.registerKeyBinding(KAME_KEY);
        net.minecraftforge.client.ClientRegistry.registerKeyBinding(PUNCH_KEY);
        net.minecraftforge.client.ClientRegistry.registerKeyBinding(FLURRY_KEY);
    }
}

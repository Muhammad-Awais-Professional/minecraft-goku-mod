package com.awais.gokumod;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ModKeys {
    public static final KeyMapping KAME_KEY = new KeyMapping(
            "key.gokumod.kame", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J, "key.categories.gokumod");
}

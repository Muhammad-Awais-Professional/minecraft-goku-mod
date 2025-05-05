package com.awais.gokumod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.*;

import org.slf4j.Logger;

@Mod(GokuMod.MODID)
public class GokuMod {
    public static final String MODID = "gokumod";
    private static final Logger LOGGER = LogUtils.getLogger();

    /* ---------- REGISTRIES ---------- */
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item>  ITEMS  = DeferredRegister.create(ForgeRegistries.ITEMS , MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    /* simple example block you already had */
    public static final RegistryObject<Block> EXAMPLE_BLOCK =
            BLOCKS.register("example_block",
                    () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM =
            ITEMS.register("example_block",
                    () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    /* NEW: the projectile item & entity */
    public static final RegistryObject<Item> KAME_ITEM =
            ITEMS.register("kamehameha_core",
                    () -> new Item(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_COMBAT)));

    public static final RegistryObject<EntityType<KamehamehaEntity>> KAME_ENTITY =
            ENTITIES.register("kamehameha",
                    () -> EntityType.Builder.<KamehamehaEntity>of(KamehamehaEntity::new, MobCategory.MISC)
                            .sized(0.75f, 0.75f)          // hit-box
                            .clientTrackingRange(64)
                            .updateInterval(10)
                            .build(new ResourceLocation(MODID, "kamehameha").toString()));

    /* ---------- NETWORK ---------- */
    private static final String PROTO = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"), () -> PROTO, PROTO::equals, PROTO::equals);

    public GokuMod() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS .register(bus);
        ENTITIES.register(bus);
        bus.addListener(this::clientSetup);

        CHANNEL.registerMessage(0, C2SAttackPacket.class, C2SAttackPacket::encode,
                C2SAttackPacket::new, C2SAttackPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));

        MinecraftForge.EVENT_BUS.register(this);
    }

    /* ---------- CLIENT ONLY ---------- */
    private void clientSetup(final FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            /* register renderer that draws the item sprite */
            net.minecraft.client.renderer.entity.EntityRenderers.register(
                    KAME_ENTITY.get(), ctx -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(ctx, 1, true));

            /* register the J key */
            net.minecraftforge.client.ClientRegistry.registerKeyBinding(ModKeys.KAME_KEY);
        });
    }

    /* ---------- COMMAND & KEY HANDLING ---------- */
    @SubscribeEvent
    public void cmd(RegisterCommandsEvent e) {
        e.getDispatcher().register(
                net.minecraft.commands.Commands.literal("activate-goku")
                        .requires(s -> s.hasPermission(0))
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            p.getPersistentData().putBoolean("is_goku", true);
                            ctx.getSource().sendSuccess(
                                    net.minecraft.network.chat.Component.literal("You can now press J to Kamehameha!"),
                                    false);
                            return 1;
                        }));
    }

    /* every client tick, fire packet if J was pressed */
    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null) return;
        while (ModKeys.KAME_KEY.consumeClick()) {
            CHANNEL.sendToServer(new C2SAttackPacket());
        }
    }
}

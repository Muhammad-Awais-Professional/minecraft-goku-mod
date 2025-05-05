package com.awais.gokumod;

import com.awais.gokumod.network.Net;
import com.awais.gokumod.network.ActivateGokuS2C;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(GokuMod.MODID)
public class GokuMod {

    public static final String MODID   = "gokumod";
    public static final String KEY_TAG = "goku_active";
    public static final Logger LOGGER  = LogUtils.getLogger();

    public static final ResourceLocation GOKU_TEXTURE =
            new ResourceLocation(MODID, "textures/entity/goku.png");

    /* ───────── Registry stuff (unchanged) ───────── */
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item>  ITEMS  =
            DeferredRegister.create(ForgeRegistries.ITEMS , MODID);

    public static final RegistryObject<Block> EXAMPLE_BLOCK =
            BLOCKS.register("example_block",
                    () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM =
            ITEMS.register("example_block",
                    () -> new BlockItem(EXAMPLE_BLOCK.get(),
                            new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    /* ───────── ctor ───────── */
    public GokuMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
        BLOCKS.register(modBus);
        ITEMS .register(modBus);

        MinecraftForge.EVENT_BUS.register(this);                     // Forge-bus events
        modBus.register(Config.class);                               // config
        FMLJavaModLoadingContext.get()
                .registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        Net.init();                                                  // <-- network channel
    }

    /* ───────── COMMON SETUP ───────── */
    private void commonSetup(FMLCommonSetupEvent e) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
        Config.items.forEach(i -> LOGGER.info("ITEM >> {}", i));
    }

    /* ───────── CLIENT-side bootstrap ───────── */
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent e) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        /** Replace vanilla player renderer with our Goku-aware one. */
        @SuppressWarnings({"rawtypes","unchecked"})
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers e) {
            e.registerEntityRenderer(EntityType.PLAYER,
                    (ctx -> new com.awais.gokumod.client.GokuPlayerRenderer(ctx,false)));
            e.registerEntityRenderer(EntityType.PLAYER,
                    (ctx -> new com.awais.gokumod.client.GokuPlayerRenderer(ctx,true )));
        }
    }

    /* ───────── Commands ───────── */
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommandEvents {
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent e) {
            e.getDispatcher().register(
                    Commands.literal("activate-goku")
                            .requires(s -> s.hasPermission(0))
                            .executes(CommandEvents::runActivateGoku)
            );
        }
        private static int runActivateGoku(CommandContext<CommandSourceStack> ctx)
                throws CommandSyntaxException {

            ServerPlayer sp = ctx.getSource().getPlayerOrException();
            sp.sendSystemMessage(Component.literal("Trying to change texture…"));
            sp.sendSystemMessage(Component.literal("File found at "+GOKU_TEXTURE));
            sp.sendSystemMessage(Component.literal("Waiting for client to apply texture…"));

            /* → send packet to *that* client */
            Net.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                    new ActivateGokuS2C(sp.getUUID()));
            LOGGER.info("{} is now flagged as Goku!", sp.getName().getString());
            return 1;
        }
    }
}

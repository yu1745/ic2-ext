package com.example;

import com.example.block.BlockChargeableDirt;
import com.example.gui.GuiHandler;
import com.example.item.ItemDummyLaser;
import com.example.proxy.CommonProxy;
import com.example.tileentity.TileEntityChargeableDirt;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = SampleMod112.MODID,
        name = SampleMod112.NAME,
        version = SampleMod112.VERSION
)
@Mod.EventBusSubscriber
public class SampleMod112 {
    public static final String MODID = "ic2-ext";
    public static final String NAME = "Sample Mod 1.12";
    public static final String VERSION = "1.0";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance
    public static SampleMod112 instance;

    public static final Item DUMMY_LASER = new ItemDummyLaser();
    public static final Block CHARGEABLE_DIRT = new BlockChargeableDirt();

    @SidedProxy(clientSide = "com.example.proxy.ClientProxy", serverSide = "com.example.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static CreativeTabs creativeTab = new CreativeTabs("ic2_ext") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(DUMMY_LASER);
        }

//        @Override
//		public ItemStack getTabIconItem() {
//			return new ItemStack(DUMMY_LASER);
//		}
    };

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent preinit) {
        LOGGER.info("Hello, world!");

        // 注册TileEntity
        GameRegistry.registerTileEntity(TileEntityChargeableDirt.class, "chargeable_dirt");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        instance = this;
        proxy.registerItemRenderer(DUMMY_LASER, 0, "dummy_laser");

        // 注册GUI处理器
        NetworkRegistry.INSTANCE.registerGuiHandler(SampleMod112.instance, new GuiHandler());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        CHARGEABLE_DIRT.setCreativeTab(creativeTab);
        event.getRegistry().register(CHARGEABLE_DIRT);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        DUMMY_LASER.setCreativeTab(creativeTab);
        event.getRegistry().register(DUMMY_LASER);
        event.getRegistry().register(new ItemBlock(CHARGEABLE_DIRT).setRegistryName(CHARGEABLE_DIRT.getRegistryName()));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        proxy.registerItemRenderer(DUMMY_LASER, 0, "dummy_laser");
        proxy.registerItemRenderer(Item.getItemFromBlock(CHARGEABLE_DIRT), 0, "chargeable_dirt");
    }

}

package wangyu;

import org.jetbrains.annotations.NotNull;
import wangyu.block.BlockChargeableDirt;
import wangyu.block.BlockChargeableDirt2;
import wangyu.gui.GuiHandler;
import wangyu.item.ItemDummyLaser;
import wangyu.proxy.CommonProxy;
import wangyu.tileentity.TileEntityChargeableDirt;
import wangyu.tileentity.TileEntityChargeableDirt2;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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

    //mod icon
    public static final Item DUMMY_LASER = new ItemDummyLaser();
//    public static final Block CHARGEABLE_DIRT = new BlockChargeableDirt();
//    public static final Block CHARGEABLE_DIRT2 = new BlockChargeableDirt2();

    @SidedProxy(clientSide = "wangyu.proxy.ClientProxy", serverSide = "wangyu.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static CreativeTabs creativeTab = new CreativeTabs("ic2_ext") {
        @NotNull
        @Override
        public ItemStack createIcon() {
            return new ItemStack(DUMMY_LASER);
        }
    };

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent preinit) {
        LOGGER.info("Hello, world!");
        // 加载ModRegistry并执行注册
        ModRegistry.Companion.registerTileEntities();
//        LOGGER.info("ModRegistry loaded and tile entities registered");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        instance = this;

        // 自动注册所有物品渲染器
        ModRegistry.Companion.registerItemRenderers();

        // 注册GUI处理器
        NetworkRegistry.INSTANCE.registerGuiHandler(SampleMod112.instance, new GuiHandler());
    }

//    @SubscribeEvent
//    public static void registerBlocks(RegistryEvent.Register<Block> event) {
//        CHARGEABLE_DIRT.setCreativeTab(creativeTab);
//        event.getRegistry().register(CHARGEABLE_DIRT);
//        CHARGEABLE_DIRT2.setCreativeTab(creativeTab);
//        event.getRegistry().register(CHARGEABLE_DIRT2);
//    }
//
//    @SubscribeEvent
//    public static void registerItems(RegistryEvent.Register<Item> event) {
//        DUMMY_LASER.setCreativeTab(creativeTab);
//        event.getRegistry().register(DUMMY_LASER);
//        event.getRegistry().register(new ItemBlock(CHARGEABLE_DIRT).setRegistryName(CHARGEABLE_DIRT.getRegistryName()));
//        event.getRegistry().register(new ItemBlock(CHARGEABLE_DIRT2).setRegistryName(CHARGEABLE_DIRT2.getRegistryName()));
//
//    }
//
//    @SubscribeEvent
//    public static void registerModels(ModelRegistryEvent event) {
//        proxy.registerItemRenderer(DUMMY_LASER, 0, "dummy_laser");
//        proxy.registerItemRenderer(Item.getItemFromBlock(CHARGEABLE_DIRT), 0, "chargeable_dirt");
//        proxy.registerItemRenderer(Item.getItemFromBlock(CHARGEABLE_DIRT2), 0, "chargeable_dirt2");
//
//    }

}

package wangyu

import net.minecraft.init.Items
import wangyu.gui.GuiHandler
import wangyu.item.ItemDummyLaser
import wangyu.proxy.CommonProxy
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.Optional

@Mod(
    modid = SampleMod112.MODID,
    name = SampleMod112.NAME,
    version = SampleMod112.VERSION
)
@Mod.EventBusSubscriber
class SampleMod112 {
    companion object {
        const val MODID = "ic2-ext"
        const val NAME = "Sample Mod 1.12"
        const val VERSION = "1.0"

        val LOGGER: Logger = LogManager.getLogger(MODID)

        @Mod.Instance
        lateinit var instance: SampleMod112

        @SidedProxy(clientSide = "wangyu.proxy.ClientProxy", serverSide = "wangyu.proxy.CommonProxy")
        lateinit var proxy: CommonProxy

        val creativeTab: CreativeTabs = object : CreativeTabs(MODID + "-tab") {
            override fun createIcon(): ItemStack {
                LOGGER.info("createIcon()")
                return ItemStack(
                    Optional.ofNullable(ModRegistry.getItemByClass(ItemDummyLaser::class.java)).orElse(Items.AIR)
                )
            }
        }
    }

    @Mod.EventHandler
    fun preinit(preinit: FMLPreInitializationEvent) {
        LOGGER.info("Hello, world!")
        // 加载ModRegistry并执行注册
        ModRegistry.registerTileEntities()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        instance = this

        // 自动注册所有物品渲染器
        ModRegistry.registerItemRenderers()

        // 注册GUI处理器
        NetworkRegistry.INSTANCE.registerGuiHandler(SampleMod112.instance, GuiHandler())
    }
}
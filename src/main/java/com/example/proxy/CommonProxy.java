package com.example.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommonProxy {

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        // 物品注册已在主模组类中完成
    }

    public void registerItemRenderer(Item item, int meta, String id) {
        // 客户端专用，在CommonProxy中为空
    }
}
package com.example.item;

import com.example.SampleMod112;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemDummyLaser extends Item {

    public ItemDummyLaser() {
        setMaxStackSize(1);
        setTranslationKey("dummy_laser");
        setRegistryName("dummy_laser");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            // 仅在聊天框显示消息，没有实际作用
            player.sendMessage(new net.minecraft.util.text.TextComponentString("这是一个虚拟镭射枪，没有实际功能"));
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
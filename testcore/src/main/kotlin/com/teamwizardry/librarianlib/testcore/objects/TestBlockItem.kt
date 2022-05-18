package com.teamwizardry.librarianlib.testcore.objects

import com.teamwizardry.librarianlib.core.util.kotlin.translationKey
import net.minecraft.block.BlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.BlockItem
import net.minecraft.item.BlockItemUseContext
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World

public open class TestBlockItem(block: TestBlock, builder: Properties): BlockItem(block, builder) {
    override fun getBlock(): TestBlock {
        return super.getBlock() as TestBlock
    }

    override fun placeBlock(context: BlockItemUseContext, state: BlockState): Boolean {
        if (super.placeBlock(context, state)) {
            context.itemInHand.grow(1)
            return true
        }
        return false
    }

    public override fun appendHoverText(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn)
        if (block.config.description != null) {
            val description = TranslationTextComponent(registryName!!.translationKey("block", "tooltip"))
            description.style.applyFormat(TextFormatting.GRAY)
            tooltip.add(description)
        }
    }
}
package com.teamwizardry.librarianlib.testcore.objects

import com.teamwizardry.librarianlib.core.util.kotlin.translationKey
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.item.UseAction
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraftforge.fml.ModLoadingContext

public open class TestItem(public val config: TestItemConfig): Item(config.properties), ITestItem {
    init {
        this.registryName = ResourceLocation(ModLoadingContext.get().activeContainer.modId, config.id)
    }

    override fun appendHoverText(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn)
        if (config.description != null) {
            val description = TranslationTextComponent(registryName!!.translationKey("item", "tooltip"))
            description.style.applyFormat(TextFormatting.GRAY)
            tooltip.add(description)
        }
    }

    override fun use(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        var used = false
        if (config.rightClickHoldDuration != 0) {
            playerIn.swingingArm = handIn
            used = true
        }

        val context = TestItemConfig.RightClickContext(worldIn, playerIn, handIn)

        config.rightClick.run(worldIn.isClientSide, context)
        config.rightClickAir.run(worldIn.isClientSide, context)
        if (config.rightClick.exists || config.rightClickAir.exists)
            used = true

        return if (used) {
            ActionResult(ActionResultType.SUCCESS, playerIn.getItemInHand(handIn))
        } else {
            ActionResult(ActionResultType.PASS, playerIn.getItemInHand(handIn))
        }
    }

    override fun useOn(context: ItemUseContext): ActionResultType {
        var result = ActionResultType.PASS

        if (context.player === null) return result

        val clickContext = TestItemConfig.RightClickContext(context.level, context.player!!, context.hand)
        val clickBlockContext = TestItemConfig.RightClickBlockContext(context)

        config.rightClick.run(context.level.isClientSide, clickContext)
        config.rightClickBlock.run(context.level.isClientSide, clickBlockContext)
        if (config.rightClick.exists || config.rightClickBlock.exists)
            result = ActionResultType.SUCCESS
        return result
    }

    override fun getUseDuration(stack: ItemStack): Int {
        return config.rightClickHoldDuration
    }

    override fun getUseAnimation(stack: ItemStack): UseAction {
        if (config.rightClickHoldDuration != 0) {
            return UseAction.BOW
        }
        return UseAction.NONE
    }

    override fun onUsingTick(stack: ItemStack, player: LivingEntity, count: Int) {
        if (player !is PlayerEntity) return

        val context = TestItemConfig.RightClickHoldContext(stack, player, count)

        config.rightClickHold.run(player.level.isClientSide, context)
    }

    override fun releaseUsing(stack: ItemStack, worldIn: World, entityLiving: LivingEntity, timeLeft: Int) {
        if (entityLiving !is PlayerEntity) return

        val context = TestItemConfig.RightClickReleaseContext(stack, worldIn, entityLiving, timeLeft)

        config.rightClickRelease.run(worldIn.isClientSide, context)
    }

    override fun onBlockStartBreak(itemstack: ItemStack, pos: BlockPos, player: PlayerEntity): Boolean {
        val context = TestItemConfig.LeftClickBlockContext(itemstack, pos, player)
        config.leftClickBlock.run(player.level.isClientSide, context)

        return super<Item>.onBlockStartBreak(itemstack, pos, player)
    }

    override fun onLeftClickEntity(stack: ItemStack, player: PlayerEntity, entity: Entity): Boolean {
        val context = TestItemConfig.LeftClickEntityContext(stack, player, entity)
        config.leftClickEntity.run(player.level.isClientSide, context)
        return config.leftClickEntity.exists
    }

    override fun interactLivingEntity(stack: ItemStack, playerIn: PlayerEntity, target: LivingEntity, hand: Hand): ActionResultType {
        val context = TestItemConfig.RightClickEntityContext(stack, playerIn, target, hand)
        val clickContext = TestItemConfig.RightClickContext(playerIn.level, playerIn, hand)

        config.rightClickEntity.run(playerIn.level.isClientSide, context)
        config.rightClick.run(playerIn.level.isClientSide, clickContext)

        return if(config.rightClickEntity.exists) ActionResultType.SUCCESS else ActionResultType.PASS
    }

    override fun inventoryTick(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if (entityIn !is PlayerEntity) return

        val context = TestItemConfig.InventoryTickContext(stack, worldIn, entityIn, itemSlot, isSelected)
        config.inventoryTick.run(worldIn.isClientSide, context)

        if (isSelected) {
            config.tickInHand.run(worldIn.isClientSide, context)
        }
    }

    override val itemName: String
        get() = config.name
    override val itemDescription: String?
        get() = config.description
}

package com.teamwizardry.librarianlib.facade.container.builtin

import com.teamwizardry.librarianlib.facade.container.slot.CustomClickSlot
import com.teamwizardry.librarianlib.facade.container.slot.FacadeSlot
import com.teamwizardry.librarianlib.facade.container.transfer.TransferSlot
import com.teamwizardry.librarianlib.facade.container.transfer.TransferState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.ClickType
import net.minecraft.inventory.container.Container
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import kotlin.math.min

public open class GhostSlot(itemHandler: IItemHandler, index: Int) : FacadeSlot(itemHandler, index), CustomClickSlot, TransferSlot {
    /**
     * Whether to disable JEI ghost slot integration
     */
    public var disableJeiGhostIntegration: Boolean = false

    override fun getMaxStackSize(): Int {
        return 1
    }

    protected open fun acceptGhostStack(stack: ItemStack) {
        val ghostStack = stack.copy()
        ghostStack.count = min(maxStackSize, ghostStack.count)
        this.set(ghostStack)
    }

    override fun handleClick(
        container: Container,
        mouseButton: Int,
        clickType: ClickType,
        player: PlayerEntity
    ): ItemStack? {
        val ghostStack = player.inventory.carried
        val isValid = mayPlace(ghostStack)

        when(clickType) {
            ClickType.PICKUP -> {
                if(mouseButton == 0 && isValid) {
                    acceptGhostStack(ghostStack)
                }
                if(mouseButton == 1) {
                    acceptGhostStack(ItemStack.EMPTY)
                }
            }
            else -> {}
        }
        return ItemStack.EMPTY
    }

    override fun isStackSimilar(stack: ItemStack): Boolean = Container.consideredTheSameItem(stack, this.item)

    override fun transferIntoSlot(transfer: TransferState) {
        if(isStackSimilar(transfer.stack)) {
            transfer.foundSpot = true
            transfer.halt = true
        }
        if(this.item.isEmpty && mayPlace(transfer.stack)) {
            acceptGhostStack(transfer.stack)
            transfer.foundSpot = true
            transfer.halt = true
        }
    }

    public open fun acceptJeiGhostStack(stack: ItemStack) {
        if(mayPlace(stack)) {
            acceptGhostStack(stack)
        }
    }
}